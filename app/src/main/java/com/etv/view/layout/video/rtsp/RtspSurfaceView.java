package com.etv.view.layout.video.rtsp;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.SyncParams;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.ys.etv.R;
import java.io.IOException;



public class RtspSurfaceView extends RelativeLayout {

    SurfaceView surfaceView;
    MediaPlayer mediaPlayer;

    public RtspSurfaceView(Context context) {
        this(context, null);
    }

    public RtspSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RtspSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = inflate(context, R.layout.view_video_rtsp, null);
        surfaceView = view.findViewById(R.id.surface_View);
        initView();
        addView(view);
    }

    private void initView() {
        mediaPlayer = new MediaPlayer();
        //mediaPlayer.setSyncParams();
        mediaPlayer.setOnPreparedListener(android.media.MediaPlayer::start);
        surfaceView.getHolder().addCallback(new HolderCallback(){
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                mediaPlayer.setDisplay(holder);
            }
        });
    }


    public void startPlayUrl(String streamUrl) {
        try {
            //mediaPlayer.setSyncParams(new SyncParams().);
            mediaPlayer.setDataSource(getContext(), Uri.parse(streamUrl));
            mediaPlayer.setVideoScalingMode(android.media.MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pausePlay() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    private void resumePlay() {

    }

    public void clearMemoryCache() {
        try {
            pausePlay();
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
