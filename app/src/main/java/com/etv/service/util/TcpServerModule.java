package com.etv.service.util;


import android.content.Context;

import com.etv.activity.model.RegisterDevListener;
import com.etv.listener.TaskChangeListener;
import com.etv.service.listener.SysSettingAllInfoListener;
import com.etv.service.listener.TcpServerListener;

public interface TcpServerModule {

    /***
     * 索取所有得设置信息
     */
    void getSystemSettingAllInfo(SysSettingAllInfoListener listener);

    /**
     * 获取背景图片相关得信息
     */
    void getBggImageInfoStatues(TcpServiceView tcpServiceView);

    /****
     * 提交监控图片
     * @param context
     */
    void monitorUpdateImage(Context context, String filePath);

    /***
     * 获取定时开关机信息
     * @param listener
     */
    void getPowerOnOffTask(final TaskChangeListener listener);

    void getSystemSettingInfoTCP();

    /***
     * 上传工作日志
     */
    void updateWorkInfoTxt();

    void registerDevToWeb(Context context, String userName, final RegisterDevListener listener);

    void getDevHartInfo(Context context, TcpServerListener listener);

    /**
     * 用来区分指令测类型
     *
     * @param tag username   修改用户名字
     *            location   修改定位信息
     */
    void updateDevInformation(Context context, String tag);

    void getProjectFontInfoFromWeb(String id);
}
