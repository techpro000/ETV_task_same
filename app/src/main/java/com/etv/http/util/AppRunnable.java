package com.etv.http.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * App启动放在线程中
 */
public class AppRunnable implements Runnable {
    Context context;
    String packageName;

    public AppRunnable(Context context, String packageName) {
        this.context = context;
        this.packageName = packageName;
    }

    @Override
    public void run() {
        try {
            if (packageName == null || packageName.length() < 3) {
                return;
            }
            PackageManager manager = context.getPackageManager();
            Intent intent = manager.getLaunchIntentForPackage(packageName);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}