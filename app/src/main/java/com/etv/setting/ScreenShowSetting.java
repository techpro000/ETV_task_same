package com.etv.setting;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.etv.config.AppInfo;
import com.etv.service.util.EtvServerModule;
import com.etv.service.util.EtvServerModuleImpl;
import com.etv.service.util.TaskServiceView;
import com.etv.util.MyLog;
import com.etv.util.NetWorkUtils;
import com.etv.util.SharedPerManager;
import com.etv.util.SharedPerUtil;
import com.etv.util.system.CpuModel;
import com.etv.view.dialog.CheckChooiceCustomDialog;
import com.ys.model.dialog.EditTextDialog;
import com.ys.etv.R;
import com.ys.model.dialog.RadioListDialog;
import com.ys.model.entity.RedioEntity;
import com.ys.model.listener.EditTextDialogListener;
import com.ys.model.listener.MoreButtonListener;
import com.ys.model.listener.MoreButtonToggleListener;
import com.ys.model.listener.RadioChooiceListener;
import com.ys.model.view.SettingClickView;
import com.ys.model.view.SettingSwitchView;

import java.util.ArrayList;
import java.util.List;

/***
 * 屏幕显示类型相关设置界面
 */
public class ScreenShowSetting extends SettingBaseActivity implements View.OnClickListener, MoreButtonListener {

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.setting_screen_show);
        initView();
        initListener();
    }

    private LinearLayout lin_exit;
    private TextView tv_exit;
    private SettingClickView btn_double_show_type;
    private SettingClickView btn_double_image_roate;
    private SettingClickView btn_capture_quetity;
    SettingSwitchView switch_info_from;

    private void initView() {
        switch_info_from = (SettingSwitchView) findViewById(R.id.switch_info_from);
        lin_exit = (LinearLayout) findViewById(R.id.lin_exit);
        tv_exit = (TextView) findViewById(R.id.tv_exit);
        lin_exit.setOnClickListener(this);
        tv_exit.setOnClickListener(this);
        btn_double_show_type = (SettingClickView) findViewById(R.id.btn_double_show_type);
        btn_double_show_type.setOnMoretListener(this);
        btn_double_image_roate = (SettingClickView) findViewById(R.id.btn_double_image_roate);
        btn_double_image_roate.setOnMoretListener(this);
        btn_capture_quetity = (SettingClickView) findViewById(R.id.btn_capture_quetity);
        btn_capture_quetity.setOnMoretListener(this);
    }


    private void initListener() {
        switch_info_from.setOnMoretListener(new MoreButtonToggleListener() {
            @Override
            public void switchToggleView(View view, boolean isChooice) {
                if (isChooice) {
                    checkInfoFromweb();
                }
                SharedPerManager.setInfoFrom(isChooice);
                updateShowView("修改信息来源");
            }
        });
    }

    EtvServerModule etvServerModule;

    private void checkInfoFromweb() {
        if (!NetWorkUtils.isNetworkConnected(ScreenShowSetting.this)) {
            return;
        }
        if (etvServerModule == null) {
            etvServerModule = new EtvServerModuleImpl();
        }
        etvServerModule.queryDeviceInfoFromWeb(ScreenShowSetting.this, new TaskServiceView() {

            @Override
            public void getDevInfoFromWeb(boolean isSuccess, String errorDesc) {

            }
        });
    }


    @Override
    public void clickView(View view) {
        switch (view.getId()) {
            case R.id.btn_capture_quetity:
                showCaptureImageQuity();
                break;
            case R.id.btn_double_image_roate:
                showDoubleImageRoateDialog();
                break;
            case R.id.btn_double_show_type:
                showDoubleScreenShowType();
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lin_exit:
            case R.id.tv_exit:
                finish();
                break;
        }
    }

    private void showCaptureImageQuity() {
        RadioListDialog radioListDialog = new RadioListDialog(ScreenShowSetting.this);
        List<RedioEntity> listShow = new ArrayList<RedioEntity>();
        listShow.add(new RedioEntity(getString(R.string.quetity_low)));
        listShow.add(new RedioEntity(getString(R.string.quetity_middle)));
        listShow.add(new RedioEntity(getString(R.string.quetity_height)));
        int roateNum = SharedPerManager.getCapturequilty();
        radioListDialog.show(getString(R.string.capture_update_height), listShow, roateNum);
        radioListDialog.setRadioChooiceListener(new RadioChooiceListener() {
            @Override
            public void backChooiceInfo(RedioEntity redioEntity, int chooicePosition) {
                SharedPerManager.setCapturequilty(chooicePosition);
                showToastView(getString(R.string.set_success));
                updateShowView("双屏图片角度设置");
            }
        });
    }

    private void showDoubleImageRoateDialog() {
        RadioListDialog radioListDialog = new RadioListDialog(ScreenShowSetting.this);
        List<RedioEntity> listShow = new ArrayList<RedioEntity>();
        listShow.add(new RedioEntity("0"));
        listShow.add(new RedioEntity("90"));
        listShow.add(new RedioEntity("180"));
        listShow.add(new RedioEntity("270"));
        int roateNum = SharedPerManager.getDoubleScreenRoateImage();
        int checkPosition = 0;
        if (roateNum == 0) {
            checkPosition = 0;
        } else if (roateNum == 90) {
            checkPosition = 1;
        } else if (roateNum == 180) {
            checkPosition = 2;
        } else if (roateNum == 270) {
            checkPosition = 3;
        }
        radioListDialog.show("副屏截图旋转角度", listShow, checkPosition);
        radioListDialog.setRadioChooiceListener(new RadioChooiceListener() {
            @Override
            public void backChooiceInfo(RedioEntity redioEntity, int chooicePosition) {
                int roateNum = 0;
                if (chooicePosition == 0) {
                    roateNum = 0;
                } else if (chooicePosition == 1) {
                    roateNum = 90;
                } else if (chooicePosition == 2) {
                    roateNum = 180;
                } else if (chooicePosition == 3) {
                    roateNum = 270;
                }
                SharedPerManager.setDoubleScreenRoateImage(roateNum);
                showToastView("设置成功");
                updateShowView("双屏图片角度设置");
            }
        });
    }

    /***
     * 双屏异显算法
     */
    private void showDoubleScreenShowType() {
        List<RedioEntity> lists = new ArrayList<>();
        lists.add(new RedioEntity(getString(R.string.default_size_show)));
        lists.add(new RedioEntity(getString(R.string.ajust_screen)));
        lists.add(new RedioEntity(getString(R.string.Interface_reverse)));
        lists.add(new RedioEntity(getString(R.string.long_width_change)));
        int showType = SharedPerManager.getDoubleScreenMath();
        RadioListDialog radioListDialog = new RadioListDialog(ScreenShowSetting.this);
        radioListDialog.setRadioChooiceListener(new RadioChooiceListener() {
            @Override
            public void backChooiceInfo(RedioEntity redioEntity, int chooicePosition) {
                SharedPerManager.setDoubleScreenMath(chooicePosition);
                showToastView("Success");
                updateShowView("设置图片的显示类型");
            }
        });
        radioListDialog.show("Type", lists, showType);
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateShowView("onResume");
    }

    private void updateShowView(String tag) {
        boolean isFromWeb = SharedPerManager.getInfoFrom();
        switch_info_from.setSwitchStatues(isFromWeb);
        switch_info_from.setTxtContent(isFromWeb ? getString(R.string.screen_double_net_model) : getString(R.string.screen_double_net_local));
        MyLog.cdl("========界面刷新==" + tag);
        int showType = SharedPerManager.getDoubleScreenMath();
        String doublwShow;
        if (showType == AppInfo.DOUBLE_SCREEN_SHOW_DEFAULT) {
            doublwShow = getString(R.string.default_size_show);
        } else if (showType == AppInfo.DOUBLE_SCREEN_SHOW_ADAPTER) {
            doublwShow = getString(R.string.ajust_screen);
        } else if (showType == AppInfo.DOUBLE_SCREEN_SHOW_GT_TRANS) {
            //高通反向
            doublwShow = getString(R.string.Interface_reverse);
        } else {
            //长宽志换
            doublwShow = getString(R.string.long_width_change);
        }
        btn_double_show_type.setTxtContent(doublwShow);
        int roateNum = SharedPerManager.getDoubleScreenRoateImage();
        btn_double_image_roate.setTxtContent(roateNum + "");

        int captureType = SharedPerManager.getCapturequilty();
        if (captureType == 0) {
            btn_capture_quetity.setTxtContent(getString(R.string.quetity_low));
        } else if (captureType == 1) {
            btn_capture_quetity.setTxtContent(getString(R.string.quetity_middle));
        } else if (captureType == 2) {
            btn_capture_quetity.setTxtContent(getString(R.string.quetity_height));
        }
    }
}