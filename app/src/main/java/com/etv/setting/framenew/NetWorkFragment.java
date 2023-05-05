package com.etv.setting.framenew;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.etv.config.AppInfo;
import com.etv.setting.TaskInfoActivity;
import com.etv.setting.parsener.TerminallParsener;
import com.etv.setting.view.TerminallView;
import com.etv.util.CodeUtil;
import com.etv.util.MyLog;
import com.etv.util.SharedPerManager;
import com.etv.util.SharedPerUtil;
import com.etv.util.apwifi.WifiMgr;
import com.etv.util.location.ProCityDialogActivity;
import com.ys.etv.R;
import com.ys.etv.databinding.FragmentNetWorkSettingBinding;
import com.ys.model.dialog.EditTextDialog;
import com.ys.model.dialog.MyToastView;
import com.ys.model.dialog.OridinryDialog;
import com.ys.model.dialog.WaitDialogUtil;
import com.ys.model.listener.EditTextDialogListener;
import com.ys.model.listener.MoreButtonListener;
import com.ys.model.listener.MoreButtonToggleListener;
import com.ys.model.listener.OridinryDialogClick;

/**
 * 网络下发模式设置
 */
public class NetWorkFragment extends MessageFragment implements TerminallView, MoreButtonListener {

    /**
     * 百度地图地址更新成功，这里刷新界面
     */
    @Override
    public void updateNetView() {
        MyLog.message("=======onChanged==updateNetView====000==");
        if (waitDialogUtil != null) {
            waitDialogUtil.dismiss();
        }
        updateView();
    }

