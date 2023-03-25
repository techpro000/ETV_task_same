package com.etv.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.etv.activity.StartActivity;
import com.etv.config.AppConfig;
import com.etv.util.MyLog;
import com.etv.util.SharedPerManager;

public class BootBroadcastReceiver extends BroadcastReceiver {

    private static final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";

    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        Log.i("BootBroadcastReceiver", "=ETV开机广播=============" + action);
        MyLog.cdl("开机广播====", true);
        if (action.equals(ACTION_BOOT)) {
            SharedPerManager.setLastDownTraff(0);
            SharedPerManager.setLastUploadTraff(0);
            openApp(context);
        }
    }

    /**
     * 1：视威得版本是开开及广播起来得，因为他没有守护进程
     * 2：其他得版本是通过守护进程拉起来得，两个不一样这里要区别对待
     *
     * @param context
     */
    private void openApp(Context context) {
        boolean isOpenPower = SharedPerManager.getOpenPower();
        if (!isOpenPower) {
            return;
        }
    }

    public void startAppFromReceive(Context context) {
        try {
            Intent intent1 = new Intent(context, StartActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}