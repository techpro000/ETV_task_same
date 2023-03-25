package com.etv.view.layout.video.surface;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;

import com.etv.listener.TaskPlayStateListener;
import com.etv.listener.VideoPlayListener;
import com.etv.task.entity.CpListEntity;
import com.etv.task.entity.MediAddEntity;
import com.etv.task.util.TaskDealUtil;
import com.etv.util.MyLog;
import com.ys.etv.R;

import java.util.List;

public class VideoSurfaceView extends RelativeLayout implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, OnErrorListener {

    public VideoSurfaceView(Context context) {
        this(context, null);
    }

    public VideoSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        view = View.inflate(context, R.layout.view_video_surface, null);
        initView(view);
        addView(view);
    }

    View view;
    Context context;
    private SurfaceView surface_view_video;
    private MediaPlayer mediaPlayer;
    private SurfaceHolder surfaceHolder;
    private RelativeLayout lin_surface_view;

    private void initView(View view) {
        mediaPlayer = new MediaPlayer();
        lin_surface_view = (RelativeLayout) view.findViewById(R.id.lin_surface_view);
        surface_view_video = (SurfaceView) view.findViewById(R.id.surface_view_video);
        surfaceHolder = surface_view_video.getHolder();
//        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.setKeepScreenOn(true);
        surfaceHolder.addCallback(this);
        surface_view_video.setOnTouchListener(onTouchListener);
    }


    float downX, upX;
    long clickDownTime = 0;
    long clickUpTime = 0;
    private OnTouchListener onTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {
            int action = motionEvent.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    clickDownTime = System.currentTimeMillis();
                    clickUpTime = System.currentTimeMillis();
                    downX = motionEvent.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    clickUpTime = System.currentTimeMillis();
                    upX = motionEvent.getX();
                    if (downX - upX > 200) { //向左滑动
                        if (playList.size() == 1) {
                            MyLog.cdl("===滑动==消费掉=");
                            return true;
                        }
                        String pmType = playList.get(currentPlayIndex).getPmType();
                    } else if (upX - downX > 200) { //向右滑动
                        if (playList.size() == 1) {
                            MyLog.cdl("===滑动==消费掉=");
                            return true;
                        }
                        if (playList.size() == 1) {
                            MyLog.cdl("===滑动==消费掉=");
                            return true;
                        }
                        MyLog.video("===播放上一个=" + currentPlayIndex + " / " + playList.size());
                        String pmType = playList.get(currentPlayIndex).getPmType();
                    } else { //执行点击事件
                        clickApEntityBack();
                    }
                    break;
            }
            return true;
        }
    };

    /**
     * 执行点击事件
     */
    private void clickApEntityBack() {
//        if (taskPlayStateListener == null) {
//            return;
//        }
//        if (Biantai.isOneClick()) {
//            MyLog.playTask("=====点击了视频===000暴力测试拦截");
//            return;
//        }
//        MyLog.playTask("=====点击了视频===000");
//
//        if (playList == null || playList.size() < 1) {
//            return;
//        }
//        List<String> listsShow = new ArrayList<String>();
//        for (int i = 0; i < playList.size(); i++) {
//            listsShow.add(playList.get(i).getUrl());
//        }
//        taskPlayStateListener.clickTaskView(cpListEntity, listsShow, 0);
    }


    private VideoPlayListener listener;

    public void setVideoPlayListener(VideoPlayListener listener) {
        this.listener = listener;
    }

    List<MediAddEntity> playList;
    private int currentPlayIndex = 0;

    /**
     * 直接播放，从0开始
     *
     * @param playUrlList
     */
    public void setPlayList(List<MediAddEntity> playUrlList) {
        this.playList = playUrlList;
        if (playList == null) {
            if (listener != null) {
                listener.playError("没有需要播放的信息");
            }
            return;
        }
        currentPlayIndex = 0;
        MediAddEntity mediAddEntity = playList.get(currentPlayIndex);
        startToPlayVideo(mediAddEntity);
    }

    public void startToPlayVideo(MediAddEntity mediAddEntity) {
        if (mediAddEntity == null) {
            if (listener != null) {
                listener.playError("被播放的素材==NULL");
            }
            return;
        }
        int volNum = TaskDealUtil.getMediaVolNum(playList, currentPlayIndex);
        float volNumChangfe = (float) (volNum * 1.0 / 100);
        String playUrl = mediAddEntity.getUrl();
        MyLog.video("==========开始播放 volNum==" + volNum + " / " + currentPlayIndex + " / " + playUrl);
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(playUrl);
            mediaPlayer.setDisplay(null);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setVolume(volNumChangfe, volNumChangfe);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            MyLog.video("播放异常:" + e.toString());
        }
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (listener != null) {
            listener.playError("播放视频异常了");
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.setDisplay(surface_view_video.getHolder());
        mp.start();
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        MyLog.video("========单机视频=====播放完成=====");
        playNextPositionVideoPlayer();
    }

    private void playNextPositionVideoPlayer() {
        MyLog.video("========单机视频======播放下一个====");
        currentPlayIndex++;
        if (currentPlayIndex > playList.size() - 1) {
            if (listener != null) {
                listener.playCompletion("全部播放完毕了");
            }
            currentPlayIndex = 0;
        } else {
            if (listener != null) {
                listener.playCompletionSplash(currentPlayIndex, 15);
            }
        }
        MediAddEntity mediAddEntity = playList.get(currentPlayIndex);
        startToPlayVideo(mediAddEntity);
    }

    private void stopPlay() {
        MyLog.video("========单机视频=====停止播放=====");
        if (mediaPlayer == null) {
            return;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }

    }

    public void clearMemory() {
        MyLog.video("========单机视频=====清理内存=====");
        if (mediaPlayer == null) {
            return;
        }
        stopPlay();
        mediaPlayer.release();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (listener != null) {
            listener.initOver();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//        MyLog.video("=========视频的尺寸===" + width + " /" + height);
//        if (surface_view_video != null) {
//            surface_view_video.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
//        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    /**
     * 恢复播放的功能
     */
    public void resumePlayView() {
        if (mediaPlayer == null) {
            return;
        }
        if (mediaPlayer.isPlaying()) {
            return;
        }
        mediaPlayer.start();
    }

    public void pauseDisplayView() {
        if (mediaPlayer == null) {
            return;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    private void playPrePositionVideoPlayer() {
        try {
            if (playList == null || playList.size() < 1) {
                listener.reStartPlayProgram("播放上一个,出错了，这里执行播放完成,跳过这里");
                return;
            }
            currentPlayIndex--;
            if (currentPlayIndex < 0) {
                currentPlayIndex = playList.size();
            }
            MediAddEntity mediAddEntity = playList.get(currentPlayIndex);
            startToPlayVideo(mediAddEntity);
        } catch (Exception e) {
            MyLog.video("播放上一个视频： " + e.toString(), true);
            e.printStackTrace();
        }
    }

    int viewSizeWidth = 0;
    int viewSizeHeight = 0;

    /***
     * 计算显示区域得尺寸
     * @param width
     * @param height
     */
    public void setViewSize(int width, int height) {
        viewSizeWidth = width;
        viewSizeHeight = height;
    }

    TaskPlayStateListener taskPlayStateListener;
    CpListEntity cpListEntity;

    public void setVideoClickListen(TaskPlayStateListener taskPlayStateListener, CpListEntity cpListEntity) {
        this.taskPlayStateListener = taskPlayStateListener;
        this.cpListEntity = cpListEntity;
    }


    //清理缓存得View
    public void removeCacheView() {
        try {
            MyLog.video("======执行销毁进程===mediapLayer==removeCacheView");
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
        } catch (Exception e) {
            MyLog.video("======执行销毁进程===mediapLayer==" + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * 暂停播放
     */
    public void pausePlayVideo() {
        if (mediaPlayer == null) {
            return;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    /**
     * 恢复播放
     */
    public void resumePlayVideo() {
        if (mediaPlayer == null) {
            return;
        }
        if (playList == null || playList.size() < 1) {
            return;
        }
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

}
