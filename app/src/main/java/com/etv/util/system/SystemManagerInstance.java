package com.etv.util.system;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.etv.setting.ScreenSettingActivity;
import com.etv.util.MyLog;
import com.etv.util.RootCmd;
import com.etv.util.SharedPerManager;
import com.ys.rkapi.Constant;
import com.ys.rkapi.MyManager;

public class SystemManagerInstance {

    Context context;
    public static SystemManagerInstance Instance;

    public static SystemManagerInstance getInstance(Context context) {
        if (Instance == null) {
            synchronized (SystemManagerInstance.class) {
                Instance = new SystemManagerInstance(context);
            }
        }
        return Instance;
    }

    MyManager myManager;

    public SystemManagerInstance(Context context) {
        this.context = context;
        myManager = MyManager.getInstance(context);
    }


    /***
     * 关机
     */
    public void shoutDownDev() {
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.shutdown");
            if (Integer.parseInt(Build.VERSION.SDK) > 25) {
                intent.setPackage(Constant.YSRECEIVER_PACKAGE_NAME);
            }
            context.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 重启设备
     */
    public void rebootDev() {
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.reboot");
            if (Integer.parseInt(Build.VERSION.SDK) > 25) {
                intent.setPackage(Constant.YSRECEIVER_PACKAGE_NAME);
            }
            context.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /***
     * 背光灯是否亮起来
     * @return
     */
    public boolean getBackLightTtatues(String tag) {
        boolean isBackOn = myManager.isBacklightOn();
        MyLog.cdl("==========判断当前灯光的状态====" + tag + " /isBackOn=" + isBackOn);
        return isBackOn;
    }

    /***
     * 开关背光灯
     * @param isOn
     */
    public void turnBackLightTtatues(boolean isOn) {
        MyLog.sleep("===========turnBackLightTtatues===" + isOn + " / " + CpuModel.getMobileType());
        if (isOn) {
            turnOnBacklight();
        } else {
            turnOffBacklight();
        }
    }

    /***
     * 修改屏幕亮度
     * @param progress
     */
    public void setScreenLightProgress(int progress) {
        try {
            if (progress < 1) {
                progress = 1;
            }
            myManager.changeScreenLight(progress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开背光
     */
    private void turnOnBacklight() {
        try {
            MyLog.sleep("===========00000执行唤醒程序");
            if (CpuModel.getMobileType().startsWith("rk3399")) {
                String lightNum = SharedPerManager.getScreenLightNum();
                MyLog.sleep("===========开启背光===" + lightNum);
                String turnOnCmd = "echo " + lightNum + " > sys/class/leds/lcd-backlight/brightness";
                RootCmd.exusecmd(turnOnCmd, "高通开启主屏背光");
                return;
            }
            MyLog.sleep("===========执行关屏操作==");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                //4.4 M1
                RootCmd.setProperty(RootCmd.BRINT_LIGHT_3128_44, "1");
                return;
            }
            myManager.turnOnBackLight();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭背光
     */
    private void turnOffBacklight() {
        try {
            MyLog.sleep("===========00000执行休眠程序===");
            if (CpuModel.getMobileType().startsWith("rk3399")) {
                String rootCmdLight = "cat sys/class/backlight/backlight/bl_power";
                String lightLightNum = RootCmd.execRootCmdBackInfo(rootCmdLight);
                SharedPerManager.setScreenLightNum(lightLightNum);
                String turnOnCmd = "echo 1 > sys/class/backlight/backlight/bl_power";
                RootCmd.exusecmd(turnOnCmd, "高通关闭主屏背光");
                Log.e("light", "===========关闭背光===" + lightLightNum);
                return;
            }
            MyLog.sleep("==========执行关屏操作===========");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                //4.4 M1
                RootCmd.setProperty(RootCmd.BRINT_LIGHT_3128_44, "0");
                return;
            }
            myManager.turnOffBackLight();
        } catch (Exception e) {
            MyLog.sleep("==========执行关屏操作======Exception=====" + e.toString());
            e.printStackTrace();
        }
    }

    public void switchAutoTime(boolean open, String tag) {
        MyLog.cdl("==========switchAutoTime======Exception=====" + tag);
        try {
            Intent intent = new Intent("com.ys.switch_auto_set_time");
            intent.putExtra("switch_auto_time", open);
            intent.setPackage(Constant.YSRECEIVER_PACKAGE_NAME);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void rotateScreen(Context context, String s) {
        try {
            myManager.rotateScreen(context, s);
        } catch (Exception E) {
            E.printStackTrace();
        }
    }
}
