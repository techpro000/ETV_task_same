package com.etv.service.listener;


/**
 * TCP网络请求监听回调
 */
public interface TcpServerListener {

    /***
     * 请求的状态
     * @param isSuccess
     * @param desc
     * 如果是true 返回json
     * false     返回错误信息
     */
    void getDevHartInfoStatues(boolean isSuccess, String desc);
}
