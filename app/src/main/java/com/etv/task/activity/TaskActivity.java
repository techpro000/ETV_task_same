package com.etv.task.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.lifecycle.Observer;

import com.etv.activity.BaseActivity;
import com.etv.activity.MainActivity;
import com.etv.config.AppInfo;
import com.etv.task.entity.DownStatuesEntity;
import com.etv.util.MyLog;
import com.etv.util.image.glide.GlideCacheUtil;
import com.etv.util.rxjava.AppStatuesListener;
import com.etv.util.system.DoubleScreenManager;
import com.etv.util.system.SystemManagerUtil;
import com.ys.model.dialog.MyToastView;

public abstract class TaskActivity extends BaseActivity {

    MyReceiver receiverBase;
    SdReceiver sdReceiver;

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            MyLog.cdl("====TaskActivity=播放界面监听到的广播===" + action);
            if (action.equals(AppInfo.RECEIVE_STOP_PLAY_TO_VIEW)) {   //接受到停止的指令
                stopOrderToPlay();
            } else if (action.contains(AppInfo.TASK_GET_INFO_NULL)) { //没有获取到任务
                getTaskInfoNull();
            } else if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(AppInfo.SYSTEM_DIALOG_REASON_KEY);
                if (reason == null) {
                    return;
                }
                if (reason.equals(AppInfo.SYSTEM_DIALOG_REASON_HOME_KEY)) {
                    Log.e("haha", "========Home键被监听=====");
                    finiMySelfAPP("Home键被监听");
                } else if (reason.equals(AppInfo.SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                    Log.e("haha", "========多任务键被监听=====");
                    finiMySelfAPP("多任务键被监听");
                }
            }
        }
    }


    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        AppInfo.isAppRun = true;
        //每次界面启动的时候调用一次，刷新下SDcard
        SystemManagerUtil.syncSdcard();
        dismissPopWindow();
        initReceiver();
        getScreenInfo();
        initRxBusManager();
    }

    private void initRxBusManager() {
        AppStatuesListener.getInstance().DownStatuesEntity.observe(TaskActivity.this, new Observer<DownStatuesEntity>() {
            @Override
            public void onChanged(DownStatuesEntity downStatuesEntity) {
                boolean isShow = downStatuesEntity.isShow();
                String desc = downStatuesEntity.getShowDesc();
                showDownStatuesView(isShow, desc);
            }
        });
    }

    /***
     * 更新下载状态
     * @param isShow
     * @param desc
     */
    public abstract void showDownStatuesView(boolean isShow, String desc);

    /**
     * 获取屏幕相关的信息
     */
    private void getScreenInfo() {
        DoubleScreenManager.getInstance(TaskActivity.this).getDevScreenNumFroSys();
    }


    private void finiMySelfAPP(String TAG) {
        try {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {
            System.exit(1);
            e.printStackTrace();
        }
    }

    private class SdReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            MyLog.cdl("=====播放界面监听到的广播===" + action);
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) { //插入SD卡
                checkSdStateFinish();
            } else if (action.equals(Intent.ACTION_MEDIA_EJECT)) {

            }
        }
    }

    long downTime;
    long upTime;
    public View.OnTouchListener touchTaskListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int action = motionEvent.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    downTime = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_UP:
                    upTime = System.currentTimeMillis();
                    long distance = upTime - downTime;
                    MyLog.cdl("==========时间差===" + upTime + " /" + downTime + " / " + distance);
                    if (upTime - downTime > 5000) {
                        showBaseSettingDialogNew();
                    }
                    downTime = 0;
                    upTime = 0;
                    break;
            }
            return true;
        }
    };

    public void showToastView(String toast) {
        MyToastView.getInstance().Toast(TaskActivity.this, toast);
    }

    public void startToMainTaskView() {
        startActivity(new Intent(TaskActivity.this, MainActivity.class));
        finish();
    }

    //检测到SD，HDML卡插拔，需要关闭界面=====================================================

    //点击了 home或者 多任务按钮
//    public abstract void clickHomeListener();

    public abstract void checkSdStateFinish();

    //接受到停止指令
    public abstract void stopOrderToPlay();

    //获取的任务==null
    public abstract void getTaskInfoNull();

    private void initReceiver() {
        receiverBase = new MyReceiver();
        sdReceiver = new SdReceiver();
        IntentFilter fileter = new IntentFilter();
        fileter.addAction(AppInfo.RECEIVE_STOP_PLAY_TO_VIEW);  //停止的指令
        fileter.addAction(AppInfo.TASK_GET_INFO_NULL);    //获取的任务==null，根据情况去操作
        fileter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);  //监听home按钮
        registerReceiver(receiverBase, fileter);

        IntentFilter fileterSd = new IntentFilter();
        fileterSd.addAction(Intent.ACTION_MEDIA_MOUNTED);  //in
        fileterSd.addAction(Intent.ACTION_MEDIA_EJECT);    //out
        fileterSd.addDataScheme("file");
        registerReceiver(sdReceiver, fileterSd);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiverBase != null) {
            unregisterReceiver(receiverBase);
        }
        if (sdReceiver != null) {
            unregisterReceiver(sdReceiver);
        }
        try {
            MyLog.cdl("=====ondestroy释放内存====");
            GlideCacheUtil.getInstance().clearImageAllCache(TaskActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
