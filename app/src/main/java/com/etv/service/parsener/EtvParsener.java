package com.etv.service.parsener;

import android.content.Context;
import android.os.Handler;

import com.etv.service.util.EtvServerModule;
import com.etv.service.util.EtvServerModuleImpl;
import com.etv.util.MyLog;

public class EtvParsener {

    Context context;
    private Handler handler;

    public EtvParsener(Context context) {
        this.context = context;
        initOther();
    }

    /***
     * 处理红外感应，GPIO触发，
     * 人来了得状况
     */
    public void dealRedGpioInfoPeronComeIn() {

    }

    public void SDorUSBcheckIn(Context context, String path) {
        initOther();
        etvServerModule.SDorUSBcheckIn(context, path);
    }

    /***
     * 删除任务
     * 提交给服务器，
     * 删除本地文件
     *删除本地数据库
     */
    public void deleteEquipmentTaskById(String tag, String taskId) {
        initOther();
        MyLog.del("=======帅选并且删除任务标记==tag");
        try {
            if (etvServerModule != null) {
                etvServerModule.deleteEquipmentTaskServer(taskId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 上传进度给服务器
     *
     * @param taskId
     * @param progress int taskId, int progress, int downKb
     */
    public void updateProgressToWebRegister(String tag, String taskId, String totalNum, int progress, int downKb, String type) {
//        MyLog.cdl("===下载进度标记==" + tag + " /taskId=" + taskId + " /totalNum=" + totalNum + " /progress= " + progress + " /downKb=" + downKb);
        initOther();
        etvServerModule.updateProgressToWebRegister(taskId, totalNum, progress, downKb, type);
    }


    /***
     * 提交设备信息到统计服务器
     */
    public void updateDevInfoToAuthorServer(String version) {
        initOther();
        etvServerModule.upodateDevInfoToAuthorServer(version);
    }

    public void updateDevStatuesToWeb(Context context) {
        initOther();
        etvServerModule.updateDevInfoToWeb(context, "EtvService 界面调用");
    }

    public void updateDownApkImgProgress(int percent, int downKb, String fileName, String tag) {
        MyLog.cdl("===下载进度标记（升级任务）==" + tag);
        initOther();
        etvServerModule.updateDownApkImgProgress(percent, downKb, fileName);
    }


    EtvServerModule etvServerModule;

    private void initOther() {
        if (handler == null) {
            handler = new Handler();
        }
        if (etvServerModule == null) {
            etvServerModule = new EtvServerModuleImpl();
        }
    }

}
