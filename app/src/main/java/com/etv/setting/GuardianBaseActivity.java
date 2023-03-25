package com.etv.setting;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.etv.config.AppInfo;
import com.etv.util.APKUtil;
import com.etv.util.RootCmd;
import com.etv.util.SharedPerManager;
import com.etv.util.guardian.GuardianUtil;
import com.etv.util.system.SystemManagerInstance;
import com.etv.util.system.SystemManagerUtil;
import com.guardian.GuardInterface;
import com.ys.etv.R;
import com.ys.model.dialog.MyToastView;
import com.ys.model.dialog.OridinryDialog;
import com.ys.model.listener.OridinryDialogClick;

/***
 * 隐藏的设定
 */
public abstract class GuardianBaseActivity extends SettingBaseActivity {

    GuardInterface aidl = null;
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            aidl = GuardInterface.Stub.asInterface(service);
            Log.e("cdl", "=====连接远程服务成功");
            updateGuardianView();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            aidl = null;
            Log.e("cdl", "=====断开远程服务");
        }
    };


    public abstract void updateGuardianView();

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        AppInfo.startCheckTaskTag = false;
        bindService();
    }

    public void bindService() {
        boolean isApkInstall = APKUtil.ApkState(GuardianBaseActivity.this, AppInfo.GUARDIAN_PACKAGE_NAME);
        if (!isApkInstall) {
            return;
        }
        try {
            String packageName = AppInfo.GUARDIAN_PACKAGE_NAME;
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(packageName, "com.guardian.service.GuardianService"));
            bindService(intent, conn, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置开机自启
     *
     * @param powerOpen
     */
    public void setOpenPower(boolean powerOpen) {
        SharedPerManager.setOpenPower(powerOpen);
        if (aidl == null) {
            showToast(getLanguageFromResurce(R.string.guardian_line_error));
            return;
        }
        try {
            aidl.setOpenPower(powerOpen);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取开机自启
     *
     * @return
     */
    public boolean getOpenPower() {
        boolean isOpen = false;
        if (aidl == null) {
            return SharedPerManager.getOpenPower();
        }
        try {
            isOpen = aidl.getOpenPower();
            showToast(getLanguageFromResurce(R.string.set_success));
        } catch (Exception e) {
            e.printStackTrace();
        }
        SharedPerManager.setOpenPower(isOpen);
        return isOpen;
    }

    /**
     * 修改守护进程开关状态
     *
     * @param b
     */
    public void setGuardianStatues(boolean b) {
        if (aidl == null) {
            showToast(getLanguageFromResurce(R.string.guardian_line_error));
            return;
        }
        try {
            aidl.setOpenGuardianStatues(b);
            showToast(getLanguageFromResurce(R.string.set_success));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 判断软件有没有安装
     *
     * @return
     */
    public boolean isInstallGuardian() {
        String packName = "com.guardian";
        boolean isInstall = APKUtil.ApkState(GuardianBaseActivity.this, packName);
        return isInstall;
    }

    /**
     * 获取守护进程得版本号
     *
     * @return
     */
    public String getGuardianAppCode() {
        String packName = "com.guardian";
        boolean isInstall = isInstallGuardian();
        if (isInstall) {
            int guardianCode = APKUtil.getOtherAppVersion(GuardianBaseActivity.this, packName);
            return packName + "_V" + guardianCode;
        }
        return getLanguageFromResurce(R.string.guardian_no_install);
    }

    /**
     * 获取守护进程得开关状态
     *
     * @return
     */
    public boolean getGuardianOpenStatues() {
        boolean isOpen = false;
        if (aidl == null) {
            showToast(getLanguageFromResurce(R.string.guardian_line_error));
            return isOpen;
        }
        try {
            isOpen = aidl.getOpenGuardianStatues();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isOpen;
    }

    /**
     * 获取检查得时间
     * 换算成秒
     *
     * @return
     */
    public int getCheckTime() {
        int time = 0;
        if (aidl == null) {
            showToast(getLanguageFromResurce(R.string.guardian_line_error));
            return time;
        }
        try {
            time = aidl.getCheckTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time / 1000;
    }

    /***
     * 修改AIDL的时间
     * 保存成毫秒
     */
    public void modifyAidlTime(int time) {
        GuardianUtil.setGuardianProjectTime(GuardianBaseActivity.this, time + "");
//        if (aidl == null) {
//            MyToastView.getInstance().Toast(GuardianBaseActivity.this, getLanguageFromResurce(R.string.guardian_line_error));
//            return;
//        }
//        try {
//            aidl.setCheckTime(time * 1000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void showToast(String s) {
        MyToastView.getInstance().Toast(GuardianBaseActivity.this, s);
    }

    OridinryDialog oridinryDialog;

    public void unInstallGuardianApk() {
        boolean isInstall = isInstallGuardian();
        if (!isInstall) {
            MyToastView.getInstance().Toast(GuardianBaseActivity.this, getLanguageFromResurce(R.string.guardian_no_install));
            return;
        }
        if (oridinryDialog == null) {
            oridinryDialog = new OridinryDialog(GuardianBaseActivity.this);
        }
        oridinryDialog.show(getString(R.string.uninstall), getString(R.string.if_uninstall_guardian));
        oridinryDialog.setOnDialogClickListener(new OridinryDialogClick() {
            @Override
            public void sure() {
                boolean isDel = RootCmd.delGuardianApk();
                MyToastView.getInstance().Toast(GuardianBaseActivity.this, isDel ? "卸载成功" : "卸载失败");
                if (isDel) {
                    SystemManagerInstance.getInstance(GuardianBaseActivity.this).rebootDev();
                }
            }

            @Override
            public void noSure() {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (aidl == null) {
                return;
            }
            unbindService(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
