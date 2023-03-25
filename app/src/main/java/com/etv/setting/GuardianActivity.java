package com.etv.setting;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.EtvApplication;
import com.etv.activity.SplashLowActivity;
import com.etv.config.AppInfo;
import com.etv.util.APKUtil;
import com.etv.util.MyLog;
import com.etv.util.SharedPerManager;
import com.ys.model.dialog.MyToastView;
import com.ys.model.dialog.EditTextDialog;
import com.ys.etv.R;
import com.ys.model.listener.EditTextDialogListener;
import com.ys.model.listener.MoreButtonHiddleListener;
import com.ys.model.listener.MoreButtonListener;
import com.ys.model.listener.MoreButtonToggleListener;
import com.ys.model.view.SettingClickView;
import com.ys.model.view.SettingSwitchView;

/***
 * 隐藏的设定
 */
public class GuardianActivity extends GuardianBaseActivity implements
        View.OnClickListener, MoreButtonToggleListener {

    public static final String COME_IN_TAG = "COME_IN_TAG";
    int commInTag = 0;  // 0 正常进入  -1，启动界面进来得


    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_guardian);
        initView();
        initListener();
    }

    private LinearLayout lin_exit;
    private TextView tv_exit;
    SettingClickView btn_modify_time, btn_uninstall_guardian;
    EditTextDialog editTextDialog;
    SettingSwitchView switch_statues;
    SettingSwitchView switch_open_power;
    SettingSwitchView switch_load_speed;


    private void initView() {
        try {
            commInTag = getIntent().getIntExtra(COME_IN_TAG, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        AppInfo.startCheckTaskTag = false;
        switch_statues = (SettingSwitchView) findViewById(R.id.switch_statues);
        switch_statues.setOnMoretListener(this);
        switch_open_power = (SettingSwitchView) findViewById(R.id.switch_open_power);
        switch_open_power.setOnMoretListener(this);
        switch_load_speed = (SettingSwitchView) findViewById(R.id.switch_load_speed);
        switch_load_speed.setOnMoretListener(this);
        lin_exit = (LinearLayout) findViewById(R.id.lin_exit);
        lin_exit.setOnClickListener(this);
        tv_exit = (TextView) findViewById(R.id.tv_exit);
        tv_exit.setOnClickListener(this);
        btn_modify_time = (SettingClickView) findViewById(R.id.btn_modify_time);
        btn_uninstall_guardian = (SettingClickView) findViewById(R.id.btn_uninstall_guardian);
        updateGuardianView();
    }

    private void initListener() {
        btn_modify_time.setOnMoretListener(new MoreButtonListener() {
            @Override
            public void clickView(View view) {
                showModifyAidlTime();
            }
        });
        btn_uninstall_guardian.setOnMoretListener(new MoreButtonListener() {
            @Override
            public void clickView(View view) {
                unInstallGuardianApk();
            }
        });
        btn_uninstall_guardian.setOnMoretHiddleListener(new MoreButtonHiddleListener() {
            @Override
            public void clickView(View view) {
                gotoGuardianApp();
            }
        });
    }


    @Override
    public void switchToggleView(View view, boolean isClick) {
        int id = view.getId();
        switch (id) {
            case R.id.switch_statues:
                MyLog.cdl("修改守护进程的状态: ==" + isClick, true);
                setGuardianStatues(isClick);
                updateGuardianView();
                break;
            case R.id.switch_open_power:
                MyLog.cdl("修改守护进程开机启动的状态: ==" + isClick, true);
                setOpenPower(isClick);
                updateGuardianView();
                break;
            case R.id.switch_load_speed:
                SharedPerManager.setDevSpeedStatues(isClick);
                MyLog.cdl("修改守护进程硬件加速的状态: ==" + isClick, true);
                updateGuardianView();
                break;
        }
    }


    @Override
    public void updateGuardianView() {
        //开机启动
        boolean isOpenPower = getOpenPower();
        String open = getString(R.string.open);
        String close = getString(R.string.close);
        String screen_same = getString(R.string.screen_same);
        switch_open_power.setTxtContent(isOpenPower ? open : close);
        switch_open_power.setSwitchStatues(isOpenPower);
        //硬件加速
        boolean isSpeed = SharedPerManager.getDevSpeedStatues();
        switch_load_speed.setTxtContent(isSpeed ? open : screen_same);
        switch_load_speed.setSwitchStatues(isSpeed);
        boolean isInstall = APKUtil.ApkState(GuardianActivity.this, "com.guardian");
        if (!isInstall) { //未安装
            switch_statues.setTxtContent(getString(R.string.guardian_line_failed));
            btn_modify_time.setTxtContent(getString(R.string.guardian_no_install));
            switch_statues.setSwitchStatues(false);
            return;
        }
        //守护进程打开状态
        boolean isOpenStatues = getGuardianOpenStatues();
        switch_statues.setTxtContent(isOpenStatues ? open : close);
        switch_statues.setSwitchStatues(isOpenStatues);
        String guardianAppCode = getGuardianAppCode();
        btn_modify_time.setTxtContent(guardianAppCode);
        int checkTime = getCheckTime();
        MyLog.cdl("====保存得时间=,显示000====" + checkTime);
        btn_modify_time.setTxtContent(checkTime + "  ( S )");
        String content = getGuardianAppCode();
        btn_uninstall_guardian.setTxtContent(content);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lin_exit:   //退出
            case R.id.tv_exit:  //退出
                backToView();
                break;
        }
    }

    /***
     * 修改AIDL的时间
     */
    private void showModifyAidlTime() {
        if (aidl == null) {
            MyToastView.getInstance().Toast(GuardianActivity.this, getString(R.string.guardian_line_failed));
            return;
        }
        String modifyTime = "0";
        int time = getCheckTime();
        MyLog.cdl("====获取得时间===" + time);
        modifyTime = time + "";
        if (editTextDialog == null) {
            editTextDialog = new EditTextDialog(GuardianActivity.this);
        }
        editTextDialog.show(getString(R.string.modify_guatime), modifyTime, getString(R.string.submit));
        editTextDialog.setOnDialogClickListener(new EditTextDialogListener() {

            @Override
            public void commit(String modifyName) {
                if (TextUtils.isEmpty(modifyName)) {
                    showToast(getString(R.string.please_insert));
                    return;
                }
                if (modifyName.trim().length() > 50) {
                    showToast(getString(R.string.insert_less));
                    return;
                }
                try {
                    int saveTime = Integer.parseInt(modifyName);
                    if (saveTime < 20) {
                        showToast(getString(R.string.modify_tips));
                        return;
                    }
                    MyLog.cdl("====保存得时间=====" + modifyName + " / " + saveTime);
                    modifyAidlTime(saveTime);
                    showToast(getString(R.string.modifu_success));
                    updateGuardianView();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void clickHiddleView() {

            }
        });
    }

    private void showToast(String s) {
        MyToastView.getInstance().Toast(GuardianActivity.this, s);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void gotoGuardianApp() {
        String packageName = "com.guardian";
        boolean isInstall = APKUtil.ApkState(GuardianActivity.this, packageName);
        MyLog.cdl("========守护进程安装状态====" + isInstall);
        if (!isInstall) {
            return;
        }
        Intent intent = new Intent();
        ComponentName cmp = new ComponentName("com.guardian", "com.guardian.ui.MainActivity");
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(cmp);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backToView();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void backToView() {
        if (commInTag == 0) {
            finish();
            return;
        }
        startActivity(new Intent(GuardianActivity.this, SplashLowActivity.class));
        finish();
    }

}