    FragmentNetWorkSettingBinding mBinding;

    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        mBinding = FragmentNetWorkSettingBinding.inflate(getActivity().getLayoutInflater());
        View view = mBinding.getRoot();
        initView(view);
        return view;
    }


    OridinryDialog oridinryDialog;
    WaitDialogUtil waitDialogUtil;

    private TerminallParsener terminallPansener;

    private void initView(View view) {
        mBinding.btnDevInfo.setOnMoretListener(new MoreButtonListener() {
            @Override
            public void clickView(View view) {
                showDevInfoDialog();
            }
        });

        mBinding.switchPlayUpdate.setOnMoretListener(new MoreButtonToggleListener() {
            @Override
            public void switchToggleView(View view, boolean isChooice) {
                MyToastView.getInstance().Toast(getActivity(), getLanguageFromResurce(R.string.net_update_chooice));
                updateView();
            }
        });

        String code = CodeUtil.getEthMAC();
        MyLog.e("cdl", "======Mac = " + code);
        oridinryDialog = new OridinryDialog(getActivity());
        mBinding.btnMidifyLocation.setOnMoretListener(this);
        mBinding.btnTaskPlay.setOnMoretListener(this);
        String id = CodeUtil.getUniquePsuedoID();
        mBinding.btnCommitNickname.setOnMoretListener(this);
        editTextDialog = new EditTextDialog(getActivity());
        terminallPansener = new TerminallParsener(getActivity(), this);
        terminallPansener.queryNickName();
        updateView();
    }

    private void showDevInfoDialog() {
        OridinryDialog oridinryDialog = new OridinryDialog(getActivity());
        oridinryDialog.setOnDialogClickListener(new OridinryDialogClick() {
            @Override
            public void sure() {

            }

            @Override
            public void noSure() {

            }
        });
        String nickName = SharedPerManager.getDevNickName();
        String ipAddredd = SharedPerUtil.getSocketIpAddress() + ":" + SharedPerUtil.getSocketPort();
        String sourceDoanPath = SharedPerUtil.getSocketDownPath();
        String devInfo = "NickName: " + nickName + "\n" +
            "IpAddredd: " + ipAddredd + "\n" +
            "SoucePath: " + sourceDoanPath;
        oridinryDialog.show(getString(R.string.dev_info), devInfo);
    }


    @Override
    public void clickView(View view) {
        switch (view.getId()) {
            case R.id.btn_midify_location:  //定位
                showLocationDialog();
                break;
            case R.id.btn_task_play:
                startActivity(new Intent(getActivity(), TaskInfoActivity.class));
                break;
            case R.id.btn_commit_nickname:      //修改昵称
                showEditSubmitDialog();
                break;
        }
    }


    public void onResumeView() {
        AppInfo.startCheckTaskTag = false;
        updateView();
    }

    /***
     * 点击定位，选择定位的刷新方式
     */
    private void showLocationDialog() {
        if (oridinryDialog == null) {
            oridinryDialog = new OridinryDialog(getActivity());
        }
        oridinryDialog.show(getLanguageFromResurce(R.string.chooice_refrash), getLanguageFromResurce(R.string.location_hand), getLanguageFromResurce(R.string.location_auto));
        oridinryDialog.setOnDialogClickListener(new OridinryDialogClick() {
            @Override
            public void sure() {
                startActivity(new Intent(getActivity(), ProCityDialogActivity.class));
            }

            @Override
            public void noSure() {
                boolean isWifiOpen = WifiMgr.getInstance(getActivity()).isWifiEnable();
                if (isWifiOpen) {
                    SharedPerManager.setAutoLocation(true);
                    return;
                }
                showOpenWifiDialog();
            }
        });
    }

    private void showOpenWifiDialog() {
        OridinryDialog oridinryDialog = new OridinryDialog(getActivity());
        oridinryDialog.setOnDialogClickListener(new OridinryDialogClick() {
            @Override
            public void sure() {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)); //直接进入手机中的wifi网络设置界面
            }

            @Override
            public void noSure() {
            }
        });
        oridinryDialog.show(getLanguageFromResurce(R.string.net_wear_title), getLanguageFromResurce(R.string.submit), getLanguageFromResurce(R.string.cancel));
    }

    EditTextDialog editTextDialog;

    private void showEditSubmitDialog() {
        editTextDialog.show(getLanguageFromResurce(R.string.modify_nick), mBinding.btnCommitNickname.getTextContent(), getLanguageFromResurce(R.string.submit));
        editTextDialog.setOnDialogClickListener(new EditTextDialogListener() {
            @Override
            public void clickHiddleView() {

            }

            @Override
            public void commit(String content) {
                if (content == null || content.length() < 2) {
                    shotToastView(getLanguageFromResurce(R.string.insert_legitimate));
                    return;
                }
                if (content.length() > 50) {
                    shotToastView(getLanguageFromResurce(R.string.insert_less));
                }
                SharedPerManager.setDevNickName(content);
                mBinding.btnCommitNickname.setTxtContent(SharedPerManager.getDevNickName());
                terminallPansener.modifyNickName(content);
            }
        });
    }

    private void updateView() {
        mBinding.switchPlayUpdate.setSwitchStatues(SharedPerManager.getPlayTotalUpdate());
        mBinding.switchPlayUpdate.setTxtContent(SharedPerManager.getPlayTotalUpdate() ? getString(R.string.open) : getString(R.string.close));
        mBinding.btnMidifyLocation.setTxtContent(SharedPerManager.getAllAddress());
        mBinding.btnCommitNickname.setTxtContent(SharedPerManager.getDevNickName());
        terminallPansener.updateDevInfoToWeb(getActivity());
    }

    @Override
    public void showWaitDialog(boolean isShow) {
        if (waitDialogUtil == null) {
            waitDialogUtil = new WaitDialogUtil(getActivity());
        }
        if (isShow) {
            waitDialogUtil.show("Dealing...");
        } else {
            waitDialogUtil.dismiss();
        }
    }

    @Override
    public void shotToastView(String toast) {
        MyToastView.getInstance().Toast(getActivity(), toast);
    }

    @Override
    public void queryNickName(boolean isSuccess, String nickName) {
        MyLog.i("cdl", "=======显示的昵称==" + nickName);
        if (isSuccess) {
            SharedPerManager.setDevNickName(nickName);
        }
        mBinding.btnCommitNickname.setTxtContent(SharedPerManager.getDevNickName());
    }

    public String getLanguageFromResurce(int resourceId) {
        String desc = getActivity().getResources().getString(resourceId);
        return desc;
    }

    public String getLanguageFromResurceWithPosition(int resourceId, String desc) {
        String stringStart = getActivity().getResources().getString(resourceId);
        String startResult = String.format(stringStart, desc);
        return startResult;
    }


}
