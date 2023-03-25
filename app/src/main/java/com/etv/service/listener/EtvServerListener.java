package com.etv.service.listener;


public interface EtvServerListener {


    /**
     * 8
     * 判断当前是不是关机时间
     * true 开机时间
     * false 关机时间
     */
    void jujleCurrentIsShutDownTime(boolean isShutDown);

}
