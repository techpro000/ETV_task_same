package com.etv.util.net;

import android.content.pm.ApplicationInfo;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

/**
 * 记录应用程序流量的model
 * Created by zhangshuo on 2016/7/7.
 */
public class AppTrafficModel extends LitePalSupport implements Serializable {

    private long download;
    private long upload;

    public AppTrafficModel() {

    }

    public AppTrafficModel(long download, long upload) {
        this.download = download;
        this.upload = upload;
    }

    public long getDownload() {
        return download;
    }

    public void setDownload(long download) {
        this.download = download;
    }

    public long getUpload() {
        return upload;
    }

    public void setUpload(long upload) {
        this.upload = upload;
    }

    private ApplicationInfo appInfo;

    public ApplicationInfo getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(ApplicationInfo appInfo) {
        this.appInfo = appInfo;
    }


    @Override
    public String toString() {
        return "AppTrafficModel{" +
                "download=" + download +
                '}';
    }
}
