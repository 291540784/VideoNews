package com.feicuiedu.videonews.videoplayer.part;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.feicuiedu.videonews.videoplayer.R;
import com.feicuiedu.videonews.videoplayer.full.VideoViewActivity;

import java.io.IOException;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;

public class SimpleVideoView extends FrameLayout {

    private static final int PROGRESS_MAX = 1000;

    private String videoPath; // 视频播放URL
    private MediaPlayer mediaPlayer;
    private boolean isPrepared; // 是否已准备好
    private boolean isPlaying; // 是否正在播放

    // 视图相关start-------------------
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private ImageView ivPreview;
    private ImageButton btnToggle;
    private ProgressBar progressBar;
    // 视图相关end-------------------

    public SimpleVideoView(Context context) {
        this(context, null);
    }

    public SimpleVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Handler handler = new Handler() {
        @Override public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isPlaying) {
                // 每200毫秒更新一次播放进度
                int progress = (int) (mediaPlayer.getCurrentPosition() * PROGRESS_MAX / mediaPlayer.getDuration());
                progressBar.setProgress(progress);
                handler.sendEmptyMessageDelayed(0, 200);
            }
        }
    };

    private void init() {
        // Vitamio的初始化
        Vitamio.isInitialized(getContext());
        // inflate
        LayoutInflater.from(getContext()).inflate(R.layout.view_simple_video_player, this, true);
        // 初始化SurfaceView
        initSurfaceView();
        // 初始化视频播放控制视图
        initControllerViews();
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public void onResume() {
        initMediaPlayer(); // 初始化MediaPlayer，设置一系列监听器
        prepareMediaPlayer(); // 准备MediaPlayer，同时更新UI状态
    }

    public void onPause() {
        pauseMediaPlayer(); // 暂停播放，同时更新UI状态
        releaseMediaPlayer(); // 释放MediaPlayer，同时更新UI状态
    }

    private void initSurfaceView() {
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        // 注意：vitamio使用SurfaceView播放时要设置fixelFormat
        surfaceHolder.setFormat(PixelFormat.RGBA_8888);
    }

    private void initControllerViews() {
        // 预览图
        ivPreview = (ImageView) findViewById(R.id.ivPreview);
        // 播放、暂停
        btnToggle = (ImageButton) findViewById(R.id.btnToggle);
        btnToggle.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    pauseMediaPlayer();
                } else if (isPrepared) {
                    startMediaPlayer();
                } else {
                    Toast.makeText(getContext(), "Can't play now!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // 设置进度条
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(PROGRESS_MAX);
        // 全屏播放按钮
        ImageButton btnFullScreen = (ImageButton) findViewById(R.id.btnFullScreen);
        btnFullScreen.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                VideoViewActivity.open(getContext(), videoPath);
            }
        });
    }

    // 初始化MediaPlayer，设置一系列监听器
    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer(getContext());
        mediaPlayer.setDisplay(surfaceHolder);
        // 监听处理
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override public void onPrepared(MediaPlayer mp) {
                isPrepared = true;
                startMediaPlayer();
            }
        });
        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override public boolean onInfo(MediaPlayer mp, int what, int extra) {
                // vitamio5.0，要进行audio处理,才能对在线视频进行播放
                if (what == MediaPlayer.MEDIA_INFO_FILE_OPEN_OK) {
                    mediaPlayer.audioInitedOk(mediaPlayer.audioTrackInit());
                    return true;
                }
                return false;
            }
        });
        mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                int layoutWidth = surfaceView.getWidth();
                int layoutHeigth = layoutWidth * height / width;
                // 更新surfaceView的size
                ViewGroup.LayoutParams layoutParams = surfaceView.getLayoutParams();
                layoutParams.width = layoutWidth;
                layoutParams.height = layoutHeigth;
                surfaceView.setLayoutParams(layoutParams);
            }
        });
    }

    // 开始播放，同时更新UI状态
    private void startMediaPlayer() {
        ivPreview.setVisibility(View.INVISIBLE);
        btnToggle.setImageResource(R.drawable.ic_pause);
        mediaPlayer.start();
        isPlaying = true;
        handler.sendEmptyMessage(0);
    }

    private void prepareMediaPlayer() {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(videoPath);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepareAsync();
            //
            ivPreview.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            Log.d("SimpleVideoView", " prepare MediaPlayer " + e.getMessage());
        }
    }

    // 暂停播放，同时更新UI状态
    private void pauseMediaPlayer() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        isPlaying = false;
        btnToggle.setImageResource(R.drawable.ic_play_arrow);
        handler.removeMessages(0);
    }

    // 释放MediaPlayer，同时更新UI状态
    private void releaseMediaPlayer() {
        mediaPlayer.release();
        mediaPlayer = null;
        isPrepared = false;
        progressBar.setProgress(0);
    }
}