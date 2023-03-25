package com.etv.setting.update.model;


import android.content.Context;

public interface UpdateMudel {

    void getUpdateInfo(Context context, UpdateInfoListener listener);

    void installApk(Context context, String savePath, UpdateInfoListener listener);

    void installImg(Context context, String savePath, UpdateInfoListener listener);

    /**
     * 保全以防万一下载不到100%。确保最终后台展示的都是100%
     */
    void updateOverProgressToWeb();

}
