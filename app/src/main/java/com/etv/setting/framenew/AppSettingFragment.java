package com.etv.setting.framenew;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.etv.setting.ControlCenterActivity;
import com.etv.setting.GeneralSetActivity;
import com.etv.setting.GuardianActivity;
import com.etv.setting.HiddleSetActivity;
import com.etv.setting.ScreenSettingActivity;
import com.etv.setting.ScreenShowSetting;
import com.etv.setting.SettingScreenActivity;
import com.etv.util.CodeUtil;
import com.etv.util.ScreenUtil;
import com.etv.util.SharedPerManager;
import com.ys.etv.R;
import com.ys.etv.databinding.FragmentSettingSysBinding;
import com.ys.model.listener.MoreButtonListener;

/**
 * 系统设置界面
 */
public class AppSettingFragment extends Fragment implements View.OnClickListener, MoreButtonListener {

    public Animation onCreateAnimation(int paramInt1, boolean paramBoolean, int paramInt2) {
        if (paramBoolean) {
            return AnimationUtils.loadAnimation(getActivity(), R.anim.anim_enter);
        }
        return AnimationUtils.loadAnimation(getActivity(), R.anim.anim_exit);
    }

    FragmentSettingSysBinding mBinding;

    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        mBinding = FragmentSettingSysBinding.inflate(getActivity().getLayoutInflater());
        View view = mBinding.getRoot();
        initView();
        return view;
    }

    private void initView() {
        mBinding.btnLineScreen.setOnMoretListener(this);
        mBinding.generalSetting.setOnMoretListener(this);
        mBinding.btnControlCenter.setOnMoretListener(this);
        mBinding.btnScreenShowSetting.setOnMoretListener(this);
        mBinding.tvVersionInfo.setOnClickListener(this);
        mBinding.btnGuardianSetting.setOnMoretListener(this);
        mBinding.btnScreenSetting.setOnMoretListener(this);
        mBinding.btnGotoHiddle.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        updateToggleStatues();
    }

    int clickNum = 0;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_version_info:
                if (clickNum > 3) {
                    mBinding.btnGotoHiddle.setVisibility(View.VISIBLE);
                }
                clickNum++;
                break;
            case R.id.btn_goto_hiddle:
                startActivity(new Intent(getActivity(), HiddleSetActivity.class));
                break;
        }
    }

    @Override
    public void clickView(View view) {
        switch (view.getId()) {
            case R.id.btn_line_screen:  // 连屏设置
                startActivity(new Intent(getActivity(), SettingScreenActivity.class));
                break;
            case R.id.general_setting: // 通用设置
                Intent intent = new Intent(getActivity(), GeneralSetActivity.class);
                intent.putExtra(GeneralSetActivity.TAG_SETTING_INFO, -1);
                startActivity(intent);
                break;
            case R.id.btn_control_center:
                startActivity(new Intent(getActivity(), ControlCenterActivity.class));
                break;
            case R.id.btn_guardian_setting:
                startActivity(new Intent(getActivity(), GuardianActivity.class));
                break;
            case R.id.btn_screen_show_setting:
                //显示设置，全屏显示还是同比例缩放
                startActivity(new Intent(getActivity(), ScreenShowSetting.class));
                break;
            case R.id.btn_screen_setting:
                startActivity(new Intent(getActivity(), ScreenSettingActivity.class));
                break;
        }
    }

    private void updateToggleStatues() {
        String sysCode = CodeUtil.getSystCodeVersion(getActivity());
        mBinding.tvVersionInfo.setText(sysCode);
        mBinding.btnScreenShowSetting.setTxtContent(ScreenUtil.getresolution());
    }

}
