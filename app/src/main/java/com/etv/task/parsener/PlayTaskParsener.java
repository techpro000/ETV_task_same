package com.etv.task.parsener;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.widget.AbsoluteLayout;

import com.EtvApplication;
import com.diff.presentation.DifferentDislay;
import com.etv.config.AppConfig;
import com.etv.config.AppInfo;
import com.etv.db.DbDevMedia;
import com.etv.entity.ScreenEntity;
import com.etv.http.util.GetFileFromPathForRunnable;
import com.etv.listener.TaskPlayStateListener;
import com.etv.service.EtvService;
import com.etv.service.TaskWorkService;
import com.etv.task.db.DBTaskUtil;
import com.etv.task.db.DbTaskManager;
import com.etv.task.entity.CacheMemory;
import com.etv.task.entity.CpListEntity;
import com.etv.task.entity.MediAddEntity;
import com.etv.task.entity.MpListEntity;
import com.etv.task.entity.PositionEntity;
import com.etv.task.entity.SceneEntity;
import com.etv.task.entity.TaskWorkEntity;
import com.etv.task.entity.TextInfo;
import com.etv.task.model.TaskGetDbListener;
import com.etv.task.model.TaskModelUtil;
import com.etv.task.model.TaskModelmpl;
import com.etv.task.model.TaskMudel;
import com.etv.task.model.TaskRequestListener;
import com.etv.task.util.TaskDealUtil;
import com.etv.task.view.PlayTaskView;
import com.etv.util.FileUtil;
import com.etv.util.MyLog;
import com.etv.util.SharedPerManager;
import com.etv.util.SimpleDateUtil;
import com.etv.util.TimerDealUtil;
import com.etv.util.image.glide.GlideCacheUtil;
import com.etv.util.system.SystemManagerInstance;
import com.etv.util.system.VoiceManager;
import com.etv.view.layout.Generator;
import com.etv.view.layout.mixedswing.ViewImgMixSameGenerate;
import com.etv.view.layout.mixedswing.ViewImgVideoNetGenerate;
import com.udpsync.bean.CmdData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlayTaskParsener {

    PlayTaskView playTaskView;
    Activity context;
    TaskMudel taskModel;
    AbsoluteLayout view_abous;
    List<SceneEntity> sceneEntityListMain = new ArrayList<>();  //需要播放的场景集合
    List<SceneEntity> sceneEntityListSecond = new ArrayList<>(); //需要播放的场景集合
    private boolean isSameTaskLeader = false;   //同步任务 是否是 leader
    boolean isHasTaskScreenSame = false;    //是否是连屏任务

    public void setTaskSameLeader(boolean sameLeader) {
        this.isSameTaskLeader = sameLeader;
    }

    public PlayTaskParsener(Activity context, PlayTaskView playTaskView) {
        this.context = context;
        this.playTaskView = playTaskView;
        taskModel = new TaskModelmpl();
        view_abous = playTaskView.getAbsoluteLayout();
    }

    public void getTaskToView(String tag) {
        //播放之前先去清理多余得素材
        checkTaskDownFile();
        MyLog.playTask("========任务播放界面刷新=getTaskToView=====" + tag);
        try {
            sceneEntityListMain.clear();
            sceneEntityListSecond.clear();
            currentSencenPosition = 0;
            taskModel.getPlayTaskListFormDb(new TaskGetDbListener() {
                @Override
                public void getTaskFromDb(List<TaskWorkEntity> list) {
                    if (list == null || list.size() < 1) {
                        MyLog.playTask("======播放界面===没有获取到需要播放的任务，去检查插播消息");
                        return;
                    }
                    MyLog.playTask("======检测到同步任务模式了，没有同步任务，这里去解析其他模式任务==");
                    if (view_abous != null) {
                        view_abous.removeAllViews();
                    }
                    //清理副屏播放得信息
                    clearLastDoubleScreenView(TAG_CLEARVIEW_ONDESTORY);
                    parsenerTaskFromDb(list);
                }

                @Override
                public void getTaskTigerFromDb(TaskWorkEntity taskWorkEntity) {

                }
            }, "====播放界面，这里获取任务数据====", TaskModelUtil.DEL_LASTDATE_AND_AFTER_NOW);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 解析通用版本得 TASK
     * @param taskWorkEntityList
     */
    private void parsenerTaskFromDb(List<TaskWorkEntity> taskWorkEntityList) {
        List<MpListEntity> mpListEntities = DBTaskUtil.getMpListInfoAll();
        boolean isFileExict = TaskDealUtil.compairMpListFileExict(mpListEntities);
        if (!isFileExict) {
            playTaskView.showViewError("没有素材信息需要播放");
            return;
        }
        List<SceneEntity> sceneEntityListCache = new ArrayList<>(); //需要播放的场景集合
        for (int i = 0; i < taskWorkEntityList.size(); i++) {
            TaskWorkEntity taskWorkEntity = taskWorkEntityList.get(i);
            String taskId = taskWorkEntity.getTaskId();
            EtvService.getInstance().updateProgressToWebRegister("进入界面，提交一次", taskId, "", 100, 0, "-1");
            if (taskWorkEntity == null) {
                break;
            }
            List<SceneEntity> listCacheSenc = DbTaskManager.getSencenEntityFormDbByTask(taskWorkEntity);
            if (listCacheSenc != null && listCacheSenc.size() > 0) {
                sceneEntityListCache.addAll(listCacheSenc);
            }
        }
        if (sceneEntityListCache == null || sceneEntityListCache.size() < 1) {
            playTaskView.showViewError("获取任务场景失败");
            return;
        }
        EtvApplication.getInstance().setTaskWorkEntityList(taskWorkEntityList);
        MyLog.playTask("====当前节目由多少个场景==" + sceneEntityListCache.size());
        for (int i = 0; i < sceneEntityListCache.size(); i++) {
            SceneEntity sceneEntity = sceneEntityListCache.get(i);
            MyLog.playTask("====遍历场景==" + "position=" + i + " / " + sceneEntity.toString());
            String disPosition = sceneEntity.getDisplayPos();
            if (disPosition == null || disPosition.length() < 1) { //防止NULL的情况
                sceneEntityListMain.add(sceneEntity);
            } else if (disPosition.contains(AppInfo.PROGRAM_POSITION_MAIN)) {
                sceneEntityListMain.add(sceneEntity);
                MyLog.playTask("====遍历场景==主频添加===" + sceneEntity.toString());
            } else if (disPosition.contains(AppInfo.PROGRAM_POSITION_SECOND)) {
                sceneEntityListSecond.add(sceneEntity);
                MyLog.playTask("====遍历场景==副频添加===" + sceneEntity.toString());
            }
        }
        //这里为了防止客户主副平下发一摸一样得节目，导致数据库保存得信息一致得问题
        if (sceneEntityListMain == null || sceneEntityListMain.size() < 1) {
            MyLog.playTask("====主屏没有素材=======0000=====");
            if (sceneEntityListSecond != null && sceneEntityListSecond.size() > 0) {
                MyLog.playTask("====主屏没有素材=====11111=======");
                sceneEntityListMain.addAll(sceneEntityListSecond);
                sceneEntityListSecond.clear();
            }
        }
        if (sceneEntityListMain == null || sceneEntityListMain.size() < 1) {
            playTaskView.showViewError("获取主界面任务场景失败");
            return;
        }
        getPmFromTask(currentSencenPosition, 0, "parsenerTaskFromDb");
    }

    //从任务中获取节目
    List<CpListEntity> cpCacheList = new ArrayList<>();
    //获取播放任务,任务检测，从这里开始
    int currentSencenPosition = 0;  //播放场景的位置

    /****
     *
     * @param position
     * 播放的位置
     * 释放时触摸任务
     * @param printTag
     * 打印标签
     */
    private void getPmFromTask(int position, int playIndex, String printTag) {
        TimerDealUtil.getInstance().removeAllGenerator();
        MyLog.playTask("====谁在切换节目000==根据位置获取场景");
        SceneEntity currentSceneEntity = sceneEntityListMain.get(position);
        MyLog.playTask("====谁在切换节目==" + printTag + " /position=" + position + " /id==" + currentSceneEntity.getSenceId());
        currentSencenPosition = position;
        try {
            clearMemory();    //这里会涉及到重复调用，所以会先清理一次内存
            MyLog.task("=====准备更新温度===清空View===" + " / " + genratorViewList);
            genratorViewList.clear();   //清理控件集合
            if (currentSceneEntity == null) {
                playTaskView.showViewError("获取场景信息异常");
                return;
            }
            String sencenId = currentSceneEntity.getSenceId();   // 场景得ID
            List<CpListEntity> cpList = DbTaskManager.getComptionFromDbBySenId(sencenId);
            if (cpList == null || cpList.size() < 1) {
                playTaskView.showViewError("获取控件异常");
                return;
            }
            MyLog.playTask("====添加到集合的顺序==" + cpList.size());
            //对控件进行排序
            cpCacheList.clear();
            cpCacheList = TaskDealUtil.mathCpListOrder(cpList);
            MyLog.playTask("====添加到集合的顺序00排序后的个数==" + cpCacheList.size());
            MyLog.playTask("=====当前播放的场景ID===" + sencenId);
            for (int i = 0; i < cpCacheList.size(); i++) {
                CpListEntity cpListEntity = cpCacheList.get(i);
                parperToShowView(cpListEntity, playIndex);
            }
//            //检测双屏任务. put this info to CLEAR_LAST_VIEW_FROM_LIST()
//            if (sceneEntityListSecond != null && sceneEntityListSecond.size() > 0) {
//                getDevScreenNum();
//            } else { //单屏任务，这里需要dissmiss 副屏，防止双屏，单屏任务切换
//                if (myPresentation != null) {
//                    myPresentation.dismiss();
//                    myPresentation = null;
//                }
//            }
            handler.sendEmptyMessageDelayed(CLEAR_LAST_VIEW_FROM_LIST, AppConfig.Seamless_Switching_Time + 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取当前场景得播放时间
    private int getCurrentSencenPlayTime() {
        int backTime = 0;
        if (sceneEntityListMain == null || sceneEntityListMain.size() < 2) {
            return backTime;
        }
        SceneEntity sceneEntity = sceneEntityListMain.get(currentSencenPosition);
        if (sceneEntity == null) {
            return backTime;
        }
        String scenTime = sceneEntity.getScTime();
        if (scenTime == null || scenTime.length() < 1) {
            return backTime;
        }
        try {
            backTime = Integer.parseInt(scenTime);
            if (backTime < 5) {
                backTime = 0;
            }
        } catch (Exception e) {
            backTime = 0;
        }
        return backTime;
    }

    //用来封装播放view的
    List<CacheMemory> genratorViewList = new ArrayList<CacheMemory>();
    //屏幕得宽高d'd
    int screenWidth = SharedPerManager.getScreenWidth();
    int screenHeight = SharedPerManager.getScreenHeight();
    List<MpListEntity> mpList = new ArrayList<MpListEntity>();     //控件的素材信息

    public void parperToShowView(CpListEntity cpEntity, int playIndex) {
        if (cpEntity == null) {
            playTaskView.showViewError("控件解析失败");
            return;
        }
        try {
            if (mpList == null) {
                mpList = new ArrayList<>();
            }
            mpList.clear();
            PositionEntity positionEntity = TaskDealUtil.getCpListPosition(cpEntity);
            int leftPosition = positionEntity.getLeftPosition();
            int topPosition = positionEntity.getTopPosition();
            int width = positionEntity.getWidth();
            int height = positionEntity.getHeight();
            String coType = cpEntity.getCoType();             //控件类型
            int viewWidth = leftPosition + width;
            int viewHeight = topPosition + height;
            if (leftPosition < 8 && leftPosition > 0) {
                leftPosition = 0;
            }
            if (topPosition < 8 && topPosition > 0) {
                topPosition = 0;
            }
            //控件的宽度 - 屏幕的宽度
            int distanceWidth = Math.abs(viewWidth - screenWidth);
            if (distanceWidth < 8) {
                width = screenWidth - leftPosition;
            }
            int distanceHeight = Math.abs(viewHeight - screenHeight);
            if (distanceHeight < 8) {
                height = screenHeight - topPosition;
            }
            MyLog.playTask("====布局的坐标点1111==>>" + coType + "/cpEntityId=" + cpEntity.getCpidId() + " / " + " / " + leftPosition + " / " + topPosition + " / " + width + " / " + height);
            if (TaskDealUtil.isResourceType(coType)) {   //资源类型
                MyLog.playTask("====布局的坐标点1111==>>资源类型");
                mpList = cpEntity.getMpList();
            }
            switch (coType) {
                case AppInfo.VIEW_IMAGE_VIDEO: //混播模式
                    List<MediAddEntity> mixtureList = TaskDealUtil.getResourceListPath(mpList);
                    if (mixtureList == null || mixtureList.size() < 1) {
                        return;
                    }
                    SceneEntity currentScentity = getCurrentSencenEntity();
                    MyLog.playTask("混播===列表数量===" + mixtureList.size());
                    viewImgMixSameGenerate = new ViewImgMixSameGenerate(context, currentScentity, leftPosition, topPosition, width, height, mixtureList, true, playIndex, AppInfo.PROGRAM_POSITION_MAIN);
                    addViewToList(viewImgMixSameGenerate, coType, false);
                    view_abous.addView(viewImgMixSameGenerate.getView(), viewImgMixSameGenerate.getLayoutParams());
                    break;
            }
        } catch (Exception e) {
            MyLog.ExceptionPrint("播放界面布局异常: " + e.toString());
            e.printStackTrace();
        }
    }


    /***
     *增加到管理View中，统一清理缓存
     * @param generatorView
     * View
     * @param coType
     * View类型
     * @param isRelation
     * 是否是关联控件
     */
    public void addViewToList(Generator generatorView, String coType, boolean isRelation) {
        MyLog.playTask("======添加view到集合中，类型=" + coType + " /是否是关联==" + isRelation);
        try {
            genratorViewList.add(new CacheMemory(generatorView, coType, isRelation));
            generatorView.setPlayStateChangeListener(new TaskPlayStateListener() {

                @Override
                public void playComplete(int playTag) {
                    MyLog.playTask("=====播放完毕回调====playTag=" + playTag);
                    updateTotalShowSizeToWeb(); //提交统计信息给服务器
                    if (generatorView.getClass() == ViewImgVideoNetGenerate.class) {
                        //混播得View 得和 混播得标记融合
                        if (playTag == TaskPlayStateListener.TAG_PLAY_VIDEO_IMAGE) {
                            changeProjectView(playTag);
                        }
                        return;
                    }
                    changeProjectView(playTag);
                }

                /**
                 * 这是同步播放的单个节目播放完毕回调
                 * @param playTag
                 */
                @Override
                public void playCompletePosition(String etLevel, String taskId, int currentPlayPosition, int playTag) {
                    if (playTag != TaskPlayStateListener.TAG_PLAY_VIDEO_IMAGE) {
                        return;
                    }
                }

                @Override
                public void reStartPlayProgram(String errorDesc) {
                    getTaskToView("播放异常，重启播放一次");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //提交统计信息
    private void updateTotalShowSizeToWeb() {
        //上传之前，判断一下，有没有权限上传记录
        boolean playUpdate = SharedPerManager.getPlayTotalUpdate();
        if (!playUpdate) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction(EtvService.UPDATE_STA_TOTAL_TO_WEB);
        context.sendBroadcast(intent);
    }

    /**
     * 修改字幕信息
     *
     * @param cpListEntity
     * @param textModify
     */
    private void modifyTextInfoToWeb(CpListEntity cpListEntity, String textModify) {
        String cpId = cpListEntity.getCpidId();
        List<TextInfo> txList = DBTaskUtil.getTxtListInfoById(cpId);
        if (txList == null || txList.size() < 1) {
            return;
        }
        TextInfo textInfo = null;
        for (int i = 0; i < txList.size(); i++) {
            textInfo = txList.get(i);
            int parsentType = textInfo.getParentCoId();
            if (parsentType == DBTaskUtil.MP_DEFAULT) {
                break;
            }
        }
        String textId = textInfo.getTxtId();
        taskModel.modifyTextInfoToWeb(textId, textModify, new TaskRequestListener() {
            @Override
            public void modifyTxtInfoStatues(boolean isSuccess, String desc) {
                if (!isSuccess) {
                    playTaskView.showToastView(desc);
                    return;
                }
                playTaskView.findTaskNew();
            }

            @Override
            public void playNextProgram(boolean isBack, List<SceneEntity> sceneEntities, int tag) {

            }

            @Override
            public void finishMySelf(String errorDesc) {

            }

            @Override
            public void parserJsonOver(String tag) {

            }
        });
    }

    //用于无缝切换得延时操作
    private static final int CLEAR_LAST_VIEW_FROM_LIST = 5614;
    //用于双屏任务无缝切换
    public static final int CLEAR_DOUBLE_LAST_VIEW_FROM_LIST = 5615;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handler.removeMessages(msg.what);
            switch (msg.what) {
                case CLEAR_DOUBLE_LAST_VIEW_FROM_LIST: //用来清除副屏的缓存View用于无缝切换
                    //同步模式，这里需要优先删除数据
                    if (isHasTaskScreenSame && myPresentation != null) {
                        //同步模式
                        MyLog.diff("============parsener---准备移除View了");
                        myPresentation.clearLastDiffView();
                        return;
                    }
                    boolean isScreenSame = isLinkDoubleScreen();
                    if (isScreenSame) {
                        return;
                    }
                    if (myPresentation != null) {
                        MyLog.diff("============parsener---准备移除View了");
                        myPresentation.clearLastDiffView();
                    }
                    break;
                case CLEAR_LAST_VIEW_FROM_LIST: //清理上一次的View
                    clearLastView(TAG_CLEARVIEW_HANDLER, "开始绘制节目，延时清理界面");
                    setPlaySecondView();
                    break;
            }
        }
    };


    /***
     *开始加载副屏内容
     *这里表示主妇屏同时切换
     */
    private void setPlaySecondView() {
        //检测双屏任务
        if (sceneEntityListSecond != null && sceneEntityListSecond.size() > 0) {
            getDevScreenNum();
        } else {
            //单屏任务，这里需要dissmiss 副屏，防止双屏，单屏任务切换
            if (myPresentation != null) {
                myPresentation.dismiss();
                myPresentation = null;
            }
        }
    }

    /**
     * 获取当前正在播放的场景
     *
     * @return
     */
    public SceneEntity getCurrentSencenEntity() {
        if (sceneEntityListMain == null || sceneEntityListMain.size() < 1) {
            return null;
        }
        return sceneEntityListMain.get(currentSencenPosition);
    }

    /***
     * 获取任务
     * 获取节目，获取节目中的控件
     * 视频优先。音频次之，图片次之
     * @param playTag
     * 这是测试代码
     */
    private void changeProjectView(int playTag) {
        if (sceneEntityListMain == null || sceneEntityListMain.size() < 1) {
            MyLog.playTask("===当前只有一个节目，不跳转====");
            return;
        }
        SceneEntity sceneEntity = sceneEntityListMain.get(currentSencenPosition);
        if (sceneEntity == null) {
            return;
        }
        String pmType = sceneEntity.getPmType();
        MyLog.playTask("========检查节目得类型==" + pmType);
        //当前有场景切换时间，这里中断操作
        int scenPlayTime = getCurrentSencenPlayTime();
        if (scenPlayTime > 4) {
            //有场景时间，这里中断操作
            MyLog.playTask("=====场景有设定播放时间，这里中断操作====");
            return;
        }
        MyLog.playTask("=====changeProjectView====" + playTag);
        taskModel.playNextProgram(sceneEntityListMain, currentSencenPosition, playTag, new TaskRequestListener() {
            @Override
            public void modifyTxtInfoStatues(boolean isSuccess, String desc) {

            }

            @Override
            public void playNextProgram(boolean isBack, List<SceneEntity> sceneEntities, int tag) {
                if (!isBack) {
                    return;
                }
                toPlayNextProject(sceneEntities);
            }

            @Override
            public void finishMySelf(String errorDesc) {

            }

            @Override
            public void parserJsonOver(String tag) {

            }
        });
    }

    /**
     * 去播放下一个节目
     *
     * @param sceneEntities
     */
    private void toPlayNextProject(List<SceneEntity> sceneEntities) {
        try {
            currentSencenPosition++;
            if (currentSencenPosition > (sceneEntities.size() - 1)) {
                currentSencenPosition = 0;
            }
            MyLog.playTask("====播放结束了，切换节目===" + currentSencenPosition + " / " + sceneEntities.size());
            MyLog.playTask("当前只有" + sceneEntities.size() + "个节目,执行下一步操作");
            getPmFromTask(currentSencenPosition, 0, "==去播放下一个节目==");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    DifferentDislay myPresentation = null;

    /***
     * 双屏界面显示
     * 1：单品节目两个屏幕显示一样
     * 2：双屏幕节目 显示双节目
     */
    public void getDevScreenNum() {
        try {
            List<ScreenEntity> screenEntityList = EtvApplication.getInstance().getListScreen();
            if (screenEntityList == null || screenEntityList.size() < 2) {
                MyLog.diff("33333====haha==当前屏幕得个数= 0 或者 1");
                return;
            }
            MyLog.diff("33333====haha==当前屏幕得个数=" + screenEntityList.size());
            ScreenEntity screenEntity = screenEntityList.get(1);
            Display display = screenEntity.getDisplay();
            int width = screenEntity.getScreenWidth();
            int height = screenEntity.getScreenHeight();
            boolean isScreenSame = isLinkDoubleScreen();
            MyLog.diff("33333====haha===" + width + " / " + height + " /是否联动== " + isScreenSame + " / " + (myPresentation == null));
            if (myPresentation == null) {
                myPresentation = new DifferentDislay(context, display, width, height, handler);
                myPresentation.show();
            }
            myPresentation.setPlayList(sceneEntityListSecond, currentSencenPosition, taskModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停播放界面
     */
    public void pauseDisplayView() {
        if (myPresentation != null) {
            myPresentation.pauseDisplayView();
        }
        if (view_abous == null) {
            return;
        }
        if (genratorViewList == null || genratorViewList.size() < 1) {
            return;
        }
        for (int i = 0; i < genratorViewList.size(); i++) {
            Generator genView = genratorViewList.get(i).getGenerator();
            genView.pauseDisplayView();
        }
    }

    /**
     * 恢复播放
     */
    public void resumePlayView() {
        MyLog.playTask("=====恢复播放的功能==============");
        if (myPresentation != null) {
            myPresentation.resumePlayView();
        }
        if (view_abous == null) {
            return;
        }
        if (genratorViewList == null || genratorViewList.size() < 1) {
            return;
        }
        for (int i = 0; i < genratorViewList.size(); i++) {
            Generator genView = genratorViewList.get(i).getGenerator();
            genView.resumePlayView();
        }
    }

    /**
     * 获取当前是否是双屏联动效果
     *
     * @return
     */
    public boolean isLinkDoubleScreen() {
        SceneEntity sceneEntity = getCurrentSencenEntity();
        return TaskDealUtil.isLinkDoubleScreen(sceneEntity);
    }


    //用来缓存上一组没有被清掉的View,下一次加载完毕，在次清理上一组的View
    List<Generator> lastCache = new ArrayList<Generator>();

    /**
     * 1:表示正常的清理View
     * Handler 清理
     * -1 表示界面执行onStop onDestory
     * 需要清理副屏任务
     *
     * @param tag
     */
    public void clearLastView(int tag, String printTag) {
        MyLog.playTask("=======clearLastView==" + tag + " / " + printTag);
        MyLog.playTask("=======clearLastView==" + lastCache.size());
        try {
            clearLastDoubleScreenView(tag);
            handler.removeMessages(CLEAR_LAST_VIEW_FROM_LIST);
            if (lastCache == null || lastCache.size() < 1) {
                return;
            }
            for (Generator generator : lastCache) {
                view_abous.removeView(generator.getView());
                generator.removeCacheView("clearLastView-parsener");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 1:表示正常的清理View
     * Handler 清理
     * -1 表示界面执行onStop onDestory
     * 需要清理副屏任务
     *
     * @param tag
     */
    public static final int TAG_CLEARVIEW_ONDESTORY = -1;
    public static final int TAG_CLEARVIEW_HANDLER = 1;

    private void clearLastDoubleScreenView(int tag) {
        if (myPresentation == null) {
            return;
        }
        MyLog.playTask("======clearLastDoubleScreenView====" + tag);
        if (tag < 0) {
            myPresentation.clearMemory("===clearLastDoubleScreenView==");
            myPresentation.clearLastDiffView();
            myPresentation.dismiss();
            myPresentation = null;
            return;
        }
        myPresentation.clearLastDiffView();
    }

    /***
     * 清理View缓存
     */
    public void clearMemory() {
        lastCache.clear();
        try {
            GlideCacheUtil.getInstance().clearImageAllCache(context);
            //去判断一下，如果是互动节目,就直接切换不做无缝，普通节目得话需要无缝切换操作
            String taskType = "1";
            SceneEntity currScenty = getCurrentSencenEntity();
            if (currScenty != null) {
                taskType = currScenty.getPmType();
            }
            MyLog.playTask("=====清理缓存一次==任务类型==" + taskType);
            if (genratorViewList != null || genratorViewList.size() > 0) {
                for (int i = 0; i < genratorViewList.size(); i++) {
                    Generator genView = genratorViewList.get(i).getGenerator();
                    genView.clearMemory();
                    lastCache.add(genView);
                }
            }
            boolean iScreenSame = isLinkDoubleScreen();//双屏联动
            if (iScreenSame) {
                if (myPresentation != null && myPresentation.isShowing()) {
                    MyLog.playTask("=====清理缓存 副屏移除VIEW====");
                    myPresentation.clearMemory("======playParsener==clearMemory========");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭副屏得背光
     */
    public void shutDownDiffScreenLight() {
        if (myPresentation != null) {
            myPresentation.clearMemory("===shutDownDiffScreenLight===");
        }
    }

    /**
     * 更新媒体的声音
     * 修稿Logo信息
     */
    public void updateMediaVoiceNum() {
        int mediaNum = DbDevMedia.getCurrentMediaVoice();
        if (mediaNum < 0) {
            return;
        }
        mediaNum = mediaNum * 15 / 100;
        int currentNum = VoiceManager.getInstance(context).getCurrentVoiceNum();
        if (currentNum == mediaNum) {
            return;
        }
        boolean isBackOn = SystemManagerInstance.getInstance(context).getBackLightTtatues("任务播放Parsener检测休眠zhuangtai");
        //休眠状态，不修改音量
        if (isBackOn) {
            VoiceManager.getInstance(context).setMediaVoiceNum(mediaNum);
        }
    }

    /**
     * 清理多余得素材
     */
    private void checkTaskDownFile() {
        long currentTime = SimpleDateUtil.getCurrentTimelONG();
        if (currentTime < AppConfig.TIME_CHECK_POWER_REDUCE) {
            MyLog.cdl("系统时间不对，不删除文件", true);
            return;
        }
        List<MpListEntity> listEntities = DBTaskUtil.getTaskListInfoAll();
        if (listEntities == null || listEntities.size() < 1) {
            //素材列表没有数据,
            return;
        }
        String path = AppInfo.BASE_TASK_URL();
        File file = new File(path);
        if (!file.exists()) {
            FileUtil.MKDIRSfILE(path);
            return;
        }
        GetFileFromPathForRunnable runnable = new GetFileFromPathForRunnable(path, new GetFileFromPathForRunnable.QueryFileFromPathListener() {
            @Override
            public void backFileList(boolean isSuccess, List<File> listFileSearch, String errorDesc) {
                if (!isSuccess) {
                    return;
                }
                if (listFileSearch == null || listFileSearch.size() < 1) {
                    return;
                }
                compairDbFileAndlocal(listEntities, listFileSearch);
            }
        });
        EtvService.getInstance().executor(runnable);
    }

    private void compairDbFileAndlocal(List<MpListEntity> listEntities, List<File> listFileSearch) {
        MyLog.playTask("compairDbFileAndlocal==" + listEntities.size() + " / " + listFileSearch.size());
        List<File> listFilelocal = new ArrayList<>();
        listFilelocal.addAll(listFileSearch);
        for (MpListEntity mpListEntity : listEntities) {
            String webFileUrl = mpListEntity.getUrl();
            String webFileName = webFileUrl.substring(webFileUrl.lastIndexOf("/") + 1);
            for (File fileLocal : listFileSearch) {
                String fileLocalName = fileLocal.getName();
                if (webFileName.startsWith(fileLocalName)) {
                    listFilelocal.remove(fileLocal);
                    MyLog.playTask("compairDbFileAndlocal 名字相同==" + webFileName + " / " + fileLocal);
                }
            }
        }
        if (listFilelocal == null || listFilelocal.size() < 1) {
            MyLog.playTask("compairDbFileAndlocal 没有多余的素材删除==");
            return;
        }
        MyLog.playTask("compairDbFileAndlocal 有多余的素材删除==" + listFilelocal.size());
        for (int i = 0; i < listFilelocal.size(); i++) {
            String filePath = listFilelocal.get(i).getPath();
            MyLog.playTask("compairDbFileAndlocal 有多余的素材删除000==" + filePath);
            FileUtil.deleteDirOrFilePath(filePath, "比对数据库,删除多余得素材");
        }
    }

    /***
     * 同步屏幕得播放进度
     * @param sameTaskVideo
     */
    public void updateSameTaskVideoProgress(CmdData sameTaskVideo) {
        if (sameTaskVideo == null) {
            return;
        }
        if (sceneEntityListMain == null || sceneEntityListMain.size() < 1) {
            MyLog.udp("=======viewImgMixSameGener==sceneEntityListMain==null====");
            return;
        }
        if (TaskWorkService.getCurrentTaskType() != TaskWorkService.TASK_TYPE_DEFAULT) {
            MyLog.udp("当前正在下载，这里拦截操作");
            return;
        }
        String senIdReceive = sameTaskVideo.secenId;
        String curreentSenId = sceneEntityListMain.get(currentSencenPosition).getSenceId();
        MyLog.udp("=======viewImgMixSameGener==senIdReceive=" + senIdReceive + " / " + curreentSenId);
        if (curreentSenId.equals(senIdReceive)) {
            //播放时同一个视频，这里去同步进度
            MyLog.udp("=======播放逻辑开始同步进度====" + (viewImgMixSameGenerate == null));
            if (viewImgMixSameGenerate != null) {
                viewImgMixSameGenerate.updateVideoPlayProgress(sameTaskVideo);
            }
            return;
        }
    }


    ViewImgMixSameGenerate viewImgMixSameGenerate;

    /****
     * 同步模式 开始同步主屏得任务
     * @param cmdData
     */
    public void startToUploadMainScreenProgram(CmdData cmdData) {
        if (cmdData == null) {
            return;
        }
        if (sceneEntityListMain == null || sceneEntityListMain.size() < 1) {
            return;
        }
        String sencentId = cmdData.secenId;
        for (int i = 0; i < sceneEntityListMain.size(); i++) {
            SceneEntity sceneEntity = sceneEntityListMain.get(i);
            String secId = sceneEntity.getSenceId();
            MyLog.udp("Parsener 接收到播放指令==secId=" + secId + " / " + sencentId);
            if (secId.equals(sencentId)) {
                MyLog.udp("Parsener 接收到播放指令==开始同步播放任务=" + i);
                currentSencenPosition = i;
                getPmFromTask(currentSencenPosition, cmdData.pos, "开始同步播放任务");
                break;
            }
        }
    }

}
