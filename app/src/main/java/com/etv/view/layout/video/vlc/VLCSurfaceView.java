//package com.etv.view.layout.video.vlc;
//
//import android.content.Context;
//import android.net.Uri;
//import android.util.AttributeSet;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//import android.view.View;
//import android.widget.RelativeLayout;
//
//import com.etv.util.MyLog;
//import com.ys.etv.R;
//
//import org.videolan.libvlc.LibVLC;
//import org.videolan.libvlc.Media;
//import org.videolan.libvlc.MediaPlayer;
//import org.videolan.libvlc.interfaces.IVLCVout;
//
////    mediaPlayer.setVolume(progress);
////    //退出全屏
////    WindowManager.LayoutParams params = getWindow().getAttributes();
////    params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
////    getWindow().setAttributes(params);
////    getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
////    //全屏
////    WindowManager.LayoutParams params = getWindow().getAttributes();
////    params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
////    getWindow().setAttributes(params);
////    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//
//public class VLCSurfaceView extends RelativeLayout {
//    public VLCSurfaceView(Context context) {
//        this(context, null);
//    }
//
//    public VLCSurfaceView(Context context, AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public VLCSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        View view = inflate(context, R.layout.view_video_vlc, null);
//        surfaceView = (SurfaceView) view.findViewById(R.id.surfaceView);
//        initView();
//        addView(view);
//    }
//
//    SurfaceView surfaceView;
//    MediaPlayer mediaPlayer;
//    private IVLCVout vlcVout;
//    LibVLC libvlc = null;
//
//    private void initView() {
//        SurfaceHolder surfaceHolder = surfaceView.getHolder();
//        libvlc = LibVLCUtil.getLibVLC(null);
//        surfaceHolder.setKeepScreenOn(true);
//        mediaPlayer = new MediaPlayer(libvlc);
//        vlcVout = mediaPlayer.getVLCVout();
//        vlcVout.setVideoView(surfaceView);
//        vlcVout.attachViews();
//    }
//
//    public void startPlayUrl(String streamUrl) {
//        if (mediaPlayer == null) {
//            initView();
//        }
//        Media media = new Media(libvlc, Uri.parse(streamUrl));
//        mediaPlayer.setMedia(media);
//        mediaPlayer.setEventListener(eventListener);
//        mediaPlayer.play();
//    }
//
//    MediaPlayer.EventListener eventListener = new MediaPlayer.EventListener() {
//        @Override
//        public void onEvent(MediaPlayer.Event event) {
//            try {
//                switch (event.type) {
////                    public static final int EndReached = 265;
////                    public static final int EncounteredError = 266;
////                    public static final int TimeChanged = 267;          //时间变化
////                    public static final int PositionChanged = 268;      //进度变化
////                    public static final int SeekableChanged = 269;
////                    public static final int PausableChanged = 270;
////                    public static final int Vout = 274;         //Playing 后边得通知
////                    public static final int ESAdded = 276;
////                    public static final int ESDeleted = 277;             //
//                    case MediaPlayer.Event.Opening:  //准备播放
//                        backViewStatues(true);
//                        break;
//                    case MediaPlayer.Event.Playing:  //正常播放
//                        backViewStatues(false);
//                        break;
//                    case MediaPlayer.Event.Paused:  //暂停播放
//                        break;
//                    case MediaPlayer.Event.EncounteredError:
//                        MyLog.playTask("====eventListener====直播流播放异常=");
//                        break;
//                    case MediaPlayer.Event.Stopped:
//                        MyLog.playTask("====eventListener====直播流中断=");
//                        backViewStatues(true);
//                        surfaceView.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                mediaPlayer.setMedia(mediaPlayer.getMedia());
//                                mediaPlayer.play();
//                                backViewStatues(false);
//                            }
//                        }, 3000);
//                        break;
//                }
////                if (event.getTimeChanged() == 0) {
////                    return;
////                }
////                if (mediaPlayer.getPlayerState() == Media.State.Ended) {
////                    mediaPlayer.setTime(0);
////                    mediaPlayer.stop();
////                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };
//
//    private void backViewStatues(boolean b) {
//        if (vlcPlayerStatuesListener == null) {
//            return;
//        }
//        vlcPlayerStatuesListener.showProgressStatues(b);
//    }
//
//    public void pausePlay() {
//        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//            mediaPlayer.pause();
//            mediaPlayer.setEventListener(null);
//        }
//        if (vlcVout != null) {
//            vlcVout.detachViews();
//        }
//    }
//
//    private void resumePlay() {
////        vlcVout.setVideoView(surfaceView);
////        vlcVout.attachViews();
////        vlcVout.addCallback(callback);
////        mediaPlayer.setEventListener(eventListener);
//    }
//
//    public void clearMemoryCache() {
//        try {
//            pausePlay();
//            if (mediaPlayer != null) {
//                mediaPlayer.release();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    VlcPlayerStatuesListener vlcPlayerStatuesListener;
//
//    public void setPlayVlcSatues(VlcPlayerStatuesListener vlcPlayerStatuesListener) {
//        this.vlcPlayerStatuesListener = vlcPlayerStatuesListener;
//    }
//
//
//}
