package com.etv.view.layout.mixedswing;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.RelativeLayout;

import com.etv.config.AppConfig;
import com.etv.config.AppInfo;
import com.etv.listener.TaskPlayStateListener;
import com.etv.task.entity.CpListEntity;
import com.etv.task.entity.MediAddEntity;
import com.etv.task.entity.SceneEntity;
import com.etv.task.util.TaskDealUtil;
import com.etv.util.Biantai;
import com.etv.util.MyLog;
import com.etv.util.SharedPerManager;
import com.etv.view.layout.Generator;
import com.etv.view.layout.image.ViewImageSingleGenertrator;
import com.ys.etv.R;
import com.etv.util.image.glide.GlideCacheUtil;
import com.etv.util.image.glide.GlideImageUtil;
import com.ys.model.entity.FileEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 混播模式 网络版本
 * 按照顺序来播放
 */
public class ViewImgVideoNetGenerate extends Generator {

    View view;
    List<MediAddEntity> listsEntity;
    Activity context;
    int screenWidth = 0;
    int screenHeight = 0;

    CpListEntity cpListEntity;
    boolean isLife = true;
    String screenPosition;
    SceneEntity currentScentity;

    RelativeLayout rela_no_data;
    AbsoluteLayout ab_view;
    Generator generatorView;
    int playPosition = 0;

    public ViewImgVideoNetGenerate(Activity context, CpListEntity cpListEntity, SceneEntity currentScentity, int x, int y,
                                   int width, int height, List<MediAddEntity> listsEntity, boolean isLife, int playPosition,
                                   String screenPosition) {
        super(context, x, y, width, height);
        this.cpListEntity = cpListEntity;
        this.currentScentity = currentScentity;
        this.screenWidth = width;
        this.screenHeight = height;
        this.playPosition = playPosition;
        this.listsEntity = listsEntity;
        this.context = context;
        this.isLife = isLife;
        this.screenPosition = screenPosition;
        view = LayoutInflater.from(context).inflate(R.layout.view_image_video, null);
        ab_view = (AbsoluteLayout) view.findViewById(R.id.ab_view);
        rela_no_data = (RelativeLayout) view.findViewById(R.id.rela_no_data);
        MyLog.playTask("====播放得位置=混播控件===playPosition==" + playPosition);
        parpreViewToShow(TYPE_ONCREATE, "初始化布局");
    }

    private static final int TYPE_ONCREATE = 0;
    private static final int TYPE_PLAY_NEXT = 1;
    private static final int TYPE_PLAY_PRO = 2;
    private static final int TYPE_PLAY_POSITION = 3;

    /**
     * @param playType 0: 初始化 1：下一个  2：播放指定得位置
     * @param tag
     */
    Biantai bianTaiUtil;

    public void parpreViewToShow(int playType, String tag) {
        MyLog.playMix("======混播开始布局==parpreViewToShow=" + playType + " / " + tag + " /screenPosition==" + screenPosition);
        //刚开始播放直接播放，不拦截
        if (playType == TYPE_ONCREATE) {
            refrashPlayView(tag);
            return;
        }
        if (bianTaiUtil == null) {
            bianTaiUtil = new Biantai();
        }
        if (bianTaiUtil.playNextDelayTime(screenPosition)) {
            MyLog.playMix("======混播开始布局===Biantai==拦截");
            return;
        }
        MyLog.playMix("======混播开始布局===过来了===" + playType + " / " + tag);
        //开始在播放了，就不去播放下一个延迟得任务了
        if (handler != null) {
            handler.removeMessages(PLAY_NEXT_MP_DELAYTIME);
        }
        refrashPlayView(tag);
    }

    @Override
    public void updateTextInfo(Object object) {

    }

