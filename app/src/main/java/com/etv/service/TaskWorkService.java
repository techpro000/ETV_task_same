package com.etv.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.EtvApplication;
import com.etv.activity.MainActivity;
import com.etv.activity.StartActivity;
import com.etv.config.ApiInfo;
import com.etv.config.AppConfig;
import com.etv.config.AppInfo;
import com.etv.http.util.GetFileFromPathForRunnable;
import com.etv.service.util.TaskServiceParsener;
import com.etv.setting.update.entity.UpdateInfo;
import com.etv.setting.update.parsener.UpdateParsener;
import com.etv.setting.update.view.UpdateView;
import com.etv.socket.online.SocketWebListener;
import com.etv.task.activity.PlayerTaskActivity;
import com.etv.task.db.DBTaskUtil;
import com.etv.task.entity.DownStatuesEntity;
import com.etv.task.entity.TaskDownEntity;
import com.etv.task.entity.TaskWorkEntity;
import com.etv.task.model.TaskCheckLimitListener;
import com.etv.task.model.TaskGetDbListener;
import com.etv.task.model.TaskGetDownListListener;
import com.etv.task.model.TaskModelUtil;
import com.etv.task.model.TaskModelmpl;
import com.etv.task.model.TaskMudel;
import com.etv.task.parsener.TaskParsener;
import com.etv.task.util.TaskDealUtil;
import com.etv.task.view.TaskView;
import com.etv.util.Biantai;
import com.etv.util.FileUtil;
import com.etv.util.MyLog;
import com.etv.util.NetWorkUtils;
import com.etv.util.RootCmd;
import com.etv.util.SharedPerManager;
import com.etv.util.SimpleDateUtil;
import com.etv.util.down.DownFileEntity;
import com.etv.util.down.DownRunnable;
import com.etv.util.down.DownStateListener;
import com.etv.util.guardian.GuardianUtil;
import com.etv.util.rxjava.AppStatuesListener;
import com.etv.util.rxjava.RxBus;
import com.etv.util.rxjava.event.RxEvent;
import com.etv.util.sdcard.MySDCard;
import com.etv.util.system.LeaderBarUtil;
import com.ys.model.dialog.MyToastView;
import com.ys.model.util.ActivityCollector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TaskWorkService extends Service implements TaskView {

    public static final String GET_TASK_FROM_WEB_TAG = "GET_TASK_FROM_WEB_TAG";  //从服务器中获取任务
    public static final String GET_TASK_FROM_WEB_NO_DOWN = "GET_TASK_FROM_WEB_NO_DOWN";  //从服务器中获取任务,但是不下载
    public static final String UPDATE_APK_IMG_INFO = "UPDATE_APK_IMG_INFO";  //升级软件

    //取消显示悬浮窗
    public static final String DISSMISS_DOWN_POOP_WINDOW = "DISSMISS_DOWN_POOP_WINDOW";
    //整体干掉悬浮窗
    public static final String DESTORY_DOWN_POOP_WINDOW = "DESTORY_DOWN_POOP_WINDOW";

    public static TaskWorkService instance;
    public static boolean isDownApkImg = false;  //判断升级任务是否在下载

    public static final int TASK_TYPE_DEFAULT = 0;  //空闲状态
    public static final int TASK_TYPE_REQUEST = 1;  //请求状态
    public static final int TASK_TYPE_PARSENER_JSON = 2;  //解析状态
    public static final int TASK_TYPE_DOWN_WAIT = 3;  //下载等待状态
    public static final int TASK_TYPE_DOWNING = 4;  //下载状态
    public static int TASK_CURRENT_TYPE = TASK_TYPE_DEFAULT;

    /***
     * 设置当前状态
     * @param type
     */
    public static void setCurrentTaskType(int type, String tag) {
        TASK_CURRENT_TYPE = type;
        MyLog.task("====task当前状态===" + tag);
    }

    /***
     * 获取当前任务状态
     * @return
     */
    public static int getCurrentTaskType() {
        return TASK_CURRENT_TYPE;
    }


    TaskParsener taskParsener;

    public static TaskWorkService getInstance() {
        if (instance == null) {
            synchronized (TaskWorkService.class) {
                if (instance == null) {
                    instance = new TaskWorkService();
                }
            }
        }
        return instance;
    }

    @TargetApi(Build.VERSION_CODES.N)
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            MyLog.task("=====接受到发送的任务==" + action);
            if (action.equals(GET_TASK_FROM_WEB_TAG)) {      //从服务器中去取任务
                String tag = intent.getStringExtra(GET_TASK_FROM_WEB_TAG);
                MyLog.task("========准备请求任务信息===" + tag);
                requestTaskInfo("BroadcastReceiver");
            } else if (action.equals(UPDATE_APK_IMG_INFO)) {
                //升级软件APK
                stopDownApkImg();
                updateApkImageInfo();
            } else if (action.equals(AppInfo.MESSAGE_CLEAR_UPDATE_IMG_APK)) { //中断APK。img下载
                stopDownApkImg();
            } else if (action.equals(Intent.ACTION_TIME_TICK)) {
                MyLog.timer("=====TaskService==时间变化,去检测任务");
                checkTaskOnTimeEver();
            } else if (action.equals(AppInfo.STOP_DOWN_TASK_RECEIVER)) {
                //停止下载任务
                closeDownTask();
                stopDownApkImg();
                if (handler != null) {
                    handler.removeMessages(CHECK_LIMIT_DOWN_NUM);
                }
            } else if (action.equals(AppInfo.NET_ONLINE)) {
                boolean isPlayActivityIsForst = ActivityCollector.isForeground(context, PlayerTaskActivity.class.getName());
                if (isPlayActivityIsForst) {
                    checkTaskOnTimeEver();
                }
            } else if (action.equals(AppInfo.NET_DISONLINE)) {
                //网络断开，停止下载
                closeDownTask();
                stopDownApkImg();
                //界面在前台才去检测
                boolean isPlayActivityIsForst = ActivityCollector.isForeground(context, PlayerTaskActivity.class.getName());
                if (isPlayActivityIsForst) {
                    checkTaskOnTimeEver();
                }
            } else if (action.equals(AppInfo.SOCKET_LINE_STATUS_CHANGE)) {
                //TCP连接断开
                try {
                    int lineStatues = intent.getIntExtra(AppInfo.SOCKET_LINE_STATUS_CODE, SocketWebListener.SOCKET_ERROR);
                    if (lineStatues != SocketWebListener.SOCKET_OPEN) {
                        closeDownTask();
                        stopDownApkImg();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (action.equals(DISSMISS_DOWN_POOP_WINDOW)) {  //隐藏下载悬浮窗
                showDownProgressPop(false, "", "广播关闭");
            } else if (action.equals(DESTORY_DOWN_POOP_WINDOW)) {
                ondestoryPopWindow();
            } else if (action.equals(AppInfo.CHECK_BGG_IMAGE_TO_DOWN_SHOW)) { //用来检测背景图片得
                startToCheckBggImage();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        initOther();
        initReceiver();
    }

    private void initOther() {
        if (taskServiceParsener == null) {
            taskServiceParsener = new TaskServiceParsener(TaskWorkService.this);
        }
    }

    /**
     * 请求节目信息
     *
     * @param tag
     */
    public void requestTaskInfo(String tag) {
        if (TASK_CURRENT_TYPE != TASK_TYPE_DEFAULT) {
            MyLog.task("====请求拦截===" + tag + " / " + TASK_CURRENT_TYPE);
            return;
        }
        MyLog.task("====请求拦截==111=" + tag + " / " + TASK_CURRENT_TYPE);
        MyLog.task("=====开始请求=" + tag);
        if (taskParsener == null) {
            taskParsener = new TaskParsener(getBaseContext(), this);
        }
        //获取内存阀值，等会下载会去判断
        taskParsener.getSystemSettingInfoTask("请求节目信息，获取一次内存阈值");
        taskParsener.requestTaskUrl("正常流程请求并且播放");
    }

    TaskMudel taskMudel;

    /**
     * 定时检测任务是否g过期
     */
    public void checkTaskOnTimeEver() {
        if (Biantai.isThreeClick()) {
            return;
        }
        try {
            if (!AppInfo.isAppRun) {  //程序起来之后
                addErrorNum();
                MyLog.timer("=====程序没有启动到主界面，中断操作====", true);
                return;
            }
            if (!AppInfo.startCheckTaskTag) { //用来判断Main play界面是否在前台，其他界面不检测
                MyLog.timer("=====可以检测的界面不再前台，中断检测====", true);
                return;
            }
            boolean isLive = LeaderBarUtil.isAppRunBackground(TaskWorkService.this, "定时检测任务是否过期");
            if (isLive) {
                MyLog.timer("判断软件是否运行在后台true=后台，false=前台=====" + isLive, true);
                return;
            }
            if (TASK_CURRENT_TYPE != TASK_TYPE_DEFAULT) {
                MyLog.timer("=====当前不是空闲状态，中断操作====");
                return;
            }
            if (isDownApkImg) {
                //任务正在下载中，终端检测
                MyLog.timer("=====定时目前任务正在下载或者升级文件，中止检测====");
                return;
            }
            if (taskMudel == null) {
                taskMudel = new TaskModelmpl();
            }
            taskMudel.getPlayTaskListFormDb(new TaskGetDbListener() {
                @Override
                public void getTaskFromDb(List<TaskWorkEntity> list) {
                    parsenerTaskFormDb(list);
                }

                @Override
                public void getTaskTigerFromDb(TaskWorkEntity taskWorkEntity) {

                }
            }, "======定时检查任务===", TaskModelUtil.DEL_LASTDATE_AND_AFTER_NOW);
        } catch (Exception e) {
            MyLog.timer("定时检查任务异常: " + e.toString());
            e.printStackTrace();
        }
    }

    private void parsenerTaskFormDb(List<TaskWorkEntity> taskWorkEntityList) {
        if (taskWorkEntityList == null || taskWorkEntityList.size() < 1) {
            MyLog.timer("=====当前没有任务需要播放，这里停止所有播放任务===", true);
            //当前没有任务，这里应该停止播放
            sendBroadCastToView(AppInfo.RECEIVE_STOP_PLAY_TO_VIEW);
            return;
        }
        List<TaskWorkEntity> taskListSave = EtvApplication.getInstance().getTaskWorkEntityList();
        if (taskListSave == null || taskListSave.size() < 1) {
            //本地没有保存任务信息
            requestTaskInfo("本地没有保存播放任务,");
            return;
        }
        boolean isSame = TaskDealUtil.conpairListSame(taskWorkEntityList, taskListSave);
        if (isSame) { //当前正在播放合适的任务
            MyLog.timer("===========当前播放的任务合法=");
        } else {   //  播放的任务不合适事宜，这里需要重新检索
            MyLog.timer("===========当前播放的任务不合法=======去刷新任务====");
            requestTaskInfo("播放的任务不合适事宜，这里需要重新检索");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    List<DownFileEntity> downFileList = new ArrayList<DownFileEntity>();
    List<TaskDownEntity> downList = null;

    int downSpeed = 0;
    //用来封装下载播放任务的集合
    List<TaskWorkEntity> listTask = new ArrayList<>();

    /***
     * 请求任务失败，或者没有任务回调这里
     * @param toast
     */
    @Override
    public void finishMyShelf(String toast) {
        sendBroadCastToView(AppInfo.TASK_GET_INFO_NULL);  //没有获取到任务，这里去通知界面
    }

    /**
     * 返回需要下载播放的任务集合
     */
    @Override
    public void backTaskList(List<TaskWorkEntity> lists, String printTag) {
        setCurrentTaskType(TASK_TYPE_DEFAULT, "恢复原始状态");
        listTask = lists;
        if (listTask == null || listTask.size() < 1) {
            //这里需要 停止播放
            MyLog.task("========parserJsonOver=======没有获取到任务=====");
            sendBroadCastToView(AppInfo.RECEIVE_STOP_PLAY_TO_VIEW);
            return;
        }
        MyLog.task("====parserJsonOver===backTaskList==" + lists.size());
        //===============获取下载文件得个数==============================================
        if (taskMudel == null) {
            taskMudel = new TaskModelmpl();
        }
        taskMudel.getTaskDownListInfoFromDbByTaskIdRunnable(lists, new TaskGetDownListListener() {
            @Override
            public void getTaskDownFileListFromDb(List<TaskDownEntity> list) {
                if (list == null || list.size() < 1) {
                    MyLog.task("====检查下载的文件===00");
                } else {
                    MyLog.task("====检查下载的文件===" + list.size());
                }
                toJujleDownFileList(list, printTag);
            }
        }, printTag);
    }

    /**
     * 去判断有没有需要下载得资源
     *
     * @param taskEntityList
     */
    private void toJujleDownFileList(List<TaskDownEntity> taskEntityList, String printTag) {
        MyLog.task("===去判断有没有需要下载得资源====" + printTag);
        if (taskEntityList == null || taskEntityList.size() < 1) {
            startPlayTaskActivity("======不需要下载，直接去播放===");
            return;
        }
        MyLog.task("=========获取任务的个数=======" + listTask.size() + " /需要下载资源得个数== " + taskEntityList.size());
        //检查下载进度
        isHasDownLimit = false;
        handler.sendEmptyMessage(CHECK_LIMIT_DOWN_NUM);      //开始检测下载资格
    }

    boolean isHasDownLimit = false;  //检查是否有下载资格
    //检查下载资格
    private static final int CHECK_LIMIT_DOWN_NUM = 7869;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handler.removeMessages(msg.what);
            switch (msg.what) {
                case CHECK_LIMIT_DOWN_NUM:  //检查下载资格
                    if (isHasDownLimit) {
                        checkLimitSuccessToDown();
                        return;
                    }
                    if (AppConfig.isOnline) {
                        showDownProgressPop(true, "检测下载资格", "定时检查下载资格");
                    } else {
                        showDownProgressPop(true, "等待服务器连接", "定时检查下载资格");
                    }
                    //没有下载资格，5秒去检测一次
                    startCheckDownLimit();
                    //这里需要延时，因为设备同时请求，数据有误差
                    int randomNum = new Random().nextInt(5000);
                    int timeDistance = 5000 + randomNum;
                    handler.sendEmptyMessageDelayed(CHECK_LIMIT_DOWN_NUM, timeDistance);
                    break;
            }
        }
    };

    /**
     * 更新下载状态栏
     *
     * @param isShow
     * @param desc
     */
    private void showDownProgressPop(boolean isShow, String desc, String tag) {
        MyLog.down("=====showDownProgressPop===" + isShow + " / " + desc);
        AppStatuesListener.getInstance().DownStatuesEntity.postValue(new DownStatuesEntity(isShow, desc));
    }

    public void ondestoryPopWindow() {
        showDownProgressPop(false, "", "广播关闭");
    }


    /**
     * 检查有没有下载资格
     */
    private void startCheckDownLimit() {
        try {
            if (!NetWorkUtils.isNetworkConnected(TaskWorkService.this)) {
                MyLog.task("===检查下载资格==当前没有网络=");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setCurrentTaskType(TASK_TYPE_DOWN_WAIT, "恢复原始状态");
        taskParsener.checkDownLimit(new TaskCheckLimitListener() {

            @Override
            public void checkDownLimitSpeed(boolean isTrue, int currDownNum, String desc) {
                MyLog.task("===检查下载资格===" + isTrue + " / " + currDownNum + " / " + desc);
                if (!isTrue) {        //请求失败,直接打断不操作
                    isHasDownLimit = false;
                    return;
                }
                int limitDownNum = SharedPerManager.getLimitDevNum();
                if (currDownNum > limitDownNum || currDownNum == limitDownNum) {
                    isHasDownLimit = false;
                    return;
                }
                isHasDownLimit = true;
                checkLimitSuccessToDown();
            }
        });
    }

    /**
     * 检查下载资格，这里直接去下载
     */
    private void checkLimitSuccessToDown() {
        //检测到有下载资格
        handler.removeMessages(CHECK_LIMIT_DOWN_NUM);
        downTaskListOneByOne();
    }

    /**
     * 开始下载任务列表，一个一个得下载
     * 如果任务下载完成，列表就会移除当前任务，开始下一个任务下载
     * 如果全部完成，就直接开始播放
     */
    private void downTaskListOneByOne() {
        if (listTask == null || listTask.size() < 1) {
            MyLog.task("========parserJsonOver====全部任务下载完毕了=====");
            startPlayTaskActivity("========全部下载完毕===downTaskListOneByOne");    //准备去播放
            return;
        }
        TaskWorkEntity taskWorkEntity = listTask.get(0);
        //加入获取得任务==null.直接跳过当前，去下载下一个
        if (taskWorkEntity == null) {
            listTask.remove(0);
            downTaskListOneByOne();
            return;
        }
        MyLog.task("========parserJsonOver====开始下载的任务ID=====" + taskWorkEntity.getEtName());
        if (downFileList == null) {
            downFileList = new ArrayList<DownFileEntity>();
        }
        downFileList.clear();
        if (taskMudel == null) {
            taskMudel = new TaskModelmpl();
        }
        taskMudel.getTaskDownListInfoFromDbByTaskIdRunnable(listTask, new TaskGetDownListListener() {
            @Override
            public void getTaskDownFileListFromDb(List<TaskDownEntity> listContent) {
                MyLog.taskDown("====service====消息回来了==downTaskListOneByOne");
                if (listContent == null || listContent.size() < 1) {
                    MyLog.task("========parserJsonOver====当前任务已经下载完毕了，直接跳过当前任务，去下一个=====");
                    listTask.remove(0);
                    downTaskListOneByOne();
                    return;
                }
                startDownTaskFile(listContent);
            }
        }, "downTaskListOneByOne");
    }

    /**
     * 下载前的计算文件大小
     * 以及上传进度相关的工作
     *
     * @param downListCache
     */
    public void startDownTaskFile(List<TaskDownEntity> downListCache) {
        setCurrentTaskType(TASK_TYPE_DOWNING, "恢复原始状态");
        MyLog.timer("====下载文件的个数========" + downListCache.size());
        downFileList.clear();
        closeDownTask();
        this.downList = downListCache;
        long fileTotalLength = 0;   //用来比对磁盘
        for (int i = 0; i < downList.size(); i++) {
            MyLog.timer("====遍历数据，查询得结果====" + downList.get(i).toString());
            String taskId = downList.get(i).getTaskId();
            String downUrl = downList.get(i).getDownUrl();
            String savePath = downList.get(i).getSavePath();
            String downFileLength = downList.get(i).getFileLength().trim();
            //防止服务器下发得背景文件大小是null
            if (downFileLength == null || downFileLength.length() < 1) {
                downFileLength = "1024";
            }
            long fileLength = Long.parseLong(downFileLength);
            fileTotalLength = fileTotalLength + fileLength;
            MyLog.timer("======获取集合的信息==" + taskId + " / " + downUrl + " / " + savePath + "/ " + downList.get(i).getFileLength());
            DownFileEntity downFileEntity = new DownFileEntity(DownFileEntity.DOWN_STATE_START, 0, false, "准备下载", downUrl, savePath, 0, fileLength, taskId);
            downFileList.add(downFileEntity);
        }
        boolean isHasLastSize = jujleLastPanSize(fileTotalLength);
        if (isHasLastSize) { //SD卡有剩余空间
            setCurrentTaskType(TASK_TYPE_DOWNING, "单个下载开始");
            downResourceOneByOne();
        } else {
            MyLog.ExceptionPrint("开始下载任务，发现内存不足");
            showToastView("内存不足");
        }
    }

    /**
     * 下载任务里面的文件一个接一个
     */
    DownRunnable downTaskrunnable;

    private void downResourceOneByOne() {
        if (downFileList == null || downFileList.size() < 1) {
            if (listTask != null && listTask.size() > 0) {
                listTask.remove(0);
            }
            downTaskListOneByOne();
            return;
        }
        DownFileEntity entity = downFileList.get(0);
        MyLog.timer("====downResourceOneByOne====" + entity.toString());
        boolean isDownOver = entity.isDown();
        //如果已经下载完毕，这里就不用下载了
        if (isDownOver) {
            if (listTask != null && listTask.size() > 0) {
                listTask.remove(0);
            }
            downTaskListOneByOne();
            return;
        }
        String downUrlCache = entity.getDownPath();

        String downPath = "";
        if (downUrlCache.startsWith("http")) {
            downPath = downUrlCache;
        } else {
            downPath = ApiInfo.getFileDownUrl() + "/" + downUrlCache;
        }
        String savePath = entity.getSavePath();
        MyLog.down("===parserJsonOver==剩余个数==" + downFileList.size() + "\n下载地址：" + downPath + "\n保存地址：" + savePath, true);
        if (downTaskrunnable == null) {
            downTaskrunnable = new DownRunnable();
        }
        downTaskrunnable.setDownInfo(downPath, savePath, new DownStateListener() {
            @Override
            public void downStateInfo(DownFileEntity entity) {
                jujleDownFileLengthToWeb("下载进度提交", entity.getTaskId());
                int downStatues = entity.getDownState();
                int progress = entity.getProgress();
                String taskId = entity.getTaskId();
                downSpeed = entity.getDownSpeed();
                MyLog.task("========下载状态=progress===" + progress + " /speed= " + downSpeed + " / taskId =  " + taskId);
                if (downStatues == DownFileEntity.DOWN_STATE_SUCCESS) {
                    //下载成功,直接下载下一个
                    if (downFileList != null && downFileList.size() > 0) {
                        downFileList.remove(0);
                    }
                    downResourceOneByOne();
                } else if (downStatues == DownFileEntity.DOWN_STATE_FAIED) {
                    //下载失败，去下载下一个，并且删除下载失败的文件
                    if (downFileList != null && downFileList.size() > 0) {
                        downFileList.remove(0);
                    }
                    downResourceOneByOne();
                }
            }
        });
        downTaskrunnable.setTaskId(entity.getTaskId());
        downTaskrunnable.setIsDelFile(false);                                 // 不删除，下次断点下载文件
        downTaskrunnable.setLimitDownSpeed(SharedPerManager.getLimitSpeed()); // 限速下载
        EtvService.getInstance().executor(downTaskrunnable);
    }

    /**
     * 直接去播放界面
     */
    private void startPlayTaskActivity(String tag) {
        //提交上传进度到服务器
        List<TaskWorkEntity> taskWorkEntityList = DBTaskUtil.getTaskInfoList();
        if (taskWorkEntityList != null && taskWorkEntityList.size() > 0) {
            for (TaskWorkEntity taskWorkEntity : taskWorkEntityList) {
                String taskId = taskWorkEntity.getTaskId();
                EtvService.getInstance().updateProgressToWebRegister("准备进入播放界面", taskId, "", 100, 0, "-1");
            }
        }
        setCurrentTaskType(TASK_TYPE_DEFAULT, "恢复原始状态");
        MyLog.task("===startPlayTaskActivity===" + tag);
        //取消下载dialog状态
        showDownProgressPop(false, "", "===startPlayTaskActivity===进入界面===" + tag);
        closeDownTask();
        //再次去判断有没有合适的任务
        if (taskMudel == null) {
            taskMudel = new TaskModelmpl();
        }
        taskMudel.getPlayTaskListFormDb(new TaskGetDbListener() {
            @Override
            public void getTaskFromDb(List<TaskWorkEntity> list) {
                if (list == null || list.size() < 1) {
                    MyLog.task("====问题追踪===播放前的检查====0");
                    sendBroadCastToView(AppInfo.TASK_GET_INFO_NULL);
                    return;
                }
                MyLog.timer("==========播放前的检查====" + list.size());
                startToPlayActivityView(tag);
            }

            @Override
            public void getTaskTigerFromDb(TaskWorkEntity taskWorkEntity) {

            }
        }, "======去播放界面前检查一次===", TaskModelUtil.DEL_LASTDATE_AND_AFTER_NOW);
    }

    private void startToPlayActivityView(String tag) {
        MyLog.timer("==========startToPlayActivity==" + SimpleDateUtil.getCurrentTimelONG() + " / " + tag, true);
        try {
            boolean isRunFirat = ActivityCollector.isForeground(getBaseContext(), PlayerTaskActivity.class.getName());
            MyLog.timer("播放界面是否在前台===" + isRunFirat, true);
            if (isRunFirat) {
                MyLog.timer("播放界面是否在前台==true=直接发送广播", true);
                sendBroadCastToView(AppInfo.DOWN_TASK_SUCCESS);
            } else {
                //防止判断出错，这里直接先发，在启动界面
                sendBroadCastToView(AppInfo.DOWN_TASK_SUCCESS);
                MyLog.timer("播放界面是否在前台==false=直接打开界面", true);
                Intent intent = new Intent();
                intent.setClass(EtvApplication.getContext(), PlayerTaskActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                EtvApplication.getContext().startActivity(intent);
            }
        } catch (Exception e) {
            MyLog.timer("任务下载完成，启动界面异常===" + e.toString(), true);
            e.printStackTrace();
        }
    }

    /**
     * 计算下载进度的方法
     */
    private void jujleDownFileLengthToWeb(String tag, String taskId) {
        try {
            if (!AppConfig.isOnline) {
                //设备不在线，中止更新进度
                showDownProgressPop(false, "", "====000");
                MyLog.down("====设备不在线，中止操作====");
                return;
            }
            long hasdownFileLength = 1;  //已经下载的容量
            long taskFileAllLength = 1;  //下载文件总内存
            if (downList == null || downList.size() < 1) {
                showDownProgressPop(false, "", "====111");
                return;
            }
            for (int i = 0; i < downList.size(); i++) {
                TaskDownEntity taskDownEntity = downList.get(i);
                if (taskDownEntity.getTaskId().contains(taskId)) {
                    String downFilePath = taskDownEntity.getSavePath();
                    String downFileLength = downList.get(i).getFileLength().trim();
                    //防止服务器下发得背景文件大小是null
                    if (downFileLength == null || downFileLength.length() < 1) {
                        downFileLength = "1024";
                    }
                    long fileLength = Long.parseLong(downFileLength);
                    taskFileAllLength = taskFileAllLength + fileLength;
                    File file = new File(downFilePath);
                    if (file.exists()) {
                        long saveFileLength = file.length();
                        hasdownFileLength = hasdownFileLength + saveFileLength;
                    }
                }
            }
            int progress = (int) (hasdownFileLength * 100 / taskFileAllLength);
            if (progress < 1) {
                progress = 1;
            }
            long titalNum = taskFileAllLength / 1024 / 1024;
            if (titalNum < 1) {
                titalNum = 1;
            }
            MyLog.test("===定时检测进度==" + taskId + " / " + titalNum + "M /progress=" + progress + " /downSpeed=" + downSpeed);
            if (taskId == null || taskId.length() < 2) {
                return;
            }
            long randomNum = new Random().nextInt(15);
            long showDownSpeed = downSpeed - randomNum;
            if (showDownSpeed < 0) {
                showDownSpeed = 0;
            }
            showDownProgressPop(true, progress + "%\n" + showDownSpeed + "kb/s", "====2222==上传更新进度");
            EtvService.getInstance().updateProgressToWebRegister(tag, taskId, titalNum + "M", progress, downSpeed, "1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //================================================================升级APK img========================================================

    /***
     * 升级APK img
     */
    DownRunnable runnableUpdateApk;

    private void updateApkImageInfo() {
        initUpdateApkImg();
    }

    UpdateParsener updateParsener;
    MySDCard mySdcard;

    private void initUpdateApkImg() {
        if (mySdcard == null) {
            mySdcard = new MySDCard(TaskWorkService.this);
        }
        updateParsener = new UpdateParsener(TaskWorkService.this, new UpdateView() {
            @Override
            public void updateMainView(List<UpdateInfo> listCache) {
                MyLog.update("没有检测到升级任务===================");
                if (listCache.size() < 1) {
                    MyLog.update("没有检测到升级任务===准备提交数据", true);
                    updateOver("操作完毕，关闭升级");
                    return;
                }
                String basePath = AppInfo.BASE_PATH_INNER;
                long lastPanSize = mySdcard.getAvailableExternalMemorySize(basePath, 1024 * 1024);
                long fileSize = listCache.get(0).getUfSize();
                fileSize = fileSize / (1024 * 1024);
                MyLog.update("=====下载的地址===" + fileSize + " /磁盘空间== " + lastPanSize);
                if (lastPanSize - fileSize < 100) { //内存小于100M
                    MyLog.cdl("内存不足,停止下载", true);
                    return;
                }
                downFileInfo(listCache.get(0));
            }

            @Override
            public void updateOver(String desc) {
                updateParsener.updateOverProgressToWeb();
                MyLog.update("=======检查全部完毕了==" + desc);
            }
        });
        updateParsener.getUpdateInfo();
    }

    /***
     * 下载更新文件
     * @param updateInfo
     */
    public void downFileInfo(final UpdateInfo updateInfo) {
        closeDownTask();  //下载APK。就停掉任务下载
        if (runnableUpdateApk != null) {
            runnableUpdateApk.stopDown();
        }
        isDownApkImg = true;
        String downUrl = ApiInfo.getFileDownUrl() + "/" + updateInfo.getUfSaveUrl();
        String downFileName = updateInfo.getUfOgname();
        final String endName = downFileName.substring(downFileName.lastIndexOf("."), downFileName.length());
        if (downFileName.endsWith(".zip") || downFileName.endsWith(".img")) {
            downFileName = "update" + endName;
        }
        FileUtil.deleteDirOrFilePath(AppInfo.BASE_APK(), "parper down apk file ");
        String savePath = AppInfo.BASE_APK() + "/" + downFileName;
        MyLog.update("====下载文件的保存地址==" + savePath + " /下载地址: " + downUrl);
        runnableUpdateApk = new DownRunnable(downUrl, savePath, new DownStateListener() {

            @Override
            public void downStateInfo(final DownFileEntity entity) {
                //更新进度给服务器
                MyLog.update(entity.toString());
                EtvService.getInstance().updateDownApkImgProgress("任务下载中", entity.getProgress(), entity.getDownSpeed(), updateInfo.getUfOgname());
                if (entity.getDownState() == DownFileEntity.DOWN_STATE_SUCCESS) {  //下载成功，更新后台数据，
                    isDownApkImg = false;
                    EtvService.getInstance().updateDownApkImgProgress("文件下载完成", 100, 0, updateInfo.getUfOgname());
                    String fileSaveLength = entity.getSavePath();
                    File fileSave = new File(fileSaveLength);
                    long fileLengthSave = fileSave.length();
                    long fileDownSize = updateInfo.getUfSize();
                    MyLog.update("====下载的文件的大小====" + fileLengthSave + " /下载的文件大小== " + fileDownSize);
                    if (fileLengthSave != fileDownSize) {
                        MyLog.update("下载文件不完整，此次取消升级", true);
                        return;
                    }
                    //下载成功，去安装
                    MyLog.update("准备安装");
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            updateParsener.installFile(entity.getSavePath());
                        }
                    }, 1000);
                } else if (entity.getDownState() == DownFileEntity.DOWN_STATE_FAIED) {  //下载失败也要更新后台数据库
                    isDownApkImg = false;
                    EtvService.getInstance().updateDownApkImgProgress("下载失败", 0, 0, updateInfo.getUfOgname());
                }
            }
        });
        //不要删除下载文件,用于断点续传
        runnableUpdateApk.setIsDelFile(false);
        runnableUpdateApk.setLimitDownSpeed(1500);
        EtvService.getInstance().executor(runnableUpdateApk);
    }

    /***
     * 升级文件停止下载
     */
    public void stopDownApkImg() {
        try {
            isDownApkImg = false;
            if (runnableUpdateApk != null) {
                runnableUpdateApk.stopDown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭下载
     */
    public void closeDownTask() {
        showDownProgressPop(false, "", "closeDownTask==关闭下载==");
        try {
            downSpeed = 0;
            setCurrentTaskType(TASK_TYPE_DEFAULT, "恢复原始状态");
            if (downTaskrunnable != null) {
                downTaskrunnable.stopDown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 请求节目信息，不用下载
     * @param tag
     */
    public void requestTaskInfoNoDown(String tag) {
//        int workModel = SharedPerManager.getWorkModel();
//        if (workModel != AppInfo.WORK_MODEL_NET) {
//            MyLog.task("非网络下发模式，不同步任务", true);
//            return;
//        }
//        if (isRequestWeb) {
//            MyLog.timer("====requestTaskInfo=jin=仅仅同步信息，不需要下载=被拦截了");
//            return;
//        }
//        MyLog.timer("====requestTaskInfo=jin=仅仅同步信息，不需要下载=" + tag);
//        TaskParsener taskParsenerNoDown = new TaskParsener(getBaseContext(), null);
//        taskParsenerNoDown.requestTaskUrl("仅仅请求，不下载不播放");
    }

    private void startToCheckBggImage() {
        initOther();
        taskServiceParsener.startToCheckBggImage();
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(GET_TASK_FROM_WEB_TAG);                //从服务中获取任务数据
        filter.addAction(UPDATE_APK_IMG_INFO);              //升级APK img信息
        filter.addAction(Intent.ACTION_TIME_TICK);          //时间变化
        filter.addAction(AppInfo.STOP_DOWN_TASK_RECEIVER);  //停止下载任务
        filter.addAction(GET_TASK_FROM_WEB_NO_DOWN);        //同步服务器任务信息
        filter.addAction(AppInfo.NET_DISONLINE);            //网络断开
        filter.addAction(AppInfo.NET_ONLINE);               //网络连接
        filter.addAction(AppInfo.SOCKET_LINE_STATUS_CHANGE);  //服务器TCP连接断开
        filter.addAction(DISSMISS_DOWN_POOP_WINDOW);   //取消显示poopdialog
        filter.addAction(DESTORY_DOWN_POOP_WINDOW);   //干掉poopdialog
        filter.addAction(AppInfo.CHECK_BGG_IMAGE_TO_DOWN_SHOW);
        registerReceiver(receiver, filter);
    }

    private void showToastView(final String s) {
        try {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    MyToastView.getInstance().Toast(TaskWorkService.this, s);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    TaskServiceParsener taskServiceParsener;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    /***
     * 判断内存不足的问题
     */
    private boolean jujleLastPanSize(long downTotalFileSize) {
        String basePath = AppInfo.BASE_SD_PATH();
        MySDCard mySdcard = new MySDCard(getBaseContext());
        long lastPanSize = mySdcard.getAvailableExternalMemorySize(basePath, 1024 * 1024);
        downTotalFileSize = downTotalFileSize / (1024 * 1024);
        MyLog.timer("=====SD卡内存剩余===" + lastPanSize + " /下载文件总内存==" + downTotalFileSize);
        if ((lastPanSize - downTotalFileSize) < 100) {  //给内存留100M
            showToastView("内存不足,停止下载");
            return false;
        }
        return true;
    }

    /**
     * 发送广播给
     *
     * @param action
     */
    public void sendBroadCastToView(String action) {
        try {
            Intent intent = new Intent();
            intent.setAction(action);
            sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 统计错误得次数
     * 为什么要统计：
     * 是因为，设备会莫名退出软件，暂时找不到原因，这里做一个自启
     * 如果5 分钟软件没有起来得话，就这里手动掉起来
     */
    int ERROR_ADD_NUM = 0;

    private void addErrorNum() {
        boolean isScreenOpen = SharedPerManager.getSleepStatues();
        if (isScreenOpen) {
            MyLog.timer("当前屏幕休眠=中止添加");
            return;
        }
        //如果是手动退出得话就不处理了
        boolean ieErrorExit = SharedPerManager.getExitDefault();
        if (ieErrorExit) {
            MyLog.timer("用户手动退出了，这里中断播放保护");
            ERROR_ADD_NUM = 0;
            return;
        }
        try {
            ERROR_ADD_NUM++;
            if (ERROR_ADD_NUM > 3) {
                MyLog.timer("设备3次检测状态失败，准备重启软件-MainActivity", true);
                ActivityCollector.finishAll();
                Intent intent = new Intent(TaskWorkService.this, StartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                ERROR_ADD_NUM = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
