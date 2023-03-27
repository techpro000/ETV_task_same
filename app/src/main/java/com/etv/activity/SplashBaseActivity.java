package com.etv.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.etv.config.AppConfig;
import com.etv.config.AppInfo;
import com.etv.setting.GuardianBaseActivity;
import com.etv.util.APKUtil;
import com.etv.util.FileUtil;
import com.etv.util.MyLog;
import com.etv.util.ProjectorUtil;
import com.etv.util.RootCmd;
import com.etv.util.SharedPerManager;
import com.etv.util.SimpleDateUtil;
import com.etv.util.guardian.GuardianUtil;
import com.etv.util.system.CpuModel;
import com.etv.util.system.LeaderBarUtil;
import com.etv.util.system.SystemManagerInstance;
import com.ys.model.config.DialogConfig;

import java.io.File;

public class SplashBaseActivity extends GuardianBaseActivity {

    @Override
    public void updateGuardianView() {
        //这里主要是用户用户手动点击的APK，判断缓存的状态
        boolean isOpenStatues = getGuardianOpenStatues();
        SharedPerManager.setGuardianStatues(isOpenStatues);
    }

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        initApp();
        closeBluth();
    }

    /**
     * 解决设备WIFI信号差的问题
     */
    private void closeBluth() {
        try {
            BluetoothAdapter mBluetooth = BluetoothAdapter.getDefaultAdapter();
            if (mBluetooth != null && mBluetooth.isEnabled()) {
                mBluetooth.disable();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initApp() {
        delCrashLogFile();
        installGuardianApp();
        setProjectorSavePath();
        String PackageName = APKUtil.getPackageName(SplashBaseActivity.this);
        SharedPerManager.setPackageName(PackageName, "程序启动，保存包名");
    }

    @Override
    protected void onResume() {
        super.onResume();
        getScreenSize();
        //隐藏虚拟按键，并且全屏
        LeaderBarUtil.hiddleLeaderBar(SplashBaseActivity.this);
        //清理U盘升级缓存得APK文件，防止缓存沾满得问题
        RootCmd.clearApkCache();
        SystemManagerInstance.getInstance(SplashBaseActivity.this).switchAutoTime(true, "ETV 默认打开自动同步系统时间");
    }

    /***
     * 删除多余的日志
     */
    private void delCrashLogFile() {
//        系统时间不对的话，不往下执行
        long currentTime = SimpleDateUtil.formatBig(System.currentTimeMillis());
        if (currentTime < AppConfig.TIME_CHECK_POWER_REDUCE) {
            return;
        }
        try {
            String url = AppInfo.BASE_CRASH_LOG();
            File fileDir = new File(url);
            if (!fileDir.exists()) {
                MyLog.cdl("===============crashlog=======文件不存在====");
                return;
            }
            File[] fileList = fileDir.listFiles();
            if (fileList == null || fileList.length < 2) {
                return;
            }
//            String dataInfo = SimpleDateUtil.getCurrentDateLong() + "";
            long lasttWOtIME = 60 * 1000 * 60 * 24 * 5;
            long saveLocalData = SimpleDateUtil.getCurrentDateLongForTime(lasttWOtIME);
            String dataInfo = saveLocalData + "";
            MyLog.cdl("=============crashlog====" + fileList.length);
            for (int i = 0; i < fileList.length; i++) {
                String filePath = fileList[i].getPath();
                if (!filePath.contains(dataInfo)) {
                    FileUtil.deleteDirOrFilePath(fileList[i].getPath(), "===delCrashLogFile==");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 设置节目的保存地址
     */
    private void setProjectorSavePath() {
        ProjectorUtil.setProjectorSavePath(SplashBaseActivity.this, "SplashBaseActivity--程序启动，保存节目路径");
    }

    //安装系统保活进程
    public void installGuardianApp() {
        GuardianUtil installUtil = new GuardianUtil(SplashBaseActivity.this);
        installUtil.startInstallGuardian();
        //修改守护进程的包名
        installUtil.setGuardianPackageName();
    }

    private void getScreenSize() {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(localDisplayMetrics);
        int width = localDisplayMetrics.widthPixels;
        int height = localDisplayMetrics.heightPixels;
        MyLog.cdl("=======屏幕得尺寸==" + width + " / " + height);
        SharedPerManager.setScreenWidth(width);
        SharedPerManager.setScreenHeight(height);
        DialogConfig.SCREEN_WIDTH = width;
        DialogConfig.SCREEN_HEIGHT = height;
    }

}

