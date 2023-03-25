package com.etv.setting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.etv.config.AppInfo;
import com.etv.setting.app.AppManagerActivity;
import com.etv.util.SharedPerManager;
import com.etv.util.guardian.GuardianUtil;
import com.etv.view.dialog.ImageShowDialog;
import com.ys.etv.R;
import com.ys.model.dialog.EditTextDialog;
import com.ys.model.dialog.RadioListDialog;
import com.ys.model.entity.RedioEntity;
import com.ys.model.listener.EditTextDialogListener;
import com.ys.model.listener.MoreButtonListener;
import com.ys.model.listener.RadioChooiceListener;
import com.ys.model.view.SettingClickView;

import java.util.ArrayList;
import java.util.List;

/***
 * 隐藏的设定
 */
public class HiddleSetActivity extends SettingBaseActivity implements View.OnClickListener, MoreButtonListener {


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(AppInfo.SEND_IMAGE_CAPTURE_SUCCESS)) {
                String imagePath = AppInfo.CAPTURE_MAIN;
                showImageDialogShow(imagePath);
            }
        }
    };

    private void showImageDialogShow(String imagePath) {
        ImageShowDialog imageShowDialog = new ImageShowDialog(HiddleSetActivity.this);
        imageShowDialog.showDialog(imagePath);
    }

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_hiddle);
        initView();
        initReceiver();
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppInfo.SEND_IMAGE_CAPTURE_SUCCESS);
        registerReceiver(receiver, filter);
    }

    private LinearLayout lin_exit;
    private TextView tv_exit;
    SettingClickView btn_exit_pass, btn_net_load, btn_update_introduce, btn_app_manager;
    SettingClickView btn_capture;

    private void initView() {
        AppInfo.startCheckTaskTag = false;
        lin_exit = (LinearLayout) findViewById(R.id.lin_exit);
        lin_exit.setOnClickListener(this);
        tv_exit = (TextView) findViewById(R.id.tv_exit);
        tv_exit.setOnClickListener(this);
        btn_exit_pass = (SettingClickView) findViewById(R.id.btn_exit_pass);
        btn_exit_pass.setOnMoretListener(this);
        btn_net_load = (SettingClickView) findViewById(R.id.btn_net_load);
        btn_net_load.setOnMoretListener(this);
        btn_update_introduce = (SettingClickView) findViewById(R.id.btn_update_introduce);
        btn_update_introduce.setOnMoretListener(this);
        btn_app_manager = (SettingClickView) findViewById(R.id.btn_app_manager);
        btn_app_manager.setOnMoretListener(this);
        btn_capture = (SettingClickView) findViewById(R.id.btn_capture);
        btn_capture.setOnMoretListener(this);
    }

    @Override
    public void clickView(View view) {
        switch (view.getId()) {
            case R.id.btn_capture:
                int screenWidth = SharedPerManager.getScreenWidth();
                int screenHeight = SharedPerManager.getScreenHeight();
                GuardianUtil.getCaptureImage(HiddleSetActivity.this, screenWidth, screenHeight, AppInfo.TAG_UPDATE);
                break;
            case R.id.btn_app_manager:
                startActivity(new Intent(HiddleSetActivity.this, AppManagerActivity.class));
                break;
            case R.id.btn_update_introduce:
                startActivity(new Intent(HiddleSetActivity.this, UpdateInfoActivity.class));
                break;
            case R.id.btn_net_load:
                showModifyShowDistaskDialog();
                break;
            case R.id.btn_exit_pass:
                showEditPasswordDialog();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMainView();
    }

    private void updateMainView() {
        btn_exit_pass.setTxtContent(SharedPerManager.getExitpassword());
        boolean isShowTask = SharedPerManager.getShowNetDownTask();
        btn_net_load.setTxtContent(isShowTask ? "显示" : "隐藏");
    }

    /**
     * 修改是否隐藏网络导入功能
     */
    private void showModifyShowDistaskDialog() {
        RadioListDialog radioListDialog = new RadioListDialog(HiddleSetActivity.this);
        List<RedioEntity> listRadio = new ArrayList<RedioEntity>();
        listRadio.add(new RedioEntity(getString(R.string.show_net_load_tag)));
        listRadio.add(new RedioEntity(getString(R.string.hiddle_net_load_tag)));
        radioListDialog.setRadioChooiceListener(new RadioChooiceListener() {

            @Override
            public void backChooiceInfo(RedioEntity redioEntity, int chooicePosition) {
                if (chooicePosition == 1) {
                    SharedPerManager.setShowNetDownTask(true);
                    showToastView(getString(R.string.show_net_load_tag));
                } else {
                    SharedPerManager.setShowNetDownTask(false);
                    showToastView(getString(R.string.hiddle_net_load_tag));
                }
                updateMainView();
            }
        });
        boolean isShowTask = SharedPerManager.getShowNetDownTask();
        radioListDialog.show(getString(R.string.net_load_setting), listRadio, isShowTask ? 0 : 1);
    }

    /**
     * 显示设置密码
     */
    private void showEditPasswordDialog() {
        EditTextDialog dialog = new EditTextDialog(HiddleSetActivity.this);
        dialog.setOnDialogClickListener(new EditTextDialogListener() {
            @Override
            public void commit(String modifyName) {
                SharedPerManager.setExitpassword(modifyName);
                showToastView("这里只作离线修改,联网会自动同步服务器设置！");
                updateMainView();
            }

            @Override
            public void clickHiddleView() {

            }
        });
        String passwordExit = SharedPerManager.getExitpassword();
        dialog.show(getString(R.string.exit_pass_setting), passwordExit, getString(R.string.close));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lin_exit:   //退出
            case R.id.tv_exit:  //退出
                finish();
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }


}

