package com.etv.util.system;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.etv.util.MyLog;

import java.util.Arrays;

public class PowerOnOffManager {

    private static PowerOnOffManager powerOnOffManager;
    private Context mContext;

    private PowerOnOffManager(Context context) {
        this.mContext = context;
    }

    public static synchronized PowerOnOffManager getInstance(Context context) {
        if (powerOnOffManager == null) {
            powerOnOffManager = new PowerOnOffManager(context);
        }

        return powerOnOffManager;
    }

    public void shutdown() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.shutdown");
        this.mContext.sendBroadcast(intent);
    }

    public void clearPowerOnOffTime() {
        Intent intent = new Intent("android.intent.ClearOnOffTime");
        this.mContext.sendBroadcast(intent);
    }

    public String getPowerOnTime() {
        return Utils.getValueFromProp("persist.sys.powerontime");
    }

    public String getPowerOffTime() {
        return Utils.getValueFromProp("persist.sys.powerofftime");
    }

    public static String getLastestPowerOnTime() {
        return Utils.getValueFromProp("persist.sys.powerontimeper");
    }

    public static String getLastestPowerOffTime() {
        return Utils.getValueFromProp("persist.sys.powerofftimeper");
    }

    public void setPowerOnOff(int[] powerOnTime, int[] powerOffTime) {
        if (mContext == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.setpoweronoff");
        intent.putExtra("timeon", powerOnTime);
        intent.putExtra("timeoff", powerOffTime);
        intent.putExtra("enable", true);
        intent.setPackage("com.adtv");                //添加定时开关机的包名
        mContext.sendBroadcast(intent);
    }

    private void setLangguoCpuPowerOnOff(int[] timeonArray, int[] timeoffArray) {
        try {
            String SET_POWER_ON_OFF = "android.intent.action.setpoweronoff";
            //携带的数据格式为：
            Intent intent = new Intent();
            intent.setAction(SET_POWER_ON_OFF);
            intent.putExtra("timeon", timeonArray);
            intent.putExtra("timeoff", timeoffArray);
            intent.putExtra("enable", true); //使能开关机，true为打开，false为关闭
            mContext.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelPowerOnOff(int[] powerOnTime, int[] powerOffTime) {
        Intent intent = new Intent("android.intent.action.setpoweronoff");
        intent.putExtra("timeon", powerOnTime);
        intent.putExtra("timeoff", powerOffTime);
        intent.putExtra("enable", false);
        this.mContext.sendBroadcast(intent);
        Log.d("PowerOnOffManager", "poweron:" + Arrays.toString(powerOnTime) + "/ poweroff:" + Arrays.toString(powerOffTime));
    }

    public String getVersion() {
        return Utils.getValueFromProp("persist.sys.poweronoffversion");
    }
}
