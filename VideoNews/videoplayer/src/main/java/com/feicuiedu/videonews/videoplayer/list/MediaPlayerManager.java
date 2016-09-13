package com.feicuiedu.videonews.videoplayer.list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Surface;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;

public class MediaPlayerManager {
    private static MediaPlayerManager sInstance;

    public synchronized static MediaPlayerManager getsInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MediaPlayerManager(context.getApplicationContext());
        }
        return sInstance;
    }

    private MediaPlayerManager(Context context) {
        this.context = context;
        onPlaybackListeners = new ArrayList<>();
        Vitamio.isInitialized(context);
    }

    private final Context context;
    private MediaPlayer mediaPlayer;
    private List<OnPlaybackListener> onPlaybackListeners;
    private boolean needRelease = false; // 是否需要释放(如果还没有资源的话,release可能出现空指针情况)
    private String videoId; // 视频ID(用来区分当前在操作谁)

    // 初始化MediaPlayer
    public void onResume() {
        mediaPlayer = new MediaPlayer(context);
        // 监听Prepared - 设置缓冲空间大小
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override public void onPrepared(MediaPlayer mp) {
                mediaPlayer.setBufferSize(512 * 1024);
                mediaPlayer.start();
            }
        });
        // 监听Completion - 播放到最后，停止播放且通知UI
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override public void onCompletion(MediaPlayer mp) {
                stopPlayer();
            }
        });
        // 监听VideoSizeChanged - 更新UI
        mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                if (width == 0 || height == 0) return;
                changVideoSize(width, height);
            }
        });
        // 监听Info - 缓冲状态处理且更新UI
        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_FILE_OPEN_OK:
                        // vitamio要做音频初始处理
                        mediaPlayer.audioInitedOk(mediaPlayer.audioTrackInit());
                        return true;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        startBuffering();
                        return true;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        endBuffering();
                        return true;
                }
                return false;
            }
        });
    }

    public String getVideoId() {
        return videoId;
    }

    // 释放MediaPlayer
    public void onPause() {
        stopPlayer();
        if (needRelease) {
            mediaPlayer.release();
        }
        mediaPlayer = null;
    }

    // 开始缓冲,且更新UI(通过接口callback)
    private void startBuffering() {
        if (mediaPlayer.isPlaying()) mediaPlayer.pause();
        // 通知UI更新
        for (OnPlaybackListener listener : onPlaybackListeners) {
            listener.onStartBuffering(videoId);
        }
    }

    // 结束缓冲,且更新UI(通过接口callback)
    private void endBuffering() {
        mediaPlayer.start();
        // 通知UI更新
        for (OnPlaybackListener listener : onPlaybackListeners) {
            listener.onStopBuffering(videoId);
        }
    }

    // 调整更改视频尺寸
    private void changVideoSize(final int width, final int height) {
        // 通知UI更新
        for (OnPlaybackListener listener : onPlaybackListeners) {
            listener.onSizeMeasured(videoId, width, height);
        }
    }

    private long startTime;

    // 开始播放,且更新UI(通过接口callback)
    public void startPlayer(
            @NonNull Surface surface,
            @NonNull String path,
            @NonNull String videoId) {
        // 避免过于频繁操作开和关
        if (System.currentTimeMillis() - startTime < 300) return;
        startTime = System.currentTimeMillis();
        // 当前有其它视频存在
        if (this.videoId != null) {
            stopPlayer();
        }
        // 更新当前视频ID
        this.videoId = videoId;
        // 通知UI更新
        for (OnPlaybackListener listener : onPlaybackListeners) {
            listener.onStartPlay(videoId);
        }
        // 准备播放
        try {
            mediaPlayer.setDataSource(path);
            needRelease = true;
            mediaPlayer.setSurface(surface);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    // 停止播放,且更新UI(通过接口callback)
    public void stopPlayer() {
        if (videoId == null) return;
        // 通知UI更新
        for (OnPlaybackListener listener : onPlaybackListeners) {
            listener.onStopPlay(videoId);
        }
        this.videoId = null;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
    }

    // 添加播放处理的监听(UI层的callback)
    public void addPlayerbackListener(OnPlaybackListener listener) {
        onPlaybackListeners.add(listener);
    }

    public void removeAllListeners() {
        onPlaybackListeners.clear();
    }

    // 视图接口
    // 在视频播放模块完成播放处理, 视图层来实现此接口, 完成视图层UI更新
    public interface OnPlaybackListener {

        void onStartBuffering(String videoId); // 视频缓冲开始

        void onStopBuffering(String videoId); // 视频缓冲结束

        void onStartPlay(String videoId); // 开始播放

        void onStopPlay(String videoId);// 停止播放

        void onSizeMeasured(String videoId, int width, int height);// 大小更改
    }
}