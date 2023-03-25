package com.etv.task.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.EtvApplication;
import com.etv.activity.MainActivity;
import com.etv.config.AppInfo;
import com.etv.service.EtvService;
import com.etv.task.entity.SceneEntity;
import com.etv.task.parsener.PlayTaskParsener;
import com.etv.task.view.PlayTaskView;
import com.etv.test.SameTaskVideo;
import com.etv.test.StartUploadEntity;
import com.etv.util.MyLog;
import com.etv.util.SharedPerManager;
import com.etv.util.SharedPerUtil;
import com.etv.util.rxjava.AppStatuesListener;
import com.ys.etv.R;
import com.ys.etv.databinding.ActivityPlayTaskBinding;
import com.ys.etv.databinding.ActivityPlayTaskTestBinding;
import com.ys.model.dialog.MyToastView;

public class PlayerTaskActivity extends TaskActivity implements PlayTaskView {

    AbsoluteLayout view_abous;
    PlayTaskParsener playTaskParsener;

    private BroadcastReceiver receiverPlayTask = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            MyLog.playTask("=======播放界面广播===" + action);
            if (action.equals(AppInfo.DOWN_TASK_SUCCESS)) {
                //任务下载完成，准备刷新界面
                updateViewInfo("======任务下载完毕，去布局绘制界面");
            } else if (action.equals(Intent.ACTION_TIME_TICK)) {
                if (!isPlayForst) {
                    MyLog.playTask("播放界面不再前台,打断操作");
                    return;
                }
                if (playTaskParsener != null) {
                    playTaskParsener.updateMediaVoiceNum();
                }
            } else if (action.equals(AppInfo.MESSAGE_RECEIVE_SCREEN_CLOSE)) {
                //停止任务自动检查
                if (playTaskParsener != null) {
                    playTaskParsener.shutDownDiffScreenLight();
                }
                MainActivity.IS_ORDER_REQUEST_TASK = false;
                startToMainTaskView();
                AppInfo.startCheckTaskTag = false;
            }
        }
    };


    ActivityPlayTaskBinding mBinding;

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        boolean isDevSpeed = SharedPerManager.getDevSpeedStatues();
        if (isDevSpeed) {
            getWindow().setFormat(PixelFormat.TRANSLUCENT);
            //硬件加速
            getWindow().setFlags(
                    android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        }
        mBinding = ActivityPlayTaskBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initView();
        initListener();
        initTaskReceiver();
    }

    private void initListener() {
        GLSurfaceView view;
        initSamaTaskStatuesListener();
        AppStatuesListener.getInstance().NetChange.observe(PlayerTaskActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                MyLog.message("=======onChanged==NetChange======" + s);
                //网络已连接，通知需要刷新得节目，刷新一次

            }
        });

        //这里需要更新声音
        AppStatuesListener.getInstance().UpdateMainMediaVoiceEvent.observe(PlayerTaskActivity.this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                MyLog.cdl("==========界面更新音量值大小========" + s);
                //背景图下载完成，这里修改音量大小
                if (playTaskParsener != null) {
                    playTaskParsener.updateMediaVoiceNum();
                }
            }
        });

        mBinding.viewClick.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showBaseSettingDialogNew();
                return true;
            }
        });

    }

    /**
     * 同步任务监听回调
     */
    private void initSamaTaskStatuesListener() {
        AppStatuesListener.getInstance().playProgress.observe(PlayerTaskActivity.this, sameTaskVideo -> {
            playTaskParsener.updateSameTaskVideoProgress(sameTaskVideo);
        });

        //开始同步主屏得任务
        AppStatuesListener.getInstance().startToUploadSameTask.observe(PlayerTaskActivity.this, cmdData -> {
            MyLog.udp("=======主界面接收到开始播放得指令 000 " + cmdData.secenId);
            boolean isMainLeader = SharedPerUtil.getSameTaskMainLeader();
            if (isMainLeader) {
                MyLog.udp("=======主屏拦截操作");
                return;
            }
            playTaskParsener.startToUploadMainScreenProgram(cmdData);
        });
    }

    @Override
    public void checkSdStateFinish() {
        startToMainTaskView();
    }

    @Override
    public void stopOrderToPlay() {
        startToMainTaskView();
    }

    //这里表示不去任务==null
    @Override
    public void getTaskInfoNull() {
        MyLog.cdl("=======获取的节目==null==playActivity==");
        startToMainTaskView();
    }

    RelativeLayout rela_down_tag;
    TextView tv_down_desc;

    private void initView() {
//        EtvApplication.getInstance().setSendIpList(null);  //清理一次数据
        tv_down_desc = (TextView) findViewById(R.id.tv_down_desc);
        rela_down_tag = (RelativeLayout) findViewById(R.id.rela_down_tag);
        view_abous = (AbsoluteLayout) findViewById(R.id.view_abous);
        playTaskParsener = new PlayTaskParsener(PlayerTaskActivity.this, this);
        playTaskParsener.updateMediaVoiceNum();
        updateViewInfo("onResume");
    }

    @Override
    public void showDownStatuesView(boolean isShow, String desc) {
        rela_down_tag.setVisibility(isShow ? View.VISIBLE : View.GONE);
        tv_down_desc.setText(desc);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppInfo.startCheckTaskTag = true;
        if (playTaskParsener == null) {
            return;
        }
        playTaskParsener.setTaskSameLeader(SharedPerUtil.getSameTaskMainLeader());
        SceneEntity currentSencent = playTaskParsener.getCurrentSencenEntity();
        Log.e("cdl", "=======currentSencent==" + (currentSencent == null));
        if (currentSencent != null) {
            playTaskParsener.resumePlayView();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyLog.playTask("========生命周期=======onPause=======");
        if (playTaskParsener != null) {
            playTaskParsener.pauseDisplayView();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isPlayForst = false;
        if (playTaskParsener != null) {
            playTaskParsener.clearMemory();
            playTaskParsener.clearLastView(PlayTaskParsener.TAG_CLEARVIEW_ONDESTORY, "OnsTOP");
        }
    }

    /**
     * 跟新界面数据
     *
     * @param tag
     */
    private void updateViewInfo(String tag) {
        if (playTaskParsener == null) {
            return;
        }
        MyLog.playTask("===========刷新界面===" + tag + "  /时间==" + System.currentTimeMillis());
        EtvService.getInstance().updateDevStatuesToWeb(PlayerTaskActivity.this);
        playTaskParsener.getTaskToView(tag);           //获取数据
    }

    private int getApkBackTime(String time) {
        time = time.trim();
        int appBackTime = 0;
        try {
            if (time == null || time.length() < 1) {
                return 0;
            }
            appBackTime = Integer.parseInt(time);
            if (appBackTime < 1) {  //没有设置返回时间，这里就不需要计时返回
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appBackTime;
    }

    /**
     * 所有的任务已经播放完毕，
     * 去检查新的播放任务
     */
    @Override
    public void findTaskNew() {
        MyLog.cdl("=========TaskCacheActivity====findTaskNew=任务播放完毕了===，去默认播放界面");
        MainActivity.IS_ORDER_REQUEST_TASK = true;
        Intent intent = new Intent(PlayerTaskActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /***
     * 界面异常:直接停止界面
     * @param errorInfo
     */
    @Override
    public void showViewError(String errorInfo) {
        MyLog.playTask("===========showViewError==" + errorInfo);
        startToMainTaskView();
    }

    @Override
    public void showToastView(String toast) {
        MyToastView.getInstance().Toast(PlayerTaskActivity.this, toast);
    }

    @Override
    public AbsoluteLayout getAbsoluteLayout() {
        return view_abous;
    }

    private void initTaskReceiver() {
        IntentFilter fileter = new IntentFilter();
        fileter.addAction(AppInfo.DOWN_TASK_SUCCESS);  //任务下载完毕，这里需要刷新界面
        fileter.addAction(Intent.ACTION_TIME_TICK);  //时间变化得广播监听
        fileter.addAction(AppInfo.MESSAGE_RECEIVE_SCREEN_CLOSE);  //息屏休眠得功能
        registerReceiver(receiverPlayTask, fileter);
    }

    private SceneEntity getCurrentPlayScenEntity() {
        SceneEntity sceneEntity = playTaskParsener.getCurrentSencenEntity();
        if (sceneEntity == null) {
            return null;
        }
        return sceneEntity;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (playTaskParsener != null) {
                playTaskParsener.clearMemory();
                playTaskParsener.clearLastView(PlayTaskParsener.TAG_CLEARVIEW_ONDESTORY, "ONdESTROY");
            }
            MyLog.playTask("========生命周期==========onDestroy====");
            EtvApplication.getInstance().setTaskWorkEntityList(null);
            if (receiverPlayTask != null) {
                unregisterReceiver(receiverPlayTask);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //判断播放界面是否在前台
    public static boolean isPlayForst = false;

    @Override
    protected void onPostResume() {
        super.onPostResume();
        isPlayForst = true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        MyLog.cdl("======onKeyDown========" + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showBaseSettingDialogNew();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
