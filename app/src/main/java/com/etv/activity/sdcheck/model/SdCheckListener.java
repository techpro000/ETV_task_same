package com.etv.activity.sdcheck.model;

/**
 * Created by jsjm on 2018/11/28.
 */

public interface SdCheckListener {

    void setThreeClose(String desc);

    void addInfoToList(String desc);

    void writeFileProgress(boolean isOver, String filePath, int progress);

    /***
     * 离线任务解析完毕
     * @param filePath
     */
    void zipTaskSuccess(String filePath);
}
