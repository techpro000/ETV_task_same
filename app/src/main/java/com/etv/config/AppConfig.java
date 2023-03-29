package com.etv.config;

import com.etv.util.SharedPerManager;
import com.etv.util.SharedPerUtil;

public class AppConfig {

    public static final int CONSUMER_DEFAULT = 0;
    public static final int CONSUMER_CNIN_MONEY = 1; //长江证券项目
    public static final int CONSUMER_SNGHANG_VIDEO = 2; //圣航视频同步及黑屏问题讨论
    public static final int CONSUMER_TYPE = CONSUMER_DEFAULT;


    //用来检测当前开机时间是否是正常得时间
    public static long TIME_CHECK_POWER_REDUCE = 20211230101010L;

    // 软件是否打印日志
    public static final boolean IF_PRINT_LOG = true;

    /***
     * Socket 连接方式
     */
    public static final int SOCKEY_TYPE_WEBSOCKET = 0;   //webSocket
    public static final int SOCKEY_TYPE_SOCKET = 1;      //socket

    /***
     * 查看设备是否连接服务器
     */
    public static boolean isOnline = false;

    /***
     * 地图定位的间隔时间
     */
    public static final int BAIDU_MAP_LOCATION = 1000 * 10;

    /***
     * 回到主界面，自动检查播放
     */
    public static int CHECK_TIMER_TO_PLAY() {
        String exitPassword = SharedPerManager.getExitpassword();
        if (exitPassword == null || exitPassword.length() < 1) {
            return 5;
        }
        return 30;
    }

    //终端发送一个一个空消息。保持服务器在线状态
    public static final int MESSAGE_AUTO_SEND_SOCKET = 3 * 1000;
    //无缝切换的时间 默认 1500
    public static final int Seamless_Switching_Time = 300;

    /***
     *设备与服务器之间的心跳
     */
    public static int TIME_TO_HART_TO_WEB() {
        if (SharedPerUtil.SOCKEY_TYPE() == SOCKEY_TYPE_SOCKET) {
            return 15 * 1000;
        }
        return 25 * 1000;
    }

    //心跳超时
    public static int TIME_TO_HART_MORE_TIME() {
        return 5 * 1000;
    }

}
