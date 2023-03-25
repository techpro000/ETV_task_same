package com.etv.activity.pansener;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.etv.activity.TimerReduceActivity;
import com.etv.activity.view.InitView;
import com.etv.config.AppConfig;
import com.etv.config.AppInfo;
import com.etv.http.util.CheckTimeRunnable;
import com.etv.service.EtvService;
import com.etv.service.TcpService;
import com.etv.service.TcpSocketService;
import com.etv.service.listener.EtvServerListener;
import com.etv.service.util.EtvServerModule;
import com.etv.service.util.EtvServerModuleImpl;
import com.etv.service.util.TaskServiceView;
import com.etv.util.MyLog;
import com.etv.util.NetWorkUtils;
import com.etv.util.SharedPerManager;
import com.etv.util.SharedPerUtil;
import com.etv.util.system.PowerOnOffUtil;

public class InitPansener {

    Context context;
    InitView initView;

    public InitPansener(Context context, InitView initView) {
        this.context = context;
        this.initView = initView;
    }

    /***
     * 检查定时开关机
     * 1：有网的情况下联网获取数据
     * 2：没有网络的情况下，从数据库中获取数据
     */
    PowerOnOffUtil powerOnOffUtil;

    public void checkMainPowerOnOff() {
        //无论如何，10秒后跳转界面
        handler.sendEmptyMessageDelayed(TIME_TO_JUMP_TO_ACTIVITY, 10 * 1000);
        try {
            if (powerOnOffUtil == null) {
                powerOnOffUtil = new PowerOnOffUtil(context);
            }
            // 网络模式
            if (NetWorkUtils.isNetworkConnected(context)) {
                //从网络获取定时开关机
                MyLog.powerOnOff("0000==============网络模式，有网去同步定时开关机===", true);
                if (SharedPerUtil.SOCKEY_TYPE() == AppConfig.SOCKEY_TYPE_WEBSOCKET) {
                    TcpService.getInstance().dealPowernOff();
                } else {
                    TcpSocketService.getInstance().dealPowernOff();
                }
            } else { //没有网络,从数据库中获取
                MyLog.powerOnOff("0000==============网络模式，没有网络去同步本地时间===", true);
                powerOnOffUtil.getPowerOnOffFromDb();
            }
            handler.sendEmptyMessageDelayed(MESSAGE_CHECK_TIMER_SHOT_DOWN, 2500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final int MESSAGE_CHECK_TIMER_SHOT_DOWN = 5642;
    private static final int TIME_TO_JUMP_TO_ACTIVITY = 5643;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handler.removeMessages(msg.what);
            switch (msg.what) {
                case MESSAGE_CHECK_TIMER_SHOT_DOWN:
                    checkTimerShotDown("延时2500秒后执行");
                    break;
                case TIME_TO_JUMP_TO_ACTIVITY:
                    MyLog.cdl("===========时间到了，跳转界面====");
                    jumpMainActivity();
                    break;
            }
        }
    };

    private void jumpMainActivity() {
        if (initView == null) {
            return;
        }
        if (handler != null) {
            handler.removeMessages(TIME_TO_JUMP_TO_ACTIVITY);
        }
        initView.jumpActivity();
    }

    /**
     * 检查当前是否是关机时间
     */
    public void checkTimerShotDown(String tag) {
        MyLog.powerOnOff("0000==============检查是否是关机时间===" + tag);
        CheckTimeRunnable runnable = new CheckTimeRunnable(new EtvServerListener() {

            @Override
            public void jujleCurrentIsShutDownTime(boolean isShutDown) {
                if (isShutDown) {
                    MyLog.powerOnOff("0000======当前是开机时间");
                    if (initView == null) {
                        return;
                    }
                    jumpMainActivity();  //当前是开机时间，直接跳转
                } else {
                    showAlertDialog();
                    MyLog.powerOnOff("0000======当前是关机时间");
                }
            }
        });
        EtvService.getInstance().executor(runnable);
    }


    private void showAlertDialog() {
        handler.removeMessages(TIME_TO_JUMP_TO_ACTIVITY);
        Intent intent = new Intent();
        intent.setClass(context, TimerReduceActivity.class);
        context.startActivity(intent);
    }

    public void onDestroy() {
        if (handler != null) {
            handler.removeMessages(MESSAGE_CHECK_TIMER_SHOT_DOWN);
            handler.removeMessages(TIME_TO_JUMP_TO_ACTIVITY);
        }
    }

    public void queryNickName() {
        if (!NetWorkUtils.isNetworkConnected(context)) {
            return;
        }
        EtvServerModule etvServerModule = new EtvServerModuleImpl();
        etvServerModule.queryDeviceInfoFromWeb(context, new TaskServiceView() {

            @Override
            public void getDevInfoFromWeb(boolean isSuccess, String errorDesc) {
                if (!isSuccess) {
                    return;
                }
            }
        });
    }
}
