package com.feicuiedu.videonews.ui.news;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.feicuiedu.videonews.R;
import com.feicuiedu.videonews.bombapi.entity.NewsEntity;
import com.feicuiedu.videonews.commons.CommonUtils;
import com.feicuiedu.videonews.ui.base.BaseItemView;
import com.feicuiedu.videonews.ui.comments.CommentsActivity;
import com.feicuiedu.videonews.videoplayer.list.MediaPlayerManager;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 新闻列表的单项视图, 将使用 MediaPlayer播放视频,TextureView来显示视频
 * <p>
 * 作者：yuanchao on 2016/8/17 0017 14:51
 * 邮箱：yuanchao@feicuiedu.com
 */
public class NewsItemView extends BaseItemView<NewsEntity> implements
        TextureView.SurfaceTextureListener,
        MediaPlayerManager.OnPlaybackListener {
    @BindView(R.id.textureView) TextureView textureView; // 用来展现视频的TextureView
    @BindView(R.id.ivPreview) ImageView ivPreview;
    @BindView(R.id.tvNewsTitle) TextView tvNewsTitle;
    @BindView(R.id.tvCreatedAt) TextView tvCreatedAt;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.ivPlay) ImageView ivPlay;

    private NewsEntity newsEntity;

    private MediaPlayerManager mediaPlayerManager;

    private Surface surface;

    public NewsItemView(Context context) {
        super(context);
    }

    @Override protected void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.item_news, this, true);
        ButterKnife.bind(this);
        //
        mediaPlayerManager = MediaPlayerManager.getsInstance(getContext());
        mediaPlayerManager.addPlayerbackListener(this);
        //
        textureView.setSurfaceTextureListener(this);
    }

    @Override protected void bindModel(NewsEntity newsEntity) {
        this.newsEntity = newsEntity;
        // 初始视图状态
        tvNewsTitle.setVisibility(View.VISIBLE);
        ivPreview.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        ivPlay.setVisibility(View.VISIBLE);
        // 设置标题,创建时间和预览图
        tvNewsTitle.setText(newsEntity.getNewsTitle());
        tvNewsTitle.setText(newsEntity.getNewsTitle());
        tvCreatedAt.setText(CommonUtils.format(newsEntity.getCreatedAt()));
        // 设置预览图像
        String url = CommonUtils.encodeUrl(newsEntity.getPreviewUrl());
        Picasso.with(getContext()).load(url).into(ivPreview);
    }

    @OnClick(R.id.tvCreatedAt)
    public void navigateToComments() {
        CommentsActivity.open(getContext(), newsEntity);
    }

    // 单击预览图像 - 开始播放
    @OnClick(R.id.ivPreview)
    public void startPlayer() {
        if (surface == null) return;
        String path = newsEntity.getVideoUrl();
        String videoId = newsEntity.getObjectId();
        mediaPlayerManager.startPlayer(surface, path, videoId);
    }

    // 单击视频 - 停止播放
    @OnClick(R.id.textureView)
    public void stopPlayer() {
        mediaPlayerManager.stopPlayer();
    }

    // SurfaceTextureListener - start
    @Override public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        surface = new Surface(surfaceTexture);
    }

    @Override public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
    }

    @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        this.surface.release();
        this.surface = null;
        // 停止自己
        if (newsEntity.getObjectId().equals(mediaPlayerManager.getVideoId())) {
            mediaPlayerManager.stopPlayer();
        }
        return false;
    }

    @Override public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
    // SurfaceTextureListener - end

    private boolean isCurrentVideo(String videoId) {
        if (videoId == null || newsEntity == null) return false;
        return videoId.equals(newsEntity.getObjectId());
    }

    // start
    @Override public void onStartBuffering(String videoId) {
        if (isCurrentVideo(videoId)) {
            // 将当前视频的ProgressBar显示出来
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override public void onStopBuffering(String videoId) {
        if (isCurrentVideo(videoId)) {
            // 将当前视频的ProgressBar隐藏
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override public void onStartPlay(String videoId) {
        if (isCurrentVideo(videoId)) {
            // 开始准备播放
            ivPreview.setVisibility(View.INVISIBLE);
            tvNewsTitle.setVisibility(View.INVISIBLE);
            ivPlay.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override public void onStopPlay(String videoId) {
        if (isCurrentVideo(videoId)) {
            // 停止播放
            ivPreview.setVisibility(View.VISIBLE);
            tvNewsTitle.setVisibility(View.VISIBLE);
            ivPlay.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override public void onSizeMeasured(String videoId, int width, int height) {
        if (isCurrentVideo(videoId)) {

        }
    }
    // end
}