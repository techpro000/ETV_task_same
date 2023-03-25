package com.etv.view.layout.mixedswing;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.RelativeLayout;

import com.etv.config.AppConfig;
import com.etv.listener.TaskPlayStateListener;
import com.etv.service.EtvService;
import com.etv.setting.entity.RowCow;
import com.etv.task.entity.MediAddEntity;
import com.etv.task.entity.PositionEntity;
import com.etv.task.entity.SceneEntity;
import com.etv.task.util.TaskDealUtil;
import com.etv.test.SameTaskVideo;
import com.etv.test.ViewUdpViewNewGenertrator;
import com.etv.test.ViewVideoUdpGenertrator;
import com.etv.util.Biantai;
import com.etv.util.CodeUtil;
import com.etv.util.MyLog;
import com.etv.util.SharedPerManager;
import com.etv.util.SharedPerUtil;
import com.etv.util.image.glide.GlideCacheUtil;
import com.etv.view.layout.Generator;
import com.etv.view.layout.image.ViewImageSingleUdpGenertrator;
import com.udpsync.Command;
import com.udpsync.UDPSocket;
import com.udpsync.bean.CmdData;
import com.ys.etv.R;
import com.ys.model.entity.FileEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 混播模式 网络版本
 * 按照顺序来播放
 */
public class ViewImgMixSameGenerate extends Generator {

    View view;
    List<MediAddEntity> listsEntity;
    Activity context;
    int screenWidth = 0;
    int screenHeight = 0;
    boolean isLife = true;
    String screenPosition;
    SceneEntity currentScentity;

    RelativeLayout rela_no_data;
    AbsoluteLayout ab_view;
    Generator generatorView;
    int playPosition = 0;

    private Disposable progressDisposable;

    static List<Generator> generatorList = new ArrayList<>();
    static List<Generator> generatorListCache = new ArrayList<>();

    public ViewImgMixSameGenerate(Activity context, SceneEntity currentScentity, int x, int y,
                                  int width, int height, List<MediAddEntity> listsEntity, boolean isLife, int playPosition,
                                  String screenPosition) {
        super(context, x, y, width, height);
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
        parpreViewToShow(TYPE_ONCREATE, "初始化布局+");
    }

    private static final int TYPE_ONCREATE = 0;
    private static final int TYPE_PLAY_NEXT = 1;
    private static final int TYPE_PLAY_POSITION = 3;

    /**
     * @param playType 0: 初始化 1：下一个  2：播放指定得位置
     * @param tag
     */
    Biantai bianTaiUtil;

