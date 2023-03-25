package com.etv.listener;

public interface TaskChangeListener {

    /***
     * 网络请求成功
     * @param object
     * 返回的实体类
     * 请求的错误信息
     */
    void taskRequestSuccess(Object object, String errorrDesc);


    void requestFailed(String errorrDesc);

}
