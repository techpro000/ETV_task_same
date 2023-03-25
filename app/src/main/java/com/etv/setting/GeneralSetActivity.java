package com.etv.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.etv.activity.MainActivity;
import com.etv.util.MyLog;
import com.etv.util.system.CpuModel;
import com.etv.util.system.SystemManagerInstance;
import com.ys.etv.R;
import com.ys.etv.databinding.ActicityGeneralViewBinding;
import com.ys.model.dialog.OridinryDialog;
import com.ys.model.listener.MoreButtonListener;
import com.ys.model.listener.OridinryDialogClick;

/***
 * 通用设置界面
 */
public class GeneralSetActivity extends SettingBaseActivity implements View.OnClickListener, MoreButtonListener {


    ActicityGeneralViewBinding mBinding;

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        mBinding = ActicityGeneralViewBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initView();
    }

    public static final String TAG_SETTING_INFO = "TAG_SETTING_INFO";
    private int TAG_FROM = -1; //-1  回到设置界面  1 回到主界面

    private void initView() {
        Intent intent = getIntent();
        TAG_FROM = intent.getIntExtra(TAG_SETTING_INFO, -1);
        mBinding.linExit.setOnClickListener(this);
        mBinding.btnSdmanager.setOnMoretListener(this);
        mBinding.settingViewTime.setOnMoretListener(this);
        mBinding.btnTimePoweronoff.setOnMoretListener(this);
        mBinding.btnRebootHand.setOnMoretListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_exit:
                backViewToFront();
                break;
        }
    }

    private void backViewToFront() {
        if (TAG_FROM < 0) {
            finish();
        } else {
            MainActivity.IS_ORDER_REQUEST_TASK = true;
            startActivity(new Intent(GeneralSetActivity.this, MainActivity.class));
            finish();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backViewToFront();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void clickView(View view) {
        switch (view.getId()) {
            case R.id.btn_reboot_hand:
                MyLog.cdl("=========btn_reboot_hand=======");
                showRebootDialog();
                break;
            case R.id.btn_sdmanager:
                startActivity(new Intent(GeneralSetActivity.this, StorageActivity.class));
                break;
            case R.id.setting_view_time:
                startActivity(new Intent(GeneralSetActivity.this, TimeSettingActivity.class));
                break;
            case R.id.btn_time_poweronoff:
                startActivity(new Intent(GeneralSetActivity.this, PowerOnOffWebActivity.class));
                break;
        }
    }

    private void showRebootDialog() {
        OridinryDialog oridinryDialog = new OridinryDialog(GeneralSetActivity.this);
        oridinryDialog.setOnDialogClickListener(new OridinryDialogClick() {
            @Override
            public void sure() {
                SystemManagerInstance.getInstance(GeneralSetActivity.this).rebootDev();
            }

            @Override
            public void noSure() {

            }
        });
        oridinryDialog.show(getString(R.string.tip_reboot), getString(R.string.app_reboot_content));
    }


    @Override
    protected void onResume() {
        super.onResume();
        mBinding.settingViewTime.requestFocus();
    }


}
