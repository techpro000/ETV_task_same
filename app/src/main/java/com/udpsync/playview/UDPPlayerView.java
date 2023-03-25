package com.udpsync.playview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.etv.config.AppConfig;
import com.etv.listener.VideoPlayListener;
import com.etv.service.EtvService;
import com.etv.util.MyLog;
import com.etv.util.SharedPerUtil;
import com.etv.view.layout.video.rtsp.HolderCallback;
import com.udpsync.CmdAct;
import com.udpsync.Command;
import com.udpsync.UDPSocket;
import com.udpsync.bean.CmdData;
import com.udpsync.observe.CmdObserver;
import com.ys.etv.R;

import java.io.IOException;

public class UDPPlayerView extends RelativeLayout {

    //private SurfaceView surfaceView;
    private TextureView surfaceView;
    private MediaPlayer mediaPlayer;

    private Observer<CmdData> cmdObserver;

    private boolean isRelease;
    private String curPath;
    private VideoPlayListener playListener;

    public UDPPlayerView(Context context) {
        this(context, null);
    }

    public UDPPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UDPPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = inflate(context, R.layout.view_udp_player, null);
        surfaceView = view.findViewById(R.id.surface_view);
        initView();
        addView(view);
        initObserver();

        surfaceView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
                playListener.initOver();
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

            }
        });

        /*surfaceView.getHolder().addCallback(new HolderCallback(){
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                playListener.initOver();
            }
        });*/
    }

    private void initView() {
        isRelease = false;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(mp -> {
            //mp.setDisplay(surfaceView.getHolder());
            mp.setSurface(new Surface(surfaceView.getSurfaceTexture()));
            mp.start();
        });
        mediaPlayer.setOnCompletionListener(mp -> {
            if (playListener != null) {
                playListener.playCompletion("playCompletion");
            }
        });
        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            if (playListener != null) {
                playListener.playError("play error " + extra);
            }
            return false;
        });
        /*surfaceView.getHolder().addCallback(new SurfaceCallback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                mediaPlayer.setDisplay(holder);
            }
        });*/
    }

    private void initObserver() {
        cmdObserver = this::parseSyncCmdData;
        CmdObserver.get().addObserver(cmdObserver);
    }

    public void parseSyncCmdData(CmdData cmdData) {
        switch (cmdData.cmd) {
            case CmdAct.CMD_PROGRESS:
                int cur = mediaPlayer.getCurrentPosition();
                if (!mediaPlayer.isPlaying() || !cmdData.path.equals(curPath)) {
                    return;
                }
                int pos = cmdData.pos;
                int offset = Math.abs(cur - pos);
//                long currtime = System.currentTimeMillis();
//                String compareStr = "s/r===> " + cmdData.time + "|" + currtime + " ===> " + (currtime - cmdData.time) + " ===main/curr->" + pos + "|" + cur + " ----> " + offset;
                if (offset > 59) {
                    EtvService.getInstance().sendUdpMessage(Command.buildSeekProgressCmd(cmdData.path, pos, cmdData.secenId), UDPSocket.ALL_HOST);
                }
//                System.out.println(compareStr);
//                MyLog.printUdpLogToSd("udp-progress.txt", compareStr + "\r\n");
                break;
            case CmdAct.CMD_SEEK_PROGRESS:

                if (!isRelease && cmdData.path.equals(curPath)) {
                    mediaPlayer.seekTo(cmdData.pos);
                    //122
//                    long curtime = System.currentTimeMillis();
//                    MyLog.printUdpLogToSd("udp-sync.txt", "s/r===> " + cmdData.time + "|" + curtime + " ===> " + (curtime - cmdData.time) + " ===> " + cmdData.pos + "\r\n");
                }
                break;
        }
    }

    public void startPlay(String path) {
        try {
            MyLog.video("----------------startPlay===> " + path);
            curPath = path;
            mediaPlayer.reset();
            mediaPlayer.setDisplay(null);
            mediaPlayer.setDataSource(path);
            mediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            if (playListener != null) {
                playListener.playError("play error: " + e.getMessage());
            }
        }
    }

    public void pausePlay() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public int getCurrentPlayIndex() {
        if (isRelease || !mediaPlayer.isPlaying()) {
            return -1;
        }
        return mediaPlayer.getCurrentPosition();
    }

    public void setVideoPlayListener(VideoPlayListener listener) {
        playListener = listener;
    }

    public void clearMemoryCache() {
        isRelease = true;
        CmdObserver.get().removeObserver(cmdObserver);
        try {
            MyLog.playTask("clearMemoryCache=====销毁节目==");
            if (mediaPlayer == null) {
                return;
            }
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearMemory() {
        try {
            if (mediaPlayer == null) {
                return;
            }
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
