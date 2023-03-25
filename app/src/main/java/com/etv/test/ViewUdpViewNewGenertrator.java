package com.etv.test;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etv.listener.TaskPlayStateListener;
import com.etv.listener.VideoPlayListener;
import com.etv.task.entity.MediAddEntity;
import com.etv.task.entity.SceneEntity;
import com.etv.util.MyLog;
import com.etv.view.layout.Generator;
import com.udpsync.bean.CmdData;
import com.udpsync.playview.UDPPlayerView;
import com.ys.etv.R;

import java.util.List;

/***
 * 播放视频资源
 */
public class ViewUdpViewNewGenertrator extends Generator {


    View view;
    String screenPosition;
    int width;
    int height;
    boolean ifViewZero = false;
    MediAddEntity mediAddEntity;

    @Override
    public void updateTextInfo(Object object) {
        if (object == null) {
            return;
        }
        playView.parseSyncCmdData((CmdData) object);
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
    public ViewUdpViewNewGenertrator(Context context, int x, int y, int width, int height, MediAddEntity mediAddEntity,
                                     String screenPosition, boolean ifViewZero) {
        super(context, x, y, width, height);
        this.width = width;
        this.height = height;
        MyLog.cdl("=====视频区域得坐标==ViewUdpViewNewGenertrator==" + x + " / " + y + " / " + width + " /" + height);
        this.mediAddEntity = mediAddEntity;
        this.ifViewZero = ifViewZero;
        this.screenPosition = screenPosition;
        view = View.inflate(context, R.layout.view_video_udp_new, null);
        MyLog.playTask("=====removeCacheView=== create ViewUdpViewNewGenertrator== " + this);
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
    UDPPlayerView playView;

    private void initView(View view) {
        tv_desc = (TextView) view.findViewById(R.id.tv_desc);
        rela_no_data = (RelativeLayout) view.findViewById(R.id.rela_no_data);
        playView = view.findViewById(R.id.play_view);

        playView.setVideoPlayListener(new VideoPlayListener() {

            @Override
            public void initOver() {
                playView.startPlay(mediAddEntity.getUrl());
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
        if (playView == null) {
            return -1;
        }
        return playView.getCurrentPlayIndex();
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
        if (playView != null) {
            playView.clearMemory();
        }
    }

    @Override
    public void removeCacheView(String tag) {
        MyLog.playTask("=====removeCacheView=== release ViewUdpViewNewGenertrator== " + this);
        if (playView != null) {
            playView.clearMemoryCache();
        }
    }

    @Override
    public void pauseDisplayView() {
        MyLog.playTask("=====pauseDisplayView===video=====");
    }


    @Override
    public void resumePlayView() {

    }

}