    public void parpreViewToShow(int playType, String tag) {
        MyLog.playMix("========parpreViewToShow=" + playType + " / " + tag);
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

    /**
     * 这里用来同步数据
     *
     * @param object
     */
    @Override
    public void updateTextInfo(Object object) {
        MediAddEntity mediAddEntity = (MediAddEntity) object;
        listsEntity.clear();
        listsEntity.add(mediAddEntity);
        refrashPlayView("同步播放-刷新数据");
    }

    String ipaddressMine = "";

    private void refrashPlayView(String tag) {
        //每次播放，同步一下自己的IP数据
        ipaddressMine = CodeUtil.getIpAddress(context, "");
        MyLog.playMix("======混播开始布局==开始渲染环境=" + tag);
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
            playNextPargramView("数组越界，直接播放下一个");
            return;
        }
        MediAddEntity mediAddEntity = listsEntity.get(playPosition);
        if (mediAddEntity == null) {
            rela_no_data.setVisibility(View.VISIBLE);
            return;
        }

        clearMemoryCache();
        dissmissLastViews("release last");

        //通知其他设备，开始同步==
        sendStartToUploadPram(mediAddEntity, "通知其他设备，开始同步", currentScentity.getSenceId());
        rela_no_data.setVisibility(View.GONE);
        int fileType = mediAddEntity.getFileType();
        MyLog.playMix("====混播00000===" + mediAddEntity.toString() + " / " + fileType);

        RowCow rowCow = SharedPerManager.getShowScreenRowCow();
        PositionEntity positionEntity = TaskDealUtil.getViewSizeByRowCow(rowCow.position, rowCow.row, rowCow.cow);
        int startX = positionEntity.getLeftPosition();
        int startY = positionEntity.getTopPosition();
        int width = positionEntity.getWidth();
        int height = positionEntity.getHeight();

        switch (fileType) {
            case FileEntity.STYLE_FILE_IMAGE:
                generatorView = new ViewImageSingleUdpGenertrator(context, null, startX, startY, width, height, mediAddEntity);
                addViewListener(generatorView);
                ab_view.addView(generatorView.getView(), generatorView.getLayoutParams());
                generatorView.updateView(null, true);
                //sendProgressMessageToOtherTaskDev("", "");
                break;
            case FileEntity.STYLE_FILE_VIDEO:
//                generatorView = new ViewVideoUdpGenertrator(context, startX, startY, width, height, mediAddEntity, screenPosition, false);
                generatorView = new ViewUdpViewNewGenertrator(context, startX, startY, width, height, mediAddEntity, screenPosition, false);
                addViewListener(generatorView);
                ab_view.addView(generatorView.getView(), generatorView.getLayoutParams());
                generatorView.updateView(null, true);
                sendProgressMessageToOtherTaskDev(mediAddEntity.getUrl(), currentScentity.getSenceId());
                break;
        }
    }

