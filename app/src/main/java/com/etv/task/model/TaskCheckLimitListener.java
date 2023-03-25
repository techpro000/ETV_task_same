package com.etv.task.model;


/***
 * 用来监听任务请求状态
 */
public interface TaskCheckLimitListener {

    /**
     * 检测自己有没有下载资格
     *
     * @param isTrue      请求是否成功
     * @param currDownNum 当前正在下载的台数
     * @param desc        错误描述信息
     */
    void checkDownLimitSpeed(boolean isTrue, int currDownNum, String desc);
}
