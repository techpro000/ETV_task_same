package com.etv.setting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;

import com.etv.activity.BaseActivity;
import com.etv.activity.MainActivity;
import com.etv.config.AppInfo;
import com.etv.util.MyLog;

import java.util.Timer;
import java.util.TimerTask;

public class SettingBaseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        AppInfo.startCheckTaskTag = false;
    }

    public static boolean inActivityFrost = false;

    @Override
    protected void onResume() {
        super.onResume();
        AppInfo.isAppRun = true;
        inActivityFrost = true;
        startTouchUpToBack();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //有按下动作时取消定时
                break;
            case MotionEvent.ACTION_UP:
                startTouchUpToBack();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private static final int TOUCH_CLICK_BACK = 8954;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TOUCH_CLICK_BACK:
                    MyLog.cdl("==============没人点击，我要返回了");
                    Intent intent = new Intent(SettingBaseActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };

    private void startTouchUpToBack() {
        cacelTouchTimer();
        touchTimer = new Timer(true);
        touchTask = new TouchTask();
        touchTimer.schedule(touchTask, 180 * 1000);
    }

    private void cacelTouchTimer() {
        if (handler != null) {
            handler.removeMessages(TOUCH_CLICK_BACK);
        }
        if (touchTimer != null) {
            touchTimer.cancel();
        }
        if (touchTask != null) {
            touchTask.cancel();
        }
    }

    private Timer touchTimer;
    private TouchTask touchTask;

    private class TouchTask extends TimerTask {

        @Override
        public void run() {
            handler.sendEmptyMessage(TOUCH_CLICK_BACK);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        cacelTouchTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cacelTouchTimer();
        inActivityFrost = false;
    }
}
