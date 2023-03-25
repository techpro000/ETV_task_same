package com.etv.util;


public class SharedPerUtil {


    public static int isSameTaskMainLeader = -1;

    /***
     * 获取同步播放主次关系
     * @return
     */
    public static boolean getSameTaskMainLeader() {
        if (isSameTaskMainLeader > -1) {
            return isSameTaskMainLeader == 0;
        }
        isSameTaskMainLeader = SharedPerManager.getTaskSameMain();
        return isSameTaskMainLeader == 0;
    }


    public static String SOCKET_DOWN_FILE_PATH = "";

    //获取socket 资源下载路径
    public static String getSocketDownPath() {
        if (SOCKET_DOWN_FILE_PATH.length() > 3) {
            return SOCKET_DOWN_FILE_PATH;
        }
        SOCKET_DOWN_FILE_PATH = SharedPerManager.getResourDownPath();
        return SOCKET_DOWN_FILE_PATH;
    }


    public static String WEBHOST_PORT = "";

    //获取网络请求得IPaddress
    public static String getWebHostPort() {
        if (WEBHOST_PORT.length() > 3) {
            return WEBHOST_PORT;
        }
        WEBHOST_PORT = SharedPerManager.getWebPort();
        return WEBHOST_PORT;
    }

    public static String WEBHOST_IP_ADDRESS = "";

    //获取网络请求得IPaddress
    public static String getWebHostIpAddress() {
        if (WEBHOST_IP_ADDRESS.length() > 3) {
            return WEBHOST_IP_ADDRESS;
        }
        WEBHOST_IP_ADDRESS = SharedPerManager.getWebHost();
        return WEBHOST_IP_ADDRESS;
    }


    public static String DEFAULT_SOCKET_IP_ADDRESS = "";

    /***
     * 获取socket ipaddredd
     * @return
     */
    public static String getSocketIpAddress() {
        if (DEFAULT_SOCKET_IP_ADDRESS.length() > 3) {
            return DEFAULT_SOCKET_IP_ADDRESS;
        }
        DEFAULT_SOCKET_IP_ADDRESS = SharedPerManager.getSocketIpAddress();
        return DEFAULT_SOCKET_IP_ADDRESS;
    }

    public static int DEFAULT_SOCKET_PORT = -1;

    public static int getSocketPort() {
        if (DEFAULT_SOCKET_PORT > 0) {
            return DEFAULT_SOCKET_PORT;
        }
        DEFAULT_SOCKET_PORT = SharedPerManager.getSocketPort();
        return DEFAULT_SOCKET_PORT;
    }


    /***
     * 用来缓存当前状态值，
     * 不用重复从sharedPerfance 中取获取消耗内存
     */
    public static int CURRENT_SOCKET_TYPE = -1;

    public static int SOCKEY_TYPE() {
        if (CURRENT_SOCKET_TYPE > -1) {
            return CURRENT_SOCKET_TYPE;
        }
        CURRENT_SOCKET_TYPE = SharedPerManager.getSocketType();
        return CURRENT_SOCKET_TYPE;
    }



}
