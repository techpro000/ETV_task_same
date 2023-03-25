package com.etv.entity;

/**
 * Created by jsjm on 2018/11/19.
 */

public class StorageInfo {

    public static final int ACTION_NOTHING = -1;             //无操作
    public static final int ACTION_MODIFY_IP = 0;            //修改IP
    //    public static final int ACTION_REGISTER_DEV_TO_WEB = 1;  //注册设备
    public static final int ACTION_TASK_DISONLINE = 1;       //网络导出任务
    public static final int ACTION_INSTALL_ETV_APK = 2;      //安装APK
    public static final int ACTION_TASK_SINGLE = 3;          //单机任务
    public static final int ACTION_VOICE_MEDIA = 4;          //触沃-替换语音文件

    public static final int TYPE_SD = 0;
    public static final int TYPE_USB = 1;


    int action;   //用来识别，文件的意图
    String path;
    int sdType;   //用来区分当前是SD卡还是U盘

    public StorageInfo(String path, int action) {
        this.path = path;
        this.action = action;
    }

    public StorageInfo(String path, int action, int sdType) {
        this.path = path;
        this.action = action;
        this.sdType = sdType;
    }


    public int getSdType() {
        return sdType;
    }

    public void setSdType(int sdType) {
        this.sdType = sdType;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "StorageInfo{" +
                ", path='" + path + '\'' +
                '}';
    }
}
