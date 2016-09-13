package com.feicuiedu.videonews.ui.local;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.UiThread;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.feicuiedu.videonews.R;
import com.feicuiedu.videonews.videoplayer.full.VideoViewActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：yuanchao on 2016/9/9 0009 14:38
 * 邮箱：yuanchao@feicuiedu.com
 */
public class LocalVideoItem extends FrameLayout {

    @BindView(R.id.ivPreview) ImageView ivPreView;
    @BindView(R.id.tvVideoName) TextView tvVideoName;
    private String filePath; // 文件路径

    public LocalVideoItem(Context context) {
        this(context, null, 0);
    }

    public LocalVideoItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocalVideoItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, 0);
        init();
    }

    public String getFilePath() {
        return filePath;
    }

    @UiThread
    public void setIvPreView(Bitmap bitmap){
        ivPreView.setImageBitmap(bitmap);
    }

    // 设置预览图像，可在后台线程执行此方法
    public void setIvPreView(final String filePath, final Bitmap bitmap) {
        if (!filePath.equals(this.filePath)) return;
        post(new Runnable() {
            @Override public void run() {
                if (!filePath.equals(LocalVideoItem.this.filePath)) return;
                ivPreView.setImageBitmap(bitmap);
            }
        });
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.item_local_video, this, true);
        ButterKnife.bind(this);
    }

    /**
     * 数据绑定(将cursor内容,设置到对应控件上)
     */
    public void bind(Cursor cursor) {
        // 取出文件路径
        filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        // 取出视频名称
        String videoName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
        tvVideoName.setText(videoName);
        ivPreView.setImageBitmap(null);
        // 清除预览图

        // 获取视频的预览图，是一个很费时的操作
        // ------ 到后台线程执行

        // 同时会去获取多张预览图
        // ------ 线程池处理

        // 已获取过的图像要做缓存
        // ------ LruCache
        // Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND);
        // ivPreView.setImageBitmap(bitmap);
    }

    // click当前控件,全屏播放
    @OnClick
    public void click() {
        VideoViewActivity.open(getContext(), filePath);
    }
}