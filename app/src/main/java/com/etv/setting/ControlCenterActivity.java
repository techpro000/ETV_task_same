package com.etv.setting;

import android.os.Bundle;
import android.view.View;

import com.etv.util.SharedPerManager;
import com.etv.util.SharedPerUtil;
import com.etv.util.system.CpuModel;
import com.ys.etv.R;
import com.ys.etv.databinding.ActivityControlCenterBinding;
import com.ys.model.listener.MoreButtonToggleListener;

/***
 * 控制中心
 */
public class ControlCenterActivity extends SettingBaseActivity implements MoreButtonToggleListener, View.OnClickListener {

    ActivityControlCenterBinding mBinding;

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        mBinding = ActivityControlCenterBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initView();
    }

    private void initView() {
        mBinding.linExit.setOnClickListener(this);
        mBinding.tvExit.setOnClickListener(this);
        mBinding.switchPlanPerson.setOnMoretListener(this);
        mBinding.switchVideoSize.setOnMoretListener(this);
        mBinding.switchTaskSame.setOnMoretListener(this);
        mBinding.switchMainBgg.setOnMoretListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_exit:
            case R.id.tv_exit:
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateToggleStatues();
    }

    @Override
    public void switchToggleView(View view, boolean isChooice) {
        switch (view.getId()) {
            case R.id.switch_plan_person:
                SharedPerManager.setAutoRebootDev(isChooice);
                showToastView(getLanguageFromResurce(R.string.set_success));
                break;
            case R.id.switch_video_size:
                SharedPerManager.setVideoMoreSize(isChooice);
                showToastView(getLanguageFromResurce(R.string.set_success));
                break;
/*            case R.id.switch_task_main:
                SharedPerManager.setTaskSameMain(isChooice ? 0 : 1);
                showToastView(getLanguageFromResurce(R.string.set_success));
                break;*/
            case R.id.switch_task_same:
                //SharedPerManager.setTaskSameEnable(isChooice);
                showToastView(getLanguageFromResurce(R.string.set_success));
                break;
            case R.id.switch_main_bgg:
                SharedPerManager.setBggImageFromWeb(isChooice);
                showToastView(getLanguageFromResurce(R.string.set_success));
                break;
        }
        updateToggleStatues();
    }

    private void updateToggleStatues() {
       // mBinding.switchTaskMain.setSwitchStatues(SharedPerUtil.getSameTaskMainLeader());
        mBinding.switchPlanPerson.setSwitchStatues(SharedPerManager.getAutoRebootDev());
        mBinding.switchVideoSize.setSwitchStatues(SharedPerManager.getVideoMoreSize());
        //mBinding.switchTaskSame.setSwitchStatues(SharedPerManager.getTaskSameEnable());
       // mBinding.switchTaskMain.setVisibility(SharedPerManager.getTaskSameEnable() ? View.VISIBLE : View.GONE);
        mBinding.switchMainBgg.setSwitchStatues(SharedPerManager.getBggImageFromWeb());

        String open = getLanguageFromResurce(R.string.screen_same);
        String close = getLanguageFromResurce(R.string.close);
    }

}
