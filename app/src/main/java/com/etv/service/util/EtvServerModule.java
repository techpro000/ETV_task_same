package com.etv.service.util;

import android.content.Context;

public interface EtvServerModule {

    /***
     * 提交服务器到统计服务器
     */
    void upodateDevInfoToAuthorServer(String clVersion);

    void queryDeviceInfoFromWeb(Context context, TaskServiceView taskServiceView);

    /***
     * 修改信息给服务器
     * @param context
     */
    void updateDevInfoToWeb(Context context, String tag);

    /***
     * 增加统计信息
     */
    void addFileNumTotal(String midId, String addType, int pmTime, int count, String timeUpdate);

    /***
     * 删除任务
     */
    void deleteEquipmentTaskServer(String taskId);

    /***
     * 检测到USB SD卡插入设备
     */
    void SDorUSBcheckIn(Context context, String path);

    /***
     * 提交上传进度给服务器
     * @param taskId
     * @param progress
     * @param titalDoanNum  下载总文件大小
     */
    void updateProgressToWebRegister(String taskId, String titalDoanNum, int progress, int downKb, String type);

    void updateDownApkImgProgress(int percent, int downKb, String fileName);
}
