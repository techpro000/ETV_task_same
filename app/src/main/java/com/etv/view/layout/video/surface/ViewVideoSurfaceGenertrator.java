package com.etv.view.layout.video.surface;

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
import com.ys.etv.R;

import java.util.List;

/***
 * 播放视频资源
 */
public class ViewVideoSurfaceGenertrator extends Generator {

    List<MediAddEntity> videoLists = null;
    View view;
    CpListEntity cpListEntity;
    String screenPosition;
    int width;
    int height;

    @Override
    public int getVideoPlayCurrentDuartion() {
        return 0;
    }

    @Override
    public void timeChangeToUpdateView() {

    }

    @Override
    public void updateTextInfo(Object object) {

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
     * 控件高度
     * @param videoLists
     * 素材集合
     */
    public ViewVideoSurfaceGenertrator(Context context, CpListEntity cpListEntity, int x, int y, int width, int height, List<MediAddEntity> videoLists, String screenPosition) {
        super(context, x, y, width, height);
        this.width = width;
        this.height = height;
        MyLog.cdl("=====视频区域得坐标==ViewVideoSurfaceGenertrator==" + x + " / " + y + " / " + width + " /" + height);
        this.videoLists = videoLists;
        this.cpListEntity = cpListEntity;
        this.screenPosition = screenPosition;
        view = View.inflate(context, R.layout.view_video_play_new_surface, null);
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

    int playTimeUpdate = 15;

    public void toUpdatePlayNum(int position) {
        if (listener == null) {
            return;
        }
        addUpdateToWeb(videoLists.get(position).getMidId(), playTimeUpdate);
    }

    private void addUpdateToWeb(String midId, int timeDisplay) {
        String resourType = AppInfo.VIEW_VIDEO;
        StatisticsEntity statisticsEntity = new StatisticsEntity(midId, resourType, timeDisplay, 1, System.currentTimeMillis());
        DbStatiscs.saveStatiseToLocal(statisticsEntity, "Video视频添加统计");
    }

    RelativeLayout rela_no_data;
    TextView tv_desc;
    VideoSurfaceView video_view_surface;

    private void initView(View view) {
        tv_desc = (TextView) view.findViewById(R.id.tv_desc);
        rela_no_data = (RelativeLayout) view.findViewById(R.id.rela_no_data);
        video_view_surface = (VideoSurfaceView) view.findViewById(R.id.video_view_surface);
        video_view_surface.setViewSize(width, height);
        video_view_surface.setVideoClickListen(listener, cpListEntity);
        video_view_surface.setVideoPlayListener(new VideoPlayListener() {
            @Override
            public void initOver() {
                video_view_surface.setPlayList(videoLists);
            }

            @Override
            public void playCompletion(String tag) {
                MyLog.video("====视频列表播放完毕了==这里回调====" + tag);
                playComplet();
            }

            @Override
            public void playCompletionSplash(int position, int playTime) {
                playTimeUpdate = playTime;
                toUpdatePlayNum(position);
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
    public void playPositionScenProgram(int position, List<MediAddEntity> mediAddEntities, SceneEntity currentScentity) {

    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public void clearMemory() {
        if (video_view_surface != null) {
            video_view_surface.clearMemory();
        }
    }

    @Override
    public void removeCacheView(String tag) {
        if (video_view_surface != null) {
            video_view_surface.removeCacheView();
        }
    }

    @Override
    public void pauseDisplayView() {
        MyLog.playTask("=====pauseDisplayView===video=====");
        if (video_view_surface != null) {
            video_view_surface.pausePlayVideo();
        }
    }

    @Override
    public void resumePlayView() {
        MyLog.playTask("=====resumePlayView===video=====");
        if (video_view_surface != null) {
            video_view_surface.resumePlayVideo();
        }
    }

}
