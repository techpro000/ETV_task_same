package com.etv.setting;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.etv.config.AppInfo;
import com.etv.util.MyLog;
import com.etv.util.RootCmd;
import com.etv.util.system.CpuModel;
import com.etv.util.system.SystemManagerInstance;
import com.etv.util.system.SystemManagerUtil;
import com.ys.etv.R;
import com.ys.etv.databinding.ActivityScreenSettingBinding;
import com.ys.model.dialog.OridinryDialog;
import com.ys.model.dialog.RadioListDialog;
import com.ys.model.entity.RedioEntity;
import com.ys.model.listener.MoreButtonListener;
import com.ys.model.listener.MoreButtonSeekBarListener;
import com.ys.model.listener.MoreButtonToggleListener;
import com.ys.model.listener.OridinryDialogClick;
import com.ys.model.listener.RadioChooiceListener;
import com.ys.model.view.SettingClickView;
import com.ys.model.view.SettingSeekBarView;
import com.ys.model.view.SettingSwitchView;

import java.util.ArrayList;
import java.util.List;

public class ScreenSettingActivity extends SettingBaseActivity implements View.OnClickListener {


    ActivityScreenSettingBinding mBingding;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBingding = ActivityScreenSettingBinding.inflate(getLayoutInflater());
        setContentView(mBingding.getRoot());
        initView();
        inltListener();
    }

    private void initView() {
        AppInfo.startCheckTaskTag = false;
        mBingding.linExit.setOnClickListener(this);
        mBingding.tvExit.setOnClickListener(this);

        mBingding.seekLight.setOnMoretListener(new MoreButtonSeekBarListener() {
            @Override
            public void switchToggleView(View view, int progress) {
                SystemManagerInstance.getInstance(ScreenSettingActivity.this).setScreenLightProgress(progress);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMainView();
    }

    private void updateMainView() {
        if (CpuModel.getMobileType().startsWith(CpuModel.CPU_MODEL_MTK_M11)) {
            mBingding.switchOpenPower.setVisibility(View.GONE);
        }
        boolean isBackLightOpen = SystemManagerInstance.getInstance(ScreenSettingActivity.this).getBackLightTtatues("屏幕设置界面");
        MyLog.e("light", "======当前背光的状态===" + isBackLightOpen);
        mBingding.switchOpenPower.setSwitchStatues(isBackLightOpen);
        //更新旋转角度
        updateScreenRoate();
        //更新屏幕亮度
        updateBlightInfo();
    }

    private void inltListener() {
        mBingding.btnSubmit.setOnMoretListener(new MoreButtonListener() {
            @Override
            public void clickView(View view) {
                showModifyScreenRoateNumDialog();
            }
        });

        mBingding.switchOpenPower.setOnMoretListener(new MoreButtonToggleListener() {
            @Override
            public void switchToggleView(View view, boolean isChooice) {
                SystemManagerInstance.getInstance(ScreenSettingActivity.this).turnBackLightTtatues(isChooice);
            }
        });
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

    OridinryDialog oridinryDialog;
    int changeRoate = 0;

    public void setScreenRotation(int rotationNum) {
        MyLog.e("CDL", "=========设置旋转的角度===" + rotationNum);
        changeRoate = 0;
        switch (rotationNum) {
            case 0:
                changeRoate = 0;
                break;
            case 1:
                changeRoate = 90;
                break;
            case 2:
                changeRoate = 180;
                break;
            case 3:
                changeRoate = 270;
                break;
        }
        if (oridinryDialog == null) {
            oridinryDialog = new OridinryDialog(ScreenSettingActivity.this);
        }
        oridinryDialog.setOnDialogClickListener(new OridinryDialogClick() {
            @Override
            public void sure() {
                SystemManagerInstance.getInstance(ScreenSettingActivity.this).rotateScreen(ScreenSettingActivity.this, changeRoate + "");
                finish();
            }

            @Override
            public void noSure() {

            }
        });
        oridinryDialog.show(getLanguageFromResurce(R.string.if_roate_screen) + " < " + changeRoate + " > ", getLanguageFromResurce(R.string.submit), getLanguageFromResurce(R.string.cancel));
    }


    private void updateBlightInfo() {
        int lightProgress = 0;
        if (CpuModel.getMobileType().startsWith(CpuModel.CPU_MODEL_MTK_M11)) {
            lightProgress = Settings.Global.getInt(getContentResolver(), "picture_backlight", 0);
        } else {
            int lightNum = SystemManagerUtil.getSystemBrightness(ScreenSettingActivity.this);
            MyLog.cdl("===获取得屏幕亮度===" + lightNum);
            int max = 255;
            lightProgress = lightNum * 100 / max;
        }
        mBingding.seekLight.setProgressNum(lightProgress);
    }


    /***
     * 更新旋转角度的问题
     */
    private void updateScreenRoate() {
        int roateNum = getScreenRoate();
        Log.e("cdl", "当前屏幕得角度===" + roateNum);
        // 90 180 270 0  这是 RK得主板
        // 1  2   3   这是 mlogic 的主板
        mBingding.btnSubmit.setTxtContent("0");
        switch (roateNum) {
            case 0:
                mBingding.btnSubmit.setTxtContent("0");
                break;
            case 90:
            case 1:
                mBingding.btnSubmit.setTxtContent("90");
                break;
            case 180:
            case 2:
                mBingding.btnSubmit.setTxtContent("180");
                break;
            case 270:
            case 3:
                mBingding.btnSubmit.setTxtContent("270");
                break;
        }
    }

    /**
     * 获取屏幕旋转的角度
     *
     * @return
     */
    public int getScreenRoate() {
        int roateNum = 0;
        if (CpuModel.ISRK312X()) {
            roateNum = SystemManagerUtil.getScreenRoate(ScreenSettingActivity.this);
        } else {
            String roate = RootCmd.getProperty(RootCmd.PROOERTY_INFO, "0");
            roateNum = Integer.parseInt(roate);
        }
        return roateNum;
    }


    /**
     * 筛选获取后的位置
     *
     * @return
     */
    public int getScreenRoateNum() {
        int bacnNum = 0;
        int roateNum = getScreenRoate();
        switch (roateNum) {
            case 0:
                bacnNum = 0;
                break;
            case 90:
                bacnNum = 1;
                break;
            case 180:
                bacnNum = 2;
                break;
            case 270:
                bacnNum = 3;
                break;
        }
        return bacnNum;
    }

    private void showModifyScreenRoateNumDialog() {
        int currentNum = getScreenRoateNum();
        RadioListDialog radioListDialog = new RadioListDialog(ScreenSettingActivity.this);
        List<RedioEntity> redioEntityList = new ArrayList<RedioEntity>();
        redioEntityList.add(new RedioEntity("0"));
        redioEntityList.add(new RedioEntity("90"));
        redioEntityList.add(new RedioEntity("180"));
        redioEntityList.add(new RedioEntity("270"));
        radioListDialog.setRadioChooiceListener(new RadioChooiceListener() {
            @Override
            public void backChooiceInfo(RedioEntity redioEntity, int chooicePosition) {
                if (currentNum == chooicePosition) {
                    //用户没有点击
                    finish();
                    return;
                }
                setScreenRotation(chooicePosition);
            }
        });
        radioListDialog.show(getString(R.string.screen_roate), redioEntityList, currentNum);
    }


}
