package com.feicuiedu.videonews.ui.local;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 作者：yuanchao on 2016/9/9 0009 14:32
 * 邮箱：yuanchao@feicuiedu.com
 */
public class LocalVideoAdapter extends CursorAdapter{

    // 用来加载视频预览图的线程池
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    // 用来缓存已加载过的预览图像
    private LruCache<String,Bitmap> lruCache = new LruCache<String,Bitmap>(5 * 1024 * 1024){
        @Override protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount();
        }
    };

    public LocalVideoAdapter(Context context) {
        super(context, null, true);
    }

    @Override public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return new LocalVideoItem(context);
    }

    @Override public void bindView(View view, Context context, Cursor cursor) {
        final LocalVideoItem localVideoItem = (LocalVideoItem) view;
        localVideoItem.bind(cursor);
        // 从缓存中获取预览图
        final String filePath = localVideoItem.getFilePath();
        Bitmap bitmap = lruCache.get(filePath);
        if(bitmap != null){
            localVideoItem.setIvPreView(bitmap);
            return;
        }
        // 后台线程获取视频预览图像
        executorService.submit(new Runnable() {
            @Override public void run() {
                // 加载视频的预览图像
                Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND);
                // 缓存当前预览图像,文件路径做为key
                lruCache.put(filePath, bitmap);
                // 将图像设置到控件上
                // 注意：当前是在后台线程内
                localVideoItem.setIvPreView(filePath, bitmap);
            }
        });
    }

    public void release(){
        executorService.shutdown();
    }
}
