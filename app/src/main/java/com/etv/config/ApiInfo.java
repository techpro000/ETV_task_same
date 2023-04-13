package com.etv.config;

import com.etv.util.CodeUtil;
import com.etv.util.MyLog;
import com.etv.util.SharedPerManager;
import com.etv.util.SharedPerUtil;
import com.etv.util.TextUtils;
import com.etv.util.aes.AESTool;

public class ApiInfo {

    public static final String IP_DEFAULT_URL_WEBSOCKET = "etv.esoncloud.com";
    public static final String IP_DEFAULT_URL_SOCKET = "etv.ids.esoncloud.com";

    public static final String SOCKET_DEFAULR_IP_ADDRESS = "120.25.204.220";
    public static final int SOCKET_DEFAULR_PORT = 9222;

    public static String getAESCodeKey() {
        String key = CodeUtil.getUniquePsuedoID();
        key = key.substring(0, 16);
        return key;
    }

    /***
     * 服务器发过来得335
     * 这里需要回435
     * @return
     */
    public static final byte[] getHearBacktMessage() {
        byte[] macInfo = new byte[]{4, 35, 104};
        return AESTool.EncryptString(macInfo);
    }

    /***
     * 获取心跳指令
     * @return
     */
    public static final byte[] getHeartMessage() {
        byte[] macInfo = new byte[]{3, 35, 104};
        return AESTool.EncryptString(macInfo);
    }

    /***
     * 设备收到指令，反馈给服务器
     * @return
     */
    public static final byte[] getOrderMessageBack(String id) {
        MyLog.netty("===反馈order===" + id);
//        byte[] orderMessageBack = new byte[]{6, 35, 104};
        String mac = "#" + id;
        byte[] orderMessageBack = TextUtils.getOrderBackMessage(mac);
        return AESTool.EncryptString(orderMessageBack);
    }

    public static String getWebIpHost() {
        return SharedPerUtil.getWebHostIpAddress();
    }

    public static String getWebPort() {
        return SharedPerUtil.getWebHostPort();
    }

    /**
     * 获取文件下载地址
     * 如果使用域名得话，直接用域名进行拼接
     * 如果其他客户得 直接使用 ip + port 端口号
     *
     * @return
     */
    public static String getFileDownUrl() {
        if (SharedPerUtil.SOCKEY_TYPE() == AppConfig.SOCKEY_TYPE_SOCKET) {
            //SOCKET 直接通过配置
            return SharedPerUtil.getSocketDownPath();
        }
        String ipaddress = getWebIpHost();
        String port = getWebPort();
        String backUrl = "";
        if (ipaddress.contains(IP_DEFAULT_URL_WEBSOCKET)) {
            backUrl = "http://" + IP_DEFAULT_URL_WEBSOCKET;
        } else {
            backUrl = "http://" + ipaddress + ":" + port;
        }
        return backUrl;
    }

    /***
     * 返回IP+端口号
     * @return
     */
    public static String getIpHostWebPort() {
        String ipaddress = getWebIpHost();
        String port = getWebPort();
        if (ipaddress.contains(IP_DEFAULT_URL_SOCKET)) {
            return "http://" + ipaddress;
        }
        if (ipaddress.contains(IP_DEFAULT_URL_WEBSOCKET)) {
            return "http://" + ipaddress;
        }
        String backUrl = "http://" + ipaddress + ":" + port;
        return backUrl;
    }

    //获取基本请求URL
    public static String WEB_BASE_URL() {
        return getIpHostWebPort() + "/etv";
    }

    /***
     * 获取天气的接口
     * @return
     */
    public static String GET_WEATHER_URL() {
        return WEB_BASE_URL() + "/webservice/selectWeatherByCityName?cityName=";
//        http://119.23.220.53:8899/etv/webservice/selectWeatherByCityName?cityName=深圳
    }

    /****
     * 上传一键报警视频接口Url
     * @return
     */
    public static String getUpdateWearVideoUrl() {
        return WEB_BASE_URL() + "/webservice/uploadWarningVideo";
    }

    /****
     * 接口统一
     * 1：内存限制台数接口
     * 2：背景图 logo 请求下载接口
     * 3：定时开关机
     * 4：字体接口
     * 5：系统时间
     * 6：心跳服务器配置
     * 7：设备自定义设置
     * 8：文件服务器地址
     * @return
     */
    public static String getDevSettingAllInfo() {
        //userName  clNo
        return WEB_BASE_URL() + "/webservice/getClientBaseInfo";
    }


    /***
     * 提交设备信息给服务器
     * @return
     */
    public static String getUpdateDevToAuthorServer() {
        return "http://47.107.50.81:8080/etvauth/authapi/putSellBoardInfo";
    }

