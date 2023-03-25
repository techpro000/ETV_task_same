package com.etv.setting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.etv.activity.BaseActivity;
import com.etv.activity.MainActivity;
import com.etv.config.AppInfo;
import com.etv.util.MyLog;
import com.etv.util.SharedPerManager;
import com.etv.util.SimpleDateUtil;
import com.etv.util.system.DoubleScreenManager;
import com.etv.util.system.SystemManagerInstance;
import com.ys.etv.R;

/***
 * 休眠界面
 */
public class InterestActivity extends BaseActivity implements View.OnClickListener {

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(AppInfo.SCREEN_BACK_LIGHT_OPEN)) {
                //屏幕亮了，直接回到主界面，播放任务
                //亮屏
                SystemManagerInstance.getInstance(InterestActivity.this).turnBackLightTtatues(true);
                startToMainView();
            } else if (action.equals(Intent.ACTION_TIME_TICK)) {
                updateCurrentTime();
            }
        }
    };

    /**
     * 回到主界面
     */
    private void startToMainView() {
        //准备请求任务信息
        MainActivity.IS_ORDER_REQUEST_TASK = true;
        startActivity(new Intent(InterestActivity.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_interest);
        MyLog.cdl("====休眠状态");
        initView();
        initReceiver();
        getScreenInfo();
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppInfo.SCREEN_BACK_LIGHT_OPEN);
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(receiver, filter);
    }

    /**
     * 获取屏幕相关的信息
     */
    private void getScreenInfo() {
        DoubleScreenManager.getInstance(InterestActivity.this).getDevScreenNumFroSys();
    }

    LinearLayout lin_exit;
    TextView tv_exit;
    TextView tv_current_timr;

    private void initView() {
        AppInfo.startCheckTaskTag = false;
        lin_exit = (LinearLayout) findViewById(R.id.lin_exit);
        tv_exit = (TextView) findViewById(R.id.tv_exit);
        tv_current_timr = (TextView) findViewById(R.id.tv_current_timr);
        lin_exit.setOnClickListener(this);
        tv_exit.setOnClickListener(this);
        findViewById(R.id.btn_view_click).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                SystemManagerInstance.getInstance(InterestActivity.this).turnBackLightTtatues(true);
                startActivity(new Intent(InterestActivity.this, MainActivity.class));
                finish();
                return true;
            }
        });
    }

    private void updateCurrentTime() {
        if (tv_current_timr != null) {
            tv_current_timr.setText("Sleep: " + SimpleDateUtil.getTime());
        }
    }

    public static boolean isMainView = false;

    @Override
    protected void onResume() {
        super.onResume();
        AppInfo.isAppRun = true;
        updateCurrentTime();
        //进入息屏界面
        isMainView = true;
        SystemManagerInstance.getInstance(InterestActivity.this).turnBackLightTtatues(false);
        SharedPerManager.setSleepStatues(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_exit:
            case R.id.tv_exit:
                startToMainView();
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isMainView = false;
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startToMainView();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
