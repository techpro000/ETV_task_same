package com.etv.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.etv.config.AppConfig;
import com.etv.config.AppInfo;
import com.etv.entity.AppInfomation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PackgeUtil {

    /***
     * 获取手机中所有已安装的应用，并判断是否系统应用
     *
     * @param context
     * 0:全部
     * 1：系统
     * 2：安装
     * @return 非系统应用
     *
     */
    public static void getPackage(Context context, PackageListener listener) {
        try {
            ArrayList<AppInfomation> appList = new ArrayList<AppInfomation>();
            PackageManager packageManager = context.getPackageManager();
            List<PackageInfo> packages = packageManager.getInstalledPackages(0);
            for (int i = 0; i < packages.size(); i++) {
                PackageInfo packageInfo = packages.get(i);
                String name = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString(); //app名字
                String packageName = packageInfo.packageName;                                                //包名
                int versionCode = packageInfo.versionCode;                                                  //版本号
                String versionName = packageInfo.versionName;
                String sourceDir = packageInfo.applicationInfo.sourceDir;
                Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
                File file = new File(sourceDir);
                long fileSize = 12;
                if (file.exists()) {
                    fileSize = file.length();
                }
                AppInfomation appInfomation = new AppInfomation(icon, name, packageName, versionCode, versionName, sourceDir, fileSize);
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    // 非系统应用
                    appInfomation.setAppTag(AppInfomation.APP_TAG_INSTALL);
                } else {
                    appInfomation.setAppTag(AppInfomation.APP_TAG_SYSTEM);
                }
                appList.add(appInfomation);
            }
            listener.getSuccess(appList);
        } catch (Exception e) {
            String desc = e.toString();
            listener.getFail(desc);
        }
    }

    public interface PackageListener {
        void getSuccess(ArrayList<AppInfomation> appList);

        void getFail(String error);
    }

//    /***
//     * 获取手机中所有已安装的应用，并判断是否系统应用
//     *
//     * @param context
//     * @return 非系统应用
//     */
//    public static void getPackage(Context context, PackageListener listener) {
//        try {
//            ArrayList<AppInfomation> appList = new ArrayList<AppInfomation>();
//            List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
//            for (int i = 0; i < packages.size(); i++) {
//                PackageInfo packageInfo = packages.get(i);
//                String name = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString(); //app名字
//                String packageName = packageInfo.packageName;                                                //包名
//                int versionCode = packageInfo.versionCode;                                                  //版本号
//                String versionName = packageInfo.versionName;                                               //版本名字
//                AppInfomation appInfomation = new AppInfomation(name, packageName, versionCode, versionName);
//                appList.add(appInfomation);
//            }
//            listener.getSuccess(appList);
//        } catch (Exception e) {
//            String desc = e.toString();
//            listener.getFail(desc);
//        }
//    }
//
//    public interface PackageListener {
//        void getSuccess(ArrayList<AppInfomation> appList);
//
//        void getFail(String error);
//    }

}