    /**
     * 用户手动删除
     *
     * @return
     */
    public static String DEL_TASK_BY_USER() {
        return WEB_BASE_URL() + "/webservice/deleteTaskByClientNo";
    }

    /**
     * 检查下载资格
     *
     * @return
     */
    public static String CHECK_DOWN_LIMIT() {
        return WEB_BASE_URL() + "/webservice/getDownLoadLimit";
    }

    //设备注册接口
    public static String REGISTER_WEB_TO_DEV() {
        return WEB_BASE_URL() + "/webservice/insertClient";
    }

    //查询定时开关机服务
    public static String POWERONOFF_QUERY() {
        return WEB_BASE_URL() + "/webservice/selectClientByClNo";
    }

    //监控图片上传
    public static String MONITOR_IMAGE_UPDATE() {
        return WEB_BASE_URL() + "/webservice/insertEqMonitorImg";
//        http://192.168.1.20:8899/etv/webservice/insertEqMonitorImg?clientNo=
    }

    //检测升级的功能
    public static String UPDATE_APP_SYSTEM() {
        return WEB_BASE_URL() + "/webservice/selectAllUpgradeFile";
    }

    //修改升级进度的功能
    //修改为100 %
    public static String UPDATE_APP_SYSTEM_TO_OVER() {
        return WEB_BASE_URL() + "/webservice/updateClientUpgradeFileProgress";
    }

    /***
     * 清除升级信息
     * 后边下载100 % 之后服务器自动销毁，这里不自动删除了
     * @return
     */
//    public static String DEL_APP_SYSTEM_INFO() {
//        return WEB_BASE_URL() + "/webservice/deleteClientUpgradeFileByClNo";
//    }

    /***
     * 下载状态更新到服务器
     * @return
     */
    public static String UPDATE_DOWN_PROGRESS() {
        return WEB_BASE_URL() + "/webservice/updateEqTaskProgress";
    }

    //修改昵称
    public static String MODIFY_NICK_NAME() {
        return WEB_BASE_URL() + "/webservice/updateClientName";
    }


    /**
     * 获取同步任务得得设备
     *
     * @return
     */
    public static String SAME_TASK_URL() {
        return WEB_BASE_URL() + "/webservice/selectSynchTaskAllClient";
    }

    //查询设备信息
    public static String QUERY_DEV_INFO() {
        return WEB_BASE_URL() + "/webservice/selectClient";
    }

    //提交素材统计信息
    public static String ADD_FILE_NUMBERS_TOTAL() {
        return WEB_BASE_URL() + "/webservice/insertStats";
    }

    //提交设备信息
    public static String UPDATE_DEV_INFO() {
        return WEB_BASE_URL() + "/webservice/updateClientInfo";
    }

    //新增移动流量数据
    public static String UPDATE_FLOW_USAGE() {
        return WEB_BASE_URL() + "/webservice/insertMobileData";
    }

    //获取任务接口
    public static String getClientTaskUrl() {
//        http://119.23.220.53:8899/etv/webservice/selectClientEMTask?clNo=301F9A8073AF18937FE2A447
        return WEB_BASE_URL() + "/webservice/selectClientEMTask";
    }

    //获取SD卡内存的阈值Url
    public static String getSdManagerCheckUrl() {
        return WEB_BASE_URL() + "/webservice/selectRamInfoAll";
    }

    //获取背景图相关得信息
    public static String getBggImageStatuesUrl() {
//        http://119.23.220.53:8899/etv/webservice/selectClientDiySet?
        return WEB_BASE_URL() + "/webservice/selectClientDiySet";
    }

    /***
     * 上传日志
     * @return
     */
    public static String getUpdateDevInfo() {
        return WEB_BASE_URL() + "/webservice/uploadEquipmentLog";
    }

    /**
     * 修改字幕内容的属性
     *
     * @return
     */
    public static String updateTxtInfo() {
        return WEB_BASE_URL() + "/webservice/updateSubtitle";
    }

    /**
     * 提交img升级进度
     *
     * @return
     */
    public static String getUpdateApkProgress() {
        return WEB_BASE_URL() + "/webservice/updateUpgradeFileProgress";
    }

    public static String getScreenLayoutUrl() {
        return WEB_BASE_URL() + "/webservice/getSameScreenLayout";
    }

    /***
     * 获取Socket长连接的IP地址
     * @return
     *   //"ws://192.168.1.97:8080/etv/websocket/socketServer.json?" + uuid;
     */
    public static String getSocketLineAddress() {
        String devId = CodeUtil.getUniquePsuedoID();
        String address = "ws://" + getWebIpHost() + ":" + getWebPort() + "/etv/webservice/linksocket?" + devId;
        return address;
    }


}
