package com.etv.test;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etv.config.AppConfig;
import com.etv.config.AppInfo;
import com.etv.db.DbStatiscs;
import com.etv.entity.StatisticsEntity;
import com.etv.listener.TaskPlayStateListener;
import com.etv.listener.VideoPlayListener;
import com.etv.task.entity.CpListEntity;
import com.etv.task.entity.MediAddEntity;
import com.etv.task.entity.SceneEntity;
import com.etv.util.MyLog;
import com.etv.view.layout.Generator;
import com.etv.view.layout.video.media.VideoViewBitmap;
import com.ys.etv.R;

import java.util.List;

/***
 * 播放视频资源
 */
public class ViewVideoUdpGenertrator extends Generator {

    VideoViewUdpBitmap videoView = null;
    View view;
    String screenPosition;
    int width;
    int height;
    boolean ifViewZero = false;
    MediAddEntity mediAddEntity;

    @Override
    public void updateTextInfo(Object object) {
        SameTaskVideo sameTaskVideo = (SameTaskVideo) object;
        if (sameTaskVideo == null) {
            return;
        }
        if (videoView == null) {
            return;
        }
        if (mediAddEntity == null) {
            return;
        }
        String localPath = mediAddEntity.getUrl();
        String filePath = sameTaskVideo.getFilePath();
        if (!localPath.equals(filePath)) {
            return;
        }
        int currentProgress = sameTaskVideo.getProgress();
        videoView.updateSeekBar(currentProgress);
    }

    /***
     *
     * @param context
     * @param x
     * 起点坐标
     * @param y
     * 起点坐标
     * @param width
     * 控件宽度
     * @param height
     * 素材集合
     */
    public ViewVideoUdpGenertrator(Context context, int x, int y, int width, int height, MediAddEntity mediAddEntity,
                                   String screenPosition, boolean ifViewZero) {
        super(context, x, y, width, height);
        this.width = width;
        this.height = height;
        MyLog.cdl("=====视频区域得坐标==ViewVideoUdpGenertrator==" + x + " / " + y + " / " + width + " /" + height);
        this.mediAddEntity = mediAddEntity;
        this.ifViewZero = ifViewZero;
        this.screenPosition = screenPosition;
        view = View.inflate(context, R.layout.view_video_play_udp, null);
    }

    @Override
    public void timeChangeToUpdateView() {

    }

    @Override
    public void updateView(Object object, boolean isShowBtn) {
        MyLog.playTask("=========控件选择=====================使用的是全屏拉伸=========");
        initView(view);
    }

    /***
     * 播放完毕
     */
    @Override
    public void playComplet() {
        if (listener != null) {
            listener.playComplete(TaskPlayStateListener.TAG_PLAY_VIDEO);
        }
    }

    RelativeLayout rela_no_data;
    TextView tv_desc;

    private void initView(View view) {
        tv_desc = (TextView) view.findViewById(R.id.tv_desc);
        rela_no_data = (RelativeLayout) view.findViewById(R.id.rela_no_data);
        videoView = (VideoViewUdpBitmap) view.findViewById(R.id.video_view);
        videoView.setVideoPlayListener(new VideoPlayListener() {

            @Override
            public void initOver() {
                videoView.startToPlay(mediAddEntity);
            }

            @Override
            public void playCompletion(String tag) {
                MyLog.video("====视频列表播放完毕了==这里回调====" + tag);
                playComplet();
            }

            @Override
            public void playCompletionSplash(int position, int playTime) {  //每次播放完毕都会调用这里
            }

            @Override
            public void playError(String errorDesc) {
                MyLog.video("error==startToPlay==" + errorDesc, true);
            }

            @Override
            public void playErrorToStop(String errorDesc) {
                MyLog.video("error==playErrorToStop==" + errorDesc, true);
                rela_no_data.setVisibility(View.VISIBLE);
                tv_desc.setText(errorDesc + "");
            }

            @Override
            public void reStartPlayProgram(String errorDesc) {
                MyLog.video("error==reStartPlayProgram==" + errorDesc, true);
                if (listener != null) {
                    listener.reStartPlayProgram(errorDesc);
                }
            }
        });
    }


    @Override
    public int getVideoPlayCurrentDuartion() {
        if (videoView == null) {
            return 0;
        }
        return videoView.getCurrentPlayIndex();
    }

    @Override
    public void playPositionScenProgram(int position, List<MediAddEntity> mediAddEntities, SceneEntity currentScentity) {

    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public void clearMemory() {
//        if (videoView != null) {
//            videoView.clearMemory();
//        }
    }

    @Override
    public void removeCacheView(String tag) {
        if (videoView != null) {
            videoView.clearMemory();
        }
//        if (videoView != null) {
//            videoView.removeCacheView();
//        }
    }

    @Override
    public void pauseDisplayView() {
        MyLog.playTask("=====pauseDisplayView===video=====");
    }


    @Override
    public void resumePlayView() {

    }

}
