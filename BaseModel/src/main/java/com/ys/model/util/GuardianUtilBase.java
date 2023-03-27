package com.ys.model.util;

import android.content.Context;
import android.content.Intent;

import com.ys.model.config.AppInfoBase;

/**
 * 守护进程控制类
 * InstallGuardianUtil
 */
public class GuardianUtilBase {

    Context context;

    public GuardianUtilBase(Context context) {
        this.context = context;
    }

    /***
     *0   慢
     *1   中
     *2   快
     ** 设置IO触发响应速度
     * @param context
     * @param speed
     */
    public static void setIoTriggleSpeed(Context context, int speed) {
        Intent intent = new Intent(AppInfoBase.SET_TRIGGLE_SPEED);
        intent.putExtra(AppInfoBase.SET_TRIGGLE_SPEED, speed);
        context.sendBroadcast(intent);
    }

    /***
     * 设置触发通知反向
     * @param context
     * @param isBack
     */
    public static void modifyIoMessageBack(Context context, boolean isBack) {
        Intent intent = new Intent(AppInfoBase.SET_LISTENER_MESSAGE_BACK);
        intent.putExtra(AppInfoBase.SET_LISTENER_MESSAGE_BACK, isBack);
        context.sendBroadcast(intent);
    }

    /***
     * 修改触发得IO
     * @param context
     * @param ioNum
     *0  IO-1
     *1  IO-2
     *2  IO-3
     *3  IO-4
     */
    public static void modifyIoChooiceNum(Context context, int ioNum) {
        Intent intent = new Intent(AppInfoBase.SET_LISTENER_PERSON_IO_CHOOICE);
        intent.putExtra(AppInfoBase.SET_LISTENER_PERSON_IO_CHOOICE, ioNum);
        context.sendBroadcast(intent);
    }

    /***
     * 修改人体感应开关
     * @param context
     * @param enable
     */
    public static void modifyIoCheckStatues(Context context, boolean enable) {
        Intent intent = new Intent(AppInfoBase.SET_LISTENER_PERSON_OPEN);
        intent.putExtra(AppInfoBase.SET_LISTENER_PERSON_OPEN, enable);
        context.sendBroadcast(intent);
    }

    /**
     * 修改守护进程得状态
     *
     * @param context
     * @param enable
     */
    public static void modifyGuardianStatues(Context context, boolean enable) {
        Intent intent = new Intent(AppInfoBase.CHANGE_GUARDIAN_STATUES);
        intent.putExtra(AppInfoBase.CHANGE_GUARDIAN_STATUES, enable);
        context.sendBroadcast(intent);
    }

    /***
     * 修改守护进程的包名
     */
    public void setGuardianPackageName(String pkgName) {
        BaseLog.guardian("======packageName===" + pkgName);
        Intent intent = new Intent(AppInfoBase.MODIFY_GUARDIAN_PACKAGENAME);
        intent.putExtra("packageName", pkgName);
        context.sendBroadcast(intent);
    }

    /***
     * 设置守护进程得时间
     * @param context
     * @param daemonProcessTime
     * 单位  秒
     */
    public static void setGuardianProjectTime(Context context, String daemonProcessTime) {
        if (context == null) {
            return;
        }
        if (daemonProcessTime == null || daemonProcessTime.contains("null")) {
            return;
        }
        if (daemonProcessTime.length() < 1) {
            return;
        }
        try {
            int guardianTime = Integer.parseInt(daemonProcessTime);
            if (guardianTime < 15) {
                return;
            }
            Intent intent = new Intent(AppInfoBase.MODIFY_GUARDIAN_TIME);
            intent.putExtra(AppInfoBase.MODIFY_GUARDIAN_TIME, guardianTime * 1000);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改守护进程的状态
     * true 打开
     * false 关闭
     *
     * @param b
     */
    public static void setGuardianStaues(Context context, boolean b) {
        Intent intent = new Intent(AppInfoBase.CHANGE_GUARDIAN_STATUES);
        intent.putExtra(AppInfoBase.CHANGE_GUARDIAN_STATUES, b);
        context.sendBroadcast(intent);
    }

    /***
     * 修改开机启动开关
     * @param context
     * @param b
     */
    public static void setGuardianPowerOnStart(Context context, boolean b) {
        Intent intent = new Intent(AppInfoBase.MODIFY_GUARDIAN_POWER_START_ETV);
        intent.putExtra(AppInfoBase.MODIFY_GUARDIAN_POWER_START_ETV, b);
//        intent.setComponent(new ComponentName("com.guardian", "com.guardian.service.GuardianService"));
        context.sendBroadcast(intent);
    }


    /***
     * 告诉守护进程，自己是哪个账户
     * @param context
     * @param companyCode
     */
    public static void sendBroadToGuardianConpany(Context context, int companyCode) {
        try {
            Intent intent = new Intent();
            intent.setAction(AppInfoBase.CHANGE_ORDER_COMPANY);
            intent.putExtra(AppInfoBase.CHANGE_ORDER_COMPANY, companyCode);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
