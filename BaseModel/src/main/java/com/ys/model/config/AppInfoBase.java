package com.ys.model.config;

public class AppInfoBase {

    public static final String MODIFY_GUARDIAN_PACKAGENAME = "MODIFY_GUARDIAN_PACKAGENAME";  //修改守护进程包名
    public static final String RED_LINE_LISTENER_IN = "RED_LINE_LISTENER_IN";  //红外感应有东西
    public static final String RED_LINE_LISTENER_OUT = "RED_LINE_LISTENER_OUT";  //红外感应没有东西
    public static final String SET_LISTENER_PERSON_OPEN = "SET_LISTENER_PERSON_OPEN";  //设置触发感应
    public static final String SET_LISTENER_PERSON_IO_CHOOICE = "SET_LISTENER_PERSON_IO_CHOOICE";  //设置触发IO
    public static final String SET_LISTENER_MESSAGE_BACK = "SET_LISTENER_MESSAGE_BACK";  //设置触发通知反向
    public static final String SET_TRIGGLE_SPEED = "SET_TRIGGLE_SPEED";  //设置IO响应速度
    public static final String START_PROJECTOR_GUARDIAN_TIMER = "START_PROJECTOR_GUARDIAN_TIMER";  //通知守护进程，从现在开始计时
    public static final String RECEIVE_BROAD_CAST_LIVE = "RECEIVE_BROAD_CAST_LIVE"; //守护进程发通知过来
    public static final String SEND_BROAD_CAST_LIVE = "SEND_BROAD_CAST_LIVE"; //告诉守护进程自己还活着
    public static final String CHANGE_GUARDIAN_STATUES = "CHANGE_GUARDIAN_STATUES"; //ETV发过来的广播修改守护进程的状态
    public static final String MODIFY_GUARDIAN_TIME = "MODIFY_GUARDIAN_TIME"; //ETV发过来的广播修改守护进程的守护时间
    public static final String MODIFY_GUARDIAN_POWER_START_ETV = "MODIFY_GUARDIAN_POWER_START_ETV";  //修改开机启动ETV

    public static final String CHANGE_ORDER_COMPANY = "CHANGE_ORDER_COMPANY";    //修改公司用户的广播

    public static final String CAPTURE_IMAGE_RECEIVE = "CAPTURE_IMAGE_RECEIVE";  //发送截图的广播
    public static final String SEND_IMAGE_CAPTURE_SUCCESS = "SEND_IMAGE_CAPTURE_SUCCESS";  //截图成功，返回的广播

}
