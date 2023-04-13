package com.etv.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.etv.config.AppConfig;
import com.etv.config.AppInfo;
import com.etv.service.EtvService;
import com.etv.service.TcpService;
import com.etv.setting.GuardianActivity;
import com.etv.setting.InterestActivity;
import com.etv.util.CodeUtil;
import com.etv.util.FileUtil;
import com.etv.util.MyLog;
import com.etv.util.NetWorkUtils;
import com.etv.util.SharedPerManager;
import com.etv.util.SharedPerUtil;
import com.etv.util.ViewSizeChange;
import com.etv.util.image.ImageUtil;
import com.ys.etv.R;
import com.ys.model.dialog.OridinryDialog;
import com.ys.model.listener.OridinryDialogClick;

/***
 * 程序启动界面
 * 里面涉及到自动登录相关功能
 */
public class SplashLowActivity extends SplashBaseActivity {

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_splash_low);
        initView();
    }

    private void initView() {
        AppInfo.startCheckTaskTag = false;
        ImageView iv_logo_show = (ImageView) findViewById(R.id.iv_logo_show);
        ViewSizeChange.setLogoPosition(iv_logo_show);
        int showBggImage = ImageUtil.getShowBggLogo();
        iv_logo_show.setBackgroundResource(showBggImage);

        if (SharedPerUtil.SOCKEY_TYPE() == AppConfig.SOCKEY_TYPE_WEBSOCKET) {
            startService(new Intent(SplashLowActivity.this, TcpService.class));
        } else {
        }
        lineToWeb();
        handler.sendEmptyMessageDelayed(CHECK_LOGIN_STATUS, 6000);
    }

    private static final int CHECK_LOGIN_STATUS = 908;
    private static final int CHEAK_GUARDIAN_TIME_COMPANY = 909;  // 守护进程开机启动关闭
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handler.removeMessages(msg.what);
            switch (msg.what) {
                case CHECK_LOGIN_STATUS:
                    checkGuardianPowerOn();
                    break;
                case CHEAK_GUARDIAN_TIME_COMPANY:
                    startToMainView();
                    break;
            }
        }
    };

    private void lineToWeb() {
        if (!NetWorkUtils.isNetworkConnected(SplashLowActivity.this)) {
            MyLog.login("没有网络，打断操作");
            return;
        }
        if (AppConfig.isOnline) {
            MyLog.login("登陆socket success 停止操作");
            return;
        }
        if (SharedPerUtil.SOCKEY_TYPE() == AppConfig.SOCKEY_TYPE_WEBSOCKET) {
            TcpService.getInstance().lineSockeyWebServer();
        } else {
        }
    }

    private void checkGuardianPowerOn() {
        boolean openPower = getOpenPower();
        if (!openPower) {
            MyLog.cdl("====守护进程开机启动未打开===");
            showGuardianPoserOnDialog();
            return;
        }
        startToMainView();
    }

    /***
     * 显示守护进程得dialog
     */
    private void showGuardianPoserOnDialog() {
        handler.sendEmptyMessageDelayed(CHEAK_GUARDIAN_TIME_COMPANY, 30 * 1000);
        OridinryDialog oridinryDialog = new OridinryDialog(SplashLowActivity.this);
        oridinryDialog.setOnDialogClickListener(new OridinryDialogClick() {
            @Override
            public void sure() {
                handler.removeMessages(CHEAK_GUARDIAN_TIME_COMPANY);
                Intent intent = new Intent(SplashLowActivity.this, GuardianActivity.class);
                intent.putExtra(GuardianActivity.COME_IN_TAG, -1);
                startActivity(intent);
                finish();
            }

            @Override
            public void noSure() {
                handler.removeMessages(CHEAK_GUARDIAN_TIME_COMPANY);
                startToMainView();
            }
        });
        oridinryDialog.show(getString(R.string.boot_up), getString(R.string.boot_dialog_content));
    }

    /***
     * 开始进入界面
     */
    private void startToMainView() {
        boolean isSlppeStatues = SharedPerManager.getSleepStatues();
        Intent intent = new Intent();
        if (isSlppeStatues) {
            MyLog.sleep("======当前是休眠状态，关屏操作", true);
            intent.setClass(this, InterestActivity.class);
        } else {
            MyLog.sleep("======当前不是休眠状态，开屏操作", true);
            intent.setClass(this, InitViewActivity.class);
        }
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FileUtil.deleteDirOrFilePath(AppInfo.BASE_CACHE(), "===清理缓存目录==SplashLow");
        String clVersion = CodeUtil.getSystCodeVersion(SplashLowActivity.this);
        EtvService.getInstance().updateDevInfoToAuthorServer(clVersion);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeMessages(CHECK_LOGIN_STATUS);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
