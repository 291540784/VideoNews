package com.feicuiedu.videonews;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.TextureView;

import com.feicuiedu.videonews.videoplayer.list.MediaPlayerManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

// 只是演示使用,无法运用
public class TestActivity extends AppCompatActivity implements MediaPlayerManager.OnPlaybackListener, TextureView.SurfaceTextureListener {

    @BindView(R.id.textureView) TextureView textureView;
    private MediaPlayerManager mediaPlayerManager;
    private Surface surface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        textureView.setSurfaceTextureListener(this);
        mediaPlayerManager = MediaPlayerManager.getsInstance(this);
    }

    @Override protected void onResume() {
        super.onResume();
        mediaPlayerManager.onResume();
    }

    @Override protected void onPause() {
        super.onPause();
        mediaPlayerManager.onPause();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        mediaPlayerManager.removeAllListeners();
    }

    @OnClick(R.id.textureView)
    public void clickView() {
        if (surface == null) return;
        String path = "";
        String videoId = "";
        mediaPlayerManager.startPlayer(surface, path, videoId);
    }

    // PlaybackListener -- start
    @Override public void onStartBuffering(String videoId) {

    }

    @Override public void onStopBuffering(String videoId) {

    }

    @Override public void onStartPlay(String videoId) {

    }

    @Override public void onStopPlay(String videoId) {

    }

    @Override public void onSizeMeasured(String videoId, int width, int height) {

    }
    // PlaybackListener -- start

    // SurfaceTextureListener -- start
    @Override public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        this.surface = new Surface(surfaceTexture);
    }

    @Override public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        this.surface.release();
        this.surface = null;
        mediaPlayerManager.stopPlayer();
        return false;
    }

    @Override public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }
    // SurfaceTextureListener -- end
}