    private void addViewListener(Generator generatorView) {
        if (generatorView == null) {
            return;
        }
        generatorList.add(generatorView);
        generatorView.setPlayStateChangeListener(new TaskPlayStateListener() {

            @Override
            public void playComplete(int playTag) {
                MyLog.playMix("===混播播放结束===playComplete=000=" + TaskPlayStateListener.getPlayTag(playTag) + "/屏幕的位置==" + screenPosition);
                if (listsEntity == null || listsEntity.size() < 1) {
                    return;
                }
                playNextPargramView("====playComplete===");
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
            MyLog.playMix("混播====移除View0000===" + isLife + "/" + tag + "/" + generatorListCache.size());
            if (generatorListCache.size() < 1) {
                return;
            }
            for (Generator genView : generatorListCache) {
                MyLog.playMix("混播====移除View0000===111--" + genView);
                genView.removeCacheView("vvvvvv");
                ab_view.removeView(genView.getView());
            }
            generatorListCache.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 主界面调用，直接清理所有得数据
     */
    @Override
    public void clearMemory() {
        MyLog.playMix("=======clearMemory=====001==");
        isLife = false;
        dissmissLastViews("clearMemory");
        if (generatorView != null) {
            generatorView.clearMemory();
        }
        if (progressDisposable != null) {
            progressDisposable.dispose();
        }
    }

    /**
     * 销毁以前得View
     */
    private void dissmissLastViews(String printTag) {
        MyLog.playMix("====clearMemory===dissmissLastViews=======" + printTag);
        handler.sendEmptyMessageDelayed(CLEAR_LAST_VIEW, AppConfig.Seamless_Switching_Time);
    }

    @Override
    public void timeChangeToUpdateView() {

    }

    private void clearMemoryCache() {
        if (progressDisposable != null) {
            progressDisposable.dispose();
        }
        generatorListCache.clear();
        try {
            for (Generator genView : generatorList) {
                MyLog.playMix("====混播，销毁布局==" + genView.getClass().getName());
                genView.clearMemory();
                generatorListCache.add(genView);
            }
            generatorList.clear();
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
        boolean isMainLeader = SharedPerUtil.getSameTaskMainLeader();
        if (listener != null || isMainLeader) {
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
        if (progressDisposable != null) {
            progressDisposable.dispose();
        }
        boolean mainLeader = SharedPerUtil.getSameTaskMainLeader();
        if (!mainLeader) {
            MyLog.playMix("===混播播放结束====从属设备，不配切换=");
            return;
        }
        MyLog.playMix("===混播播放结束====播放下一个=");
        playPosition++;
        if (playPosition >= listsEntity.size()) {
            playComplet();
            playPosition = 0;
        }
        parpreViewToShow(TYPE_PLAY_NEXT, "播放下一个一个素材00000 ---" + printTag);
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public void pauseDisplayView() {
        if (generatorList.size() < 1) {
            return;
        }
        for (int i = 0; i < generatorList.size(); i++) {
            generatorList.get(i).pauseDisplayView();
        }
    }

    @Override
    public void resumePlayView() {
        if (generatorList.size() < 1) {
            return;
        }
        for (int i = 0; i < generatorList.size(); i++) {
            generatorList.get(i).resumePlayView();
        }
    }

    /***
     * 通知其他设备开始同步消息
     * @param mediAddEntity
     */
    private void sendStartToUploadPram(MediAddEntity mediAddEntity, String tag, String secenId) {
        boolean mainLeader = SharedPerUtil.getSameTaskMainLeader();
        MyLog.udp("==sendStartToUploadPram=发送开始播放消息,tag==" + tag + "/" + secenId);
        if (!mainLeader) { //不是主关系
            MyLog.udp("==sendStartToUploadPram=发送开始播放消息,不是主关系==");
            return;
        }

//        String ipaddress = ipaddressMine.substring(0, ipaddressMine.lastIndexOf(".") + 1) + 255;
        String ipaddress = UDPSocket.ALL_HOST;
        String filePath = mediAddEntity.getUrl();
        int fileType = mediAddEntity.getFileType();
        String playParam = mediAddEntity.getPlayParam();  //播放时间
//        String sendMessage = UDPConfig.getStartUploadPrgramOrder(fileType, playParam, filePath, secenId);

        String sendMessage = Command.buildPlayCmd(fileType, playParam, filePath, secenId, playPosition);
        MyLog.udp("==sendStartToUploadPram=发送开始播放消息==" + sendMessage + "   / " + ipaddress);
        EtvService.getInstance().sendUdpMessage(sendMessage, ipaddress);
    }

    @Override
    public int getVideoPlayCurrentDuartion() {
        if (generatorView == null) {
            return -1;
        }
        return generatorView.getVideoPlayCurrentDuartion();
    }


    private void sendProgressMessageToOtherTaskDev(String filePath, String sencedId) {
        boolean mainLeader = SharedPerUtil.getSameTaskMainLeader();
        if (!mainLeader) { //不是主关系
            return;
        }
        if (progressDisposable != null && !progressDisposable.isDisposed()) {
            return;
        }
        progressDisposable = Observable
                .interval(2300, TimeUnit.MILLISECONDS)
                .doOnNext(timer -> {
                    if (TextUtils.isEmpty(filePath)) {
                        return;
                    }
                    int progress = getVideoPlayCurrentDuartion();
                    if (progress > -1) {
                        sendMessageToOther(filePath, progress, sencedId);
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    /***
     * 同步视频播放进度
     * @param progress
     */
    private void sendMessageToOther(String filePath, int progress, String sencenId) {
        String ipaddress = UDPSocket.ALL_HOST;
        String message = Command.buildProgressCmd(filePath, progress, sencenId);
        EtvService.getInstance().sendUdpMessage(message, ipaddress);
        MyLog.udp("==sendMessageToOther==" + message);
    }

    /***
     * 次屏  ，
     * @param sameTaskVideo
     */
    public void updateVideoPlayProgress(CmdData sameTaskVideo) {
        if (sameTaskVideo == null) {
            MyLog.playMix("====updateVideoPlayProgress==sameTaskVideo==null==");
            return;
        }
        if (generatorView instanceof ViewUdpViewNewGenertrator) {
            generatorView.updateTextInfo(sameTaskVideo);
        }
    }
}
