package com.etv.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.etv.config.AppConfig;
import com.etv.listener.VideoPlayListener;
import com.etv.task.entity.MediAddEntity;
import com.etv.util.MyLog;
import com.ys.etv.R;

public class VideoViewUdpBitmap extends RelativeLayout implements
        TextureView.SurfaceTextureListener, MediaPlayer.OnCompletionListener,
        OnPreparedListener, MediaPlayer.OnErrorListener, SeekBar.OnSeekBarChangeListener {

    View view;
    private MediaPlayer mMediaPlayer;
    private Surface surface;
    private TextureView textureView;
    VideoPlayListener listener;
    Context context;

    public void setVideoPlayListener(VideoPlayListener listener) {
        this.listener = listener;
    }

    public VideoViewUdpBitmap(Context context) {
        this(context, null);
    }

    public VideoViewUdpBitmap(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoViewUdpBitmap(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        view = View.inflate(context, R.layout.view_video_udp, null);
        initView(view);
        addView(view);
    }

    TextView tv_info;

    private void initView(View view) {
        tv_info = (TextView) view.findViewById(R.id.tv_info);
        textureView = (TextureView) view.findViewById(R.id.textureview);
        textureView.setSurfaceTextureListener(this);//设置监听函数  重写4个方法
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        surface = new Surface(surfaceTexture);
        if (listener != null) {
            listener.initOver();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        surface = null;
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
        clearMemory();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    public void startToPlay(MediAddEntity mediAddEntity) {
        String playUrl = mediAddEntity.getUrl();
        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
            }
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(playUrl);
            mMediaPlayer.setSurface(null);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnErrorListener(this);
//            mMediaPlayer.prepare();
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            if (listener != null) {
                listener.playError(e.toString());
            }
            MyLog.video("==========播放异常====" + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        try {
            playTimeCurrent = (mediaPlayer.getDuration()) / 1000;
            MyLog.playTask("=====当前播放时长 ：" + playTimeCurrent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMediaPlayer.setSurface(surface);
        mediaPlayer.start();
    }

    int playTimeCurrent = 15;


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        listener.playCompletion("onCompletion");
    }


    public int getCurrentPlayIndex() {
        if (mMediaPlayer == null) {
            return 0;
        }
        int playIndex = 0;
        try {
            if (mMediaPlayer.isPlaying()) {
                playIndex = mMediaPlayer.getCurrentPosition();
            }
        } catch (IllegalStateException e) {

            e.printStackTrace();
        }
        return playIndex;
    }

    //==============================================================================

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        if (listener != null) {
            listener.playError("播放异常");
        }
        return false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.e("seekBar", "onStartTrackingTouch=========");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int maxDuration = mMediaPlayer.getDuration();
        int progress = seekBar.getProgress();
        int curretDuration = progress * maxDuration / 100;
        mMediaPlayer.seekTo(curretDuration);
        Log.e("seekBar", "onStopTrackingTouch=========");
    }

    public void clearMemory() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void updateSeekBar(int receiverPosition) {
        if (mMediaPlayer == null) {
            return;
        }
        int currentPosition = mMediaPlayer.getCurrentPosition();
        int distanceTime = Math.abs(receiverPosition - currentPosition);
        if (tv_info != null) {
            tv_info.setText(currentPosition + " /" + mMediaPlayer.getDuration());
        }
        MyLog.video("=====当前的进度==" + currentPosition + " / 接受的进度==" + receiverPosition + "/时间差==" + distanceTime);
        if (distanceTime > 150 && distanceTime < 10 * 1000) {
            MyLog.video("时间差000===" + distanceTime);
            mMediaPlayer.seekTo(receiverPosition + 20);
        }
    }

}