    private void refrashPlayView(String tag) {
        clearMemoryCache();
        if (!isLife) {
            return;
        }
        if (listsEntity == null || listsEntity.size() < 1) {
            rela_no_data.setVisibility(View.VISIBLE);
            return;
        }
        MyLog.playMix("======混播开始布局==11=" + tag + " / " + playPosition + " / " + listsEntity.size());
        //这里是针对同步任务，可能传递得参数大于当前列表得长度，这里就直接中断操作
        if (playPosition > listsEntity.size() - 1) {
            playNextPargramView("数组越界，播放下一个");
            return;
        }
        MediAddEntity mediAddEntity = listsEntity.get(playPosition);
        if (mediAddEntity == null) {
            rela_no_data.setVisibility(View.VISIBLE);
            return;
        }
        rela_no_data.setVisibility(View.GONE);
        int fileType = mediAddEntity.getFileType();
        MyLog.playMix("====混播00000===" + mediAddEntity.toString() + " / " + fileType);
        List<MediAddEntity> listsImageVideo = new ArrayList<MediAddEntity>();
        listsImageVideo.add(mediAddEntity);
        switch (fileType) {
            case FileEntity.STYLE_FILE_IMAGE:
                generatorView = new ViewImageSingleGenertrator(context, cpListEntity, 0, 0, screenWidth, screenHeight, mediAddEntity);
                addViewListener(generatorView);
                ab_view.addView(generatorView.getView(), generatorView.getLayoutParams());
                generatorView.updateView(null, true);
                break;
            case FileEntity.STYLE_FILE_VIDEO:
                generatorView = TaskDealUtil.getVideoPlayView(context, null, 0, 0, screenWidth, screenHeight, listsImageVideo, screenPosition);
                addViewListener(generatorView);
                ab_view.addView(generatorView.getView(), generatorView.getLayoutParams());
                generatorView.updateView(null, true);
                break;
        }
        dissmissLastViews("===开始布局==");
    }

    @Override
    public int getVideoPlayCurrentDuartion() {
        return 0;
    }

    List<Generator> generatorList = new ArrayList<Generator>();
    List<Generator> generatorListCache = new ArrayList<Generator>();

    private void addViewListener(Generator generatorView) {
        if (generatorView == null) {
            return;
        }
        generatorList.add(generatorView);
        generatorView.setPlayStateChangeListener(new TaskPlayStateListener() {

            @Override
            public void playComplete(int playTag) {
                MyLog.playMix("===混播播放结束=====" + TaskPlayStateListener.getPlayTag(playTag) + "/屏幕的位置==" + screenPosition);
                if (listsEntity == null || listsEntity.size() < 1) {
                    MyLog.playMix("===混播播放结束====终端操作=");
                    return;
                }
                playNextPargramView("播放结束。去播放下一个");
            }

            @Override
            public void playCompletePosition(String etLevel, String taskId, int currentPlayPosition, int playTag) {

            }

            @Override
            public void reStartPlayProgram(String errorDesc) {
                if (listener != null) {
                    listener.reStartPlayProgram(errorDesc);
                }
            }
        });
    }


