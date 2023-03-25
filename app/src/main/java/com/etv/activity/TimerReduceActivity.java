package com.etv.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.etv.config.AppConfig;
import com.etv.config.AppInfo;
import com.etv.http.util.CheckTimeRunnable;
import com.etv.listener.TimeChangeListener;
import com.etv.service.EtvService;
import com.etv.service.listener.EtvServerListener;
import com.etv.setting.PowerOnOffWebActivity;
import com.etv.util.MyLog;
import com.etv.util.SharedPerManager;
import com.etv.util.SimpleDateUtil;
import com.etv.util.TimerDealUtil;
import com.etv.util.rxjava.AppStatuesListener;
import com.etv.util.rxjava.RxBus;
import com.etv.util.rxjava.event.RxEvent;
import com.etv.util.system.PowerOnOffUtil;
import com.etv.util.system.SystemManagerInstance;
import com.etv.util.system.SystemManagerUtil;
import com.ys.etv.R;

import io.reactivex.functions.Consumer;

public class TimerReduceActivity extends BaseActivity implements View.OnClickListener {

    TextView tv_reduce;
    Button btn_shut_down;
    Button btn_cacel_shut;
    private static final int DEFAULT_TIME = 40;
    int redusNum = DEFAULT_TIME;


    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_time_reduce);
        initDialog();
        initRxBus();
    }

    private void initRxBus() {
        AppStatuesListener.getInstance().PowerOnOffEvent.observe(TimerReduceActivity.this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //用户在开机之后重新设定了开关机时间，这里重新检查获取一下
                MyLog.powerOnOff("==初始化界面接受到刷新界面的广播了");
                checkTimerReduceDialog("倒计时界面界面接受到刷新界面的广播");
            }
        });
    }

    TextView tv_current_time;

    private void initDialog() {
        tv_current_time = (TextView) findViewById(R.id.tv_current_time);
        tv_reduce = (TextView) findViewById(R.id.tv_reduce);
        btn_shut_down = (Button) findViewById(R.id.btn_shut_down);
        btn_cacel_shut = (Button) findViewById(R.id.btn_cacel_shut);
        btn_shut_down.setOnClickListener(this);
        btn_cacel_shut.setOnClickListener(this);

        long currentTime = SimpleDateUtil.getCurrentTimelONG();
        if (currentTime < AppConfig.TIME_CHECK_POWER_REDUCE) {
            showToastView(getString(R.string.system_time_error));
            MyLog.cdl("当前时间不合法，中断倒计时: " + currentTime, true);
            jumpActivity();
            return;
        }
        startTimer();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_shut_down:
                clickSureButton();
                break;
            case R.id.btn_cacel_shut:
                jumpActivity();
                break;
        }
    }

    /**
     * 关机
     */
    private void clickSureButton() {
        try {
            SystemManagerInstance.getInstance(TimerReduceActivity.this).shoutDownDev();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }

    private void jumpActivity() {
        try {
            MyLog.powerOnOff("00000==========取消关机");
            PowerOnOffUtil powerOnOffUtil = new PowerOnOffUtil(TimerReduceActivity.this);
            powerOnOffUtil.changePowerOnOffByWorkModel("倒计时界面");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent();
        intent.setClass(TimerReduceActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    public void startTimer() {
        redusNum = DEFAULT_TIME;
        long currentTime = System.currentTimeMillis();
        TimerDealUtil.getInstance().setTimeChangeListener(currentTime, new TimeChangeListener() {
            @Override
            public void timeChangeMin(long classId) {
                redusNum--;
                if (redusNum < 0) {
                    clickSureButton();
                    return;
                }
                String currentTime = SimpleDateUtil.formatTaskTimeShow(System.currentTimeMillis());
                tv_current_time.setText(currentTime);
                if (redusNum < 10) {
                    tv_reduce.setText("0" + redusNum);
                } else {
                    tv_reduce.setText(redusNum + "");
                }
            }
        });
        redusNum = DEFAULT_TIME;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /***
     * 用户在开机时间下发定时开关及任务
     */
    public void checkTimerReduceDialog(String tag) {
        MyLog.powerOnOff("=====用户在开机时间下发定时开关及任务==" + tag);
        CheckTimeRunnable runnable = new CheckTimeRunnable(new EtvServerListener() {

            @Override
            public void jujleCurrentIsShutDownTime(boolean isShutDown) {
                if (isShutDown) {
                    jumpActivity();
                    MyLog.powerOnOff("0000======当前是开机时间", true);
                } else {
                    MyLog.powerOnOff("0000======当前是关机时间", true);
                }
            }
        });
        EtvService.getInstance().executor(runnable);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //取消时间回调监听
        TimerDealUtil.getInstance().setTimeChangeListener(-1, null);
    }
}
