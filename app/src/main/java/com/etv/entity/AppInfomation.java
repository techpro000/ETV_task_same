package com.etv.entity;


import android.graphics.drawable.Drawable;

public class AppInfomation {

    public static final int APP_TAG_ALL = 0;
    public static final int APP_TAG_SYSTEM = 1;
    public static final int APP_TAG_INSTALL = 2;

    private Drawable icon;
    String name;
    String packageName;
    int versionCode;
    String versionName;
    String installPath;
    long apkSize;
    int appTag;

    public AppInfomation(String name, String packageName, int versionCode, String versionName) {
        this.name = name;
        this.packageName = packageName;
        this.versionCode = versionCode;
        this.versionName = versionName;
    }

    public AppInfomation(Drawable icon, String name, String packageName, int versionCode, String versionName, String installPath, long apkSize) {
        this.name = name;
        this.icon = icon;
        this.packageName = packageName;
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.installPath = installPath;
        this.apkSize = apkSize;
    }

    public int getAppTag() {
        return appTag;
    }

    public void setAppTag(int appTag) {
        this.appTag = appTag;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public long getApkSize() {
        return apkSize;
    }

    public void setApkSize(long apkSize) {
        this.apkSize = apkSize;
    }

    public String getInstallPath() {
        return installPath;
    }

    public void setInstallPath(String installPath) {
        this.installPath = installPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    @Override
    public String toString() {
        return "AppInfomation{" +
                "name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                ", versionCode=" + versionCode +
                ", versionName='" + versionName + '\'' +
                '}';
    }
}