    private static final int CLEAR_LAST_VIEW = 45123;
    private static final int PLAY_NEXT_MP_DELAYTIME = 45124;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PLAY_NEXT_MP_DELAYTIME:
                    //播放下一个节目
                    parpreViewToShow(TYPE_PLAY_POSITION, "播放指定得素材开始延迟");
                    break;
                case CLEAR_LAST_VIEW:
                    removeCacheView("延时cleraView--混播");
                    break;
            }
        }
    };

    @Override
    public void removeCacheView(String tag) {
        try {
            MyLog.playMix("混播====移除View0000===" + tag);
            if (!isLife) {
                MyLog.playMix("混播====移除View==null,直接移除View=isLife==false==" + tag);
                ab_view.removeAllViews();
                return;
            }
            if (generatorListCache == null || generatorListCache.size() < 1) {
                return;
            }
            for (int i = 0; i < generatorListCache.size(); i++) {
                Generator genView = generatorListCache.get(i);
                genView.removeCacheView("vvvvvv");
                ab_view.removeView(genView.getView());
                MyLog.playMix("混播====移除View====1111");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 主界面调用，直接清理所有得数据
     */
    @Override
    public void clearMemory() {
        isLife = false;
        clearMemoryCache();
        dissmissLastViews("clearMemory");
    }

    /**
     * 销毁以前得View
     */
    private void dissmissLastViews(String printTag) {
        MyLog.playMix("=======dissmissLastViews=======" + printTag);
        //网络导入或者网络下发模式
        handler.sendEmptyMessageDelayed(CLEAR_LAST_VIEW, AppConfig.Seamless_Switching_Time);
    }

    @Override
    public void timeChangeToUpdateView() {

    }

    private void clearMemoryCache() {
        generatorListCache.clear();
        try {
            if (generatorList != null && generatorList.size() > 0) {
                for (int i = 0; i < generatorList.size(); i++) {
                    Generator genView = generatorList.get(i);
                    MyLog.playMix("====混播，销毁布局==" + genView.getClass().getName());
                    genView.clearMemory();
                    generatorListCache.add(genView);
                }
                generatorList.clear();
            }
            GlideCacheUtil.getInstance().clearImageAllCache(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateView(Object object, boolean isShow) {

    }

    @Override
    public void playComplet() {
        if (listener != null) {
            listener.playComplete(TaskPlayStateListener.TAG_PLAY_VIDEO_IMAGE);
        }
    }

    /**
     * 播放固定位置得场景
     *
     * @param position
     */
    @Override
    public void playPositionScenProgram(int position, List<MediAddEntity> mediAddEntities, SceneEntity currentScentity) {
        playPosition = position;
        this.listsEntity = mediAddEntities;
        this.currentScentity = currentScentity;
        if (currentScentity == null) {
            return;
        }
        parpreViewToShow(TYPE_PLAY_POSITION, "播放指定位置得素材==" + playPosition);
    }

    /**
     * 播放下一个
     */
    private void playNextPargramView(String printTag) {
        MyLog.playMix("=======播放下一个=" + printTag);
        playPosition++;
        if (playPosition > (listsEntity.size() - 1)) {
            playComplet();
            playPosition = 0;
        }
        MyLog.playMix("==============混播播放结束=====" + (currentScentity == null));
        //下面这一段代码是调试代码，如果有问题可以删除
        if (currentScentity != null) {
            String etLevel = currentScentity.getEtLevel();
            MyLog.playMix("==============混播播放结束==etLevel===" + etLevel);
            if (etLevel.contains(AppInfo.TASK_PLAY_PLAY_SAME)) {  //同步模式
                backPlayOverToMainView(playPosition);
                handler.sendEmptyMessageDelayed(PLAY_NEXT_MP_DELAYTIME, 500);
            } else {
                parpreViewToShow(TYPE_PLAY_NEXT, "播放下一个一个素材");
            }
            return;
        }
        parpreViewToShow(TYPE_PLAY_NEXT, "播放下一个一个素材,当前场景==null");
    }

    /**
     * 播放完毕了一个，回调给主界面
     */
    private void backPlayOverToMainView(int currentPlayPosition) {
        MyLog.playMix("=====backPlayOverToMainView=====00000=");
        if (!screenPosition.contains(AppInfo.PROGRAM_POSITION_MAIN)) {
            MyLog.playMix("=====backPlayOverToMainView=====副屏回调，直接中断操作=");
            return;
        }
        if (listener == null) {
            MyLog.playMix("=====backPlayOverToMainView=====listener=null=");
            return;
        }
        if (currentScentity == null) {
            MyLog.playMix("=====backPlayOverToMainView=====currentScentity=null=");
            return;
        }
        String etLevel = currentScentity.getEtLevel();
        String taskId = currentScentity.getTaskid();
        listener.playCompletePosition(etLevel, taskId, currentPlayPosition, TaskPlayStateListener.TAG_PLAY_VIDEO_IMAGE);
    }

    /**
     * 播放上一个素材
     */
    private void playPretPargramView() {
        playPosition--;
        if (playPosition < 0) {
            playPosition = listsEntity.size() - 1;
        }
        parpreViewToShow(TYPE_PLAY_PRO, "播放上一个素材");
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public void pauseDisplayView() {
        if (generatorList == null || generatorList.size() < 1) {
            return;
        }
        for (int i = 0; i < generatorList.size(); i++) {
            generatorList.get(i).pauseDisplayView();
        }
    }

    @Override
    public void resumePlayView() {
        if (generatorList == null || generatorList.size() < 1) {
            return;
        }
        for (int i = 0; i < generatorList.size(); i++) {
            generatorList.get(i).resumePlayView();
        }
    }

}
