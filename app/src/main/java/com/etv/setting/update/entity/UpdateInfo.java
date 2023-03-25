package com.etv.setting.update.entity;


import android.os.Parcel;
import android.os.Parcelable;

public class UpdateInfo implements Parcelable {

    String ufOgname;      //文件名字
    String ufPackageName; //包名
    int ufVersion;        //版本
    String ufSysVerson;   //版本号
    long ufSize;          //下载文件大小
    String ufSaveUrl;     //下载屏接地址
    int ufState;          //1 表示打开下载，  -1表示不可下载
    String serverUrl;     //服务器地址，用来屏接下载地址

    public UpdateInfo(String ufOgname, String ufPackageName, int ufVersion, String ufSysVerson, long ufSize, String ufSaveUrl, int ufState, String serverUrl) {
        this.ufOgname = ufOgname;
        this.ufPackageName = ufPackageName;
        this.ufVersion = ufVersion;
        this.ufSysVerson = ufSysVerson;
        this.ufSize = ufSize;
        this.ufSaveUrl = ufSaveUrl;
        this.ufState = ufState;
        this.serverUrl = serverUrl;
    }

    public String getUfOgname() {
        return ufOgname;
    }

    public void setUfOgname(String ufOgname) {
        this.ufOgname = ufOgname;
    }

    public String getUfPackageName() {
        return ufPackageName;
    }

    public void setUfPackageName(String ufPackageName) {
        this.ufPackageName = ufPackageName;
    }

    public int getUfVersion() {
        return ufVersion;
    }

    public void setUfVersion(int ufVersion) {
        this.ufVersion = ufVersion;
    }

    public String getUfSysVerson() {
        return ufSysVerson;
    }

    public void setUfSysVerson(String ufSysVerson) {
        this.ufSysVerson = ufSysVerson;
    }

    public long getUfSize() {
        return ufSize;
    }

    public void setUfSize(long ufSize) {
        this.ufSize = ufSize;
    }

    public String getUfSaveUrl() {
        return ufSaveUrl;
    }

    public void setUfSaveUrl(String ufSaveUrl) {
        this.ufSaveUrl = ufSaveUrl;
    }

    public int getUfState() {
        return ufState;
    }

    public void setUfState(int ufState) {
        this.ufState = ufState;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    @Override
    public String toString() {
        return "UpdateInfo{" +
                "ufOgname='" + ufOgname + '\'' +
                ", ufPackageName='" + ufPackageName + '\'' +
                ", ufVersion=" + ufVersion +
                ", ufSysVerson='" + ufSysVerson + '\'' +
                ", ufSize=" + ufSize +
                ", ufSaveUrl='" + ufSaveUrl + '\'' +
                ", ufState=" + ufState +
                ", serverUrl='" + serverUrl + '\'' +
                '}';
    }

    protected UpdateInfo(Parcel in) {
        ufOgname = in.readString();
        ufPackageName = in.readString();
        ufVersion = in.readInt();
        ufSysVerson = in.readString();
        ufSize = in.readLong();
        ufSaveUrl = in.readString();
        ufState = in.readInt();
        serverUrl = in.readString();
    }

    public static final Creator<UpdateInfo> CREATOR = new Creator<UpdateInfo>() {
        @Override
        public UpdateInfo createFromParcel(Parcel in) {
            return new UpdateInfo(in);
        }

        @Override
        public UpdateInfo[] newArray(int size) {
            return new UpdateInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(ufPackageName);
        dest.writeString(ufPackageName);
        dest.writeInt(ufVersion);
        dest.writeString(ufSysVerson);
        dest.writeLong(ufSize);
        dest.writeString(ufSaveUrl);
        dest.writeInt(ufState);
        dest.writeString(serverUrl);
    }
}