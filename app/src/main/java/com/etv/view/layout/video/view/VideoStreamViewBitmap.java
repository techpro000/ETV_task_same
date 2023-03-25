package com.etv.view.layout.video.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.etv.listener.TaskPlayStateListener;
import com.etv.listener.VideoPlayListener;
import com.etv.task.entity.CpListEntity;
import com.etv.util.MyLog;
import com.ys.etv.R;

import java.io.IOException;

/***
 * 系统得
 * mediaPlayer
 */
public class VideoStreamViewBitmap extends RelativeLayout implements
        OnPreparedListener, MediaPlayer.OnErrorListener {
    View view;
    private MediaPlayer mMediaPlayer;
    private Surface surface;
    private TextureView textureView;
    VideoPlayListener listener;
    Context context;
    CpListEntity cpListEntity;
    TaskPlayStateListener taskPlayStateListener;

    public void setVideoPlayListener(VideoPlayListener listener, TaskPlayStateListener taskPlayStateListener, CpListEntity cpListEntity) {
        this.listener = listener;
        this.taskPlayStateListener = taskPlayStateListener;
        this.cpListEntity = cpListEntity;
    }

    public VideoStreamViewBitmap(Context context) {
        this(context, null);
    }

    public VideoStreamViewBitmap(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoStreamViewBitmap(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        view = View.inflate(context, R.layout.view_video_stream, null);
        initView(view);
        addView(view);
    }

    LinearLayout lin_progress;

    private void initView(View view) {
        lin_progress = (LinearLayout) view.findViewById(R.id.lin_progress);
        textureView = (TextureView) view.findViewById(R.id.textureview);
        textureView.setSurfaceTextureListener(surfaceTextureListener);
    }

    TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            try {
                surface = new Surface(surfaceTexture);
                if (listener != null) {
                    listener.initOver();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            try {
                surface = null;
                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                }
                clearMemory();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    public void startToPlay(String playUrl) {
        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
            }
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(playUrl);
            mMediaPlayer.setSurface(surface);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {

                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    switch (what) {
                        case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                            MyLog.video("=========缓冲进度==start==");
                            if (lin_progress != null) {
                                lin_progress.setVisibility(VISIBLE);
                            }
                            break;
                        case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                            MyLog.video("=========缓冲进度==end==");
                            if (lin_progress != null) {
                                lin_progress.setVisibility(GONE);
                            }
                            break;
                    }
                    return false;
                }
            });

        } catch (IOException e) {
            MyLog.video("==========播放异常====" + e.toString() + " / " + playUrl, true);
            if (listener != null) {
                listener.playError(e.toString());
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        if (listener != null) {
            listener.playError("播放异常 onError回调 :" + i + " / " + i1);
        }
        return true;
    }

    public void clearMemory() {
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
            mMediaPlayer = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pauseDisplayView() {
        MyLog.playTask("=====流媒体=====暂停");
        if (mMediaPlayer == null) {
            return;
        }
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    public void resumePlayView() {
        MyLog.playTask("=====流媒体=====恢复");
        if (mMediaPlayer == null) {
            return;
        }
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
    }

}
