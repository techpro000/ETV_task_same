package com.ys.model.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ActivityCollector {
    public static final String TAG = "ActivityCollector";
    private static List<Activity> activities = new ArrayList();

    public static void addActivity(Activity paramActivity) {
        activities.add(paramActivity);
        BaseLog.cdl("activity =====addActivity===== " + paramActivity.getClass(), true);
    }

    public static void removeActivity(Activity paramActivity) {
        activities.remove(paramActivity);
        BaseLog.cdl("activity =====removeActivity===== " + paramActivity.getClass(), true);
    }

    public static void finishAll() {
        Iterator localIterator = activities.iterator();
        while (localIterator.hasNext()) {
            Activity localActivity = (Activity) localIterator.next();
            if (!localActivity.isFinishing()) {
                localActivity.finish();
            }
        }
        Log.i("ActivityCollector", "移除所有的activity");
    }


    /***
     * 判断当前界面是否在前台
     * @param context
     * @param listClassName
     * @return
     */
    public static boolean isForegroundList(Context context, List<String> listClassName) {
        try {
            List<Boolean> listFirst = new ArrayList<Boolean>();
            boolean isForrground = false;
            if (listClassName.size() < 1) {
                return isForrground;
            }
            for (int i = 0; i < listClassName.size(); i++) {
                String className = listClassName.get(i);
                listFirst.add(isForeground(context, className));
            }
            for (int k = 0; k < listFirst.size(); k++) {
                boolean isForst = listFirst.get(k);
                if (isForst) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            BaseLog.cdl("====界面在前台异常==" + e.toString());
        }
        return false;
    }

    /**
     * 判断某个界面是否在前台
     *
     * @param context   Context
     * @param className 界面的类名
     * @return 是否在前台显示
     */
    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className))
            return false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            Log.i("activityName---", cpn.getClassName());
            if (className.equals(cpn.getClassName()))
                return true;
        }
        return false;
    }


}
