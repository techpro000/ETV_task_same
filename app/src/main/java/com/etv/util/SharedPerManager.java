package com.etv.util;

import android.text.TextUtils;
import android.util.Log;

import com.EtvApplication;
import com.etv.config.ApiInfo;
import com.etv.config.AppConfig;
import com.etv.config.AppInfo;
import com.etv.setting.entity.RowCow;
import com.etv.task.entity.ViewPosition;
import com.etv.util.system.CpuModel;
import com.etv.util.system.SystemManagerUtil;

import java.util.Calendar;
import java.util.Locale;

public class SharedPerManager {

    /**
     * 设置设备得MAC 地址
     *
     * @param macaddress
     */
    public static void setDevMacAddress(String macaddress) {
        EtvApplication.getInstance().saveData("macaddress", macaddress);
    }

    public static String getDevMacAddress() {
        String resourceServer = (String) EtvApplication.getInstance().getData("macaddress", "");
        return resourceServer;
    }

    /**
     * 是否自动从网络获取屏幕的行列
     */
    public static boolean getAutoRowCow() {
        return (boolean) EtvApplication.getInstance().getData("auto_screen_row", false);
    }

    public static void setAutoRowCow(boolean auto) {
        EtvApplication.getInstance().saveData("auto_screen_row", auto);
    }

    /***
     * 设置显示得位置
     * @return
     */
    public static void setShowScreenRowCow(int row, int cow, int position) {
        EtvApplication.getInstance().saveData("screen_row", row);
        EtvApplication.getInstance().saveData("screen_cow", cow);
        EtvApplication.getInstance().saveData("showPosition", position);
    }


    public static RowCow getShowScreenRowCow() {
        int row = (int) EtvApplication.getInstance().getData("screen_row", 1);
        int cow = (int) EtvApplication.getInstance().getData("screen_cow", 1);
        int position = (int) EtvApplication.getInstance().getData("showPosition", 0);
        return new RowCow(row, cow, position);
    }

    /***
     * 设置连接方式
     * @return
     */
    public static int getSocketType() {
        int defaultSocket = AppConfig.SOCKEY_TYPE_WEBSOCKET;
        switch (AppConfig.CONSUMER_TYPE) {
            case AppConfig.CONSUMER_CNIN_MONEY:
                defaultSocket = AppConfig.SOCKEY_TYPE_WEBSOCKET;
                break;
        }
        int socketType = (int) EtvApplication.getInstance().getData("socketType", defaultSocket);
        return socketType;
    }

    /****
     * 0  webSocket
     * 1  socket
     * @param socketType
     */
    public static void setSocketType(int socketType) {
        SharedPerUtil.CURRENT_SOCKET_TYPE = socketType;
        EtvApplication.getInstance().saveData("socketType", socketType);
    }

    /***
     * 凌晨重启计划
     * @return
     */
    public static boolean getAutoRebootDev() {
        boolean autoRebootDev = (boolean) EtvApplication.getInstance().getData("autoRebootDev", true);
        return autoRebootDev;
    }

    public static void setAutoRebootDev(boolean autoRebootDev) {
        EtvApplication.getInstance().saveData("autoRebootDev", autoRebootDev);
    }

    /***
     * 4K支持
     * @return
     */
    public static boolean getVideoMoreSize() {
        boolean isDefaultEnable = false;
        boolean VideoMoreSize = (boolean) EtvApplication.getInstance().getData("VideoMoreSize", isDefaultEnable);
        return VideoMoreSize;
    }

    public static void setVideoMoreSize(boolean VideoMoreSize) {
        EtvApplication.getInstance().saveData("VideoMoreSize", VideoMoreSize);
    }

    /***
     * 数据来源
     * true 网络
     * false 本地设置
     * @return
     */
    public static boolean getInfoFrom() {
        boolean isDefault = true;
        boolean infoFrom = (boolean) EtvApplication.getInstance().getData("infoFrom", isDefault);
        return infoFrom;
    }

    public static void setInfoFrom(boolean infoFrom) {
        EtvApplication.getInstance().saveData("infoFrom", infoFrom);
    }

//    /**
//     * 是否加载同步任务
//     *
//     * @return
//     */
//    public static boolean getTaskSameEnable() {
//        boolean isDefaultEnable = true;
//        boolean taskSameEnable = (boolean) EtvApplication.getInstance().getData("taskSameEnable", isDefaultEnable);
//        return taskSameEnable;
//    }
//
//    public static void setTaskSameEnable(boolean taskSameEnable) {
//        EtvApplication.getInstance().saveData("taskSameEnable", taskSameEnable);
//    }

    /***
     * 同步主次
     * 0 主
     * 1 次
     */
    public static int getTaskSameMain() {
        int isDefaultEnable = 1;
        int taskSameEnable = (int) EtvApplication.getInstance().getData("TaskSameMainNew", isDefaultEnable);
        return taskSameEnable;
    }

    /***
     * 同步主次
     * 0 主
     * 1 次
     * @param TaskSameMain
     */
    public static void setTaskSameMain(int TaskSameMain) {
        SharedPerUtil.isSameTaskMainLeader = TaskSameMain;
        EtvApplication.getInstance().saveData("TaskSameMainNew", TaskSameMain);
    }

    /****
     * 一键报警得功能
     * @return
     */
    public static boolean getGpioAction() {
        boolean SleepStatues = (boolean) EtvApplication.getInstance().getData("openPoliceAction", false);
        return SleepStatues;
    }

    public static void setGpioAction(boolean openPoliceAction) {
        EtvApplication.getInstance().saveData("openPoliceAction", openPoliceAction);
    }

    /***
     * 获取休眠状态
     * true  当前是休眠状态
     * false 当前是屏幕显示状态
     * @return
     */
    public static boolean getSleepStatues() {
        boolean SleepStatues = (boolean) EtvApplication.getInstance().getData("SleepStatues", false);
        MyLog.sleep("=====休眠状态=获取==" + SleepStatues, true);
        return SleepStatues;
    }

    //设备休眠状态
    public static void setSleepStatues(boolean SleepStatues) {
        MyLog.sleep("=====休眠状态==设置=" + SleepStatues, true);
        EtvApplication.getInstance().saveData("SleepStatues", SleepStatues);
    }

    /***
     * 获取截图质量
     * 0  差
     * 1  中
     * 2 高
     * @return
     */
    public static int getCapturequilty() {
        int currentVoiceNum = ((int) EtvApplication.getInstance().getData("capturequilty", 0));
        return currentVoiceNum;
    }

    /***
     * 设置截图质量
     * @param capturequilty
     */
    public static void setCapturequilty(int capturequilty) {
        EtvApplication.getInstance().saveData("capturequilty", capturequilty);
    }

    /***
     * 获取屏幕的亮度
     * @return
     */
    public static String getScreenLightNum() {
        String cpuModel = ((String) EtvApplication.getInstance().getData("screenLight", "86"));
        MyLog.cdl("=======屏幕亮度===getScreenLightNum==" + cpuModel);
        return cpuModel;
    }

    /***
     * 设置屏幕的亮度
     * @param screenLight
     */
    public static void setScreenLightNum(String screenLight) {
        MyLog.cdl("=======屏幕亮度===setScreenLightNum==" + screenLight);
        EtvApplication.getInstance().saveData("screenLight", screenLight);
    }

    /***
     * 获取CPU的类型
     * @return
     */
    public static String getCpuModel() {
        String cpuModel = ((String) EtvApplication.getInstance().getData("cpuModel", ""));
        return cpuModel;
    }

    /***
     * 设置CPU的类型
     * @param cpuModel
     */
    public static void setCpuModel(String cpuModel) {
        EtvApplication.getInstance().saveData("cpuModel", cpuModel);
    }

    /**
     * 播放统计功能
     *
     * @return
     */
    public static boolean getPlayTotalUpdate() {
        boolean ifHdmiInSuport = ((boolean) EtvApplication.getInstance().getData("PlayTotalUpdate", false));
        return ifHdmiInSuport;
    }

    public static void setPlayTotalUpdate(boolean PlayTotalUpdate) {
        EtvApplication.getInstance().saveData("PlayTotalUpdate", PlayTotalUpdate);
    }


    /***
     * 是否从服务器下拉背景
     * @return
     */
    public static boolean getBggImageFromWeb() {
        boolean ifHdmiInSuport = ((boolean) EtvApplication.getInstance().getData("BggImageFromWeb", true));
        return ifHdmiInSuport;
    }

    /**
     * 设置壁纸背景开关
     *
     * @param BggImageFromWeb
     */
    public static void setBggImageFromWeb(boolean BggImageFromWeb) {
        EtvApplication.getInstance().saveData("BggImageFromWeb", BggImageFromWeb);
    }


    /***
     * 副屏图片旋转上传角度
     */
    public static int getDoubleScreenRoateImage() {
        int doubleScreenRoateImage = ((int) EtvApplication.getInstance().getData("doubleScreenRoateImage", 0));
        return doubleScreenRoateImage;
    }

    /***
     * 副屏图片旋转上传角度
     * @param doubleScreenRoateImage
     */
    public static void setDoubleScreenRoateImage(int doubleScreenRoateImage) {
        EtvApplication.getInstance().saveData("doubleScreenRoateImage", doubleScreenRoateImage);
    }

    /***
     * 获取双屏显示算法适配
     *     public static final int DOUBLE_SCREEN_SHOW_DEFAULT = 0; //原比例显示
     *     public static final int DOUBLE_SCREEN_SHOW_ADAPTER = 1; //强制拉伸
     *     public static final int DOUBLE_SCREEN_SHOW_GT_TRANS = 2; //高通翻转
     * @return
     */
    public static int getDoubleScreenMath() {
        int defaultMath = AppInfo.DOUBLE_SCREEN_SHOW_DEFAULT;
        int doubleScreenMath = ((int) EtvApplication.getInstance().getData("doubleScreenMath", defaultMath));
        return doubleScreenMath;
    }

    /****
     * 设置双屏异步显示得算法
     * 0：原尺寸显示
     * 1：屏幕强制适配
     * 2：高通反转
     * 3：长宽互置
     * @param doubleScreenMath
     */
    public static void setDoubleScreenMath(int doubleScreenMath) {
        Log.e("cdl", "========setDoubleScreenMath====" + doubleScreenMath);
        EtvApplication.getInstance().saveData("doubleScreenMath", doubleScreenMath);
    }

    /**
     * 获取退出密码
     *
     * @return
     */
    private static String exitPassword = "";

    public static String getExitpassword() {
        String devusername = ((String) EtvApplication.getInstance().getData("exitpassword", ""));
        return devusername;
    }

    public static void setExitpassword(String exitpassword) {
        exitpassword = exitpassword.trim();
        if (exitpassword == null || exitpassword.length() < 1 || exitpassword.contains("null")) {
            exitpassword = "";
        }
        EtvApplication.getInstance().saveData("exitpassword", exitpassword);
    }

    /**
     * 用来获取上次提交设备信息得
     * 防止高频率提交设备信息
     * MainActivity
     *
     * @return
     */
    public static long getLastUpdateDevTime() {
        long lastUpdateDevTime = (long) EtvApplication.getInstance().getData("lastUpdateDevTime", 0L);
        return lastUpdateDevTime;
    }

    public static void setLastUpdateDevTime(long lastUpdateDevTime) {
        EtvApplication.getInstance().saveData("lastUpdateDevTime", lastUpdateDevTime);
    }

    /**
     * 是否是正常退出
     * true  手动退出
     * false 异常退出
     *
     * @return
     */
    public static boolean getExitDefault() {
        boolean isBack = (boolean) EtvApplication.getInstance().getData("exitDefault", false);
        return isBack;
    }

    /**
     * 设置是否正常退出
     * 主要是播放保护
     * 如果是手动退出得话就不启动
     * 异常退出，就自己启动
     *
     * @param exitDefault
     */
    public static void setExitDefault(boolean exitDefault) {
        MyLog.cdl("=====是否是正常退出机制===" + exitDefault);
        EtvApplication.getInstance().saveData("exitDefault", exitDefault);
    }

    /**
     * 设备的运行内存
     */
    public static void setDevCacheSize(int devCacheSize) {
        EtvApplication.getInstance().saveData("devCacheSize", devCacheSize);
    }

    /**
     * 获取开机启动开关
     *
     * @return
     */
    public static boolean getOpenPower() {
        boolean openPower = ((boolean) EtvApplication.getInstance().getData("openPower", true));
        return openPower;
    }

    /**
     * 设置开机启动开关
     *
     * @param openPower
     */
    public static void setOpenPower(boolean openPower) {
        EtvApplication.getInstance().saveData("openPower", openPower);
    }

    //获取限制速度
    public static int getLimitSpeed() {
        int limitspeed = ((int) EtvApplication.getInstance().getData("limitspeed", 2000));
        String ipAddress = getWebHost();
        if (ipAddress.contains("119.23.220.53") || ipAddress.contains(ApiInfo.IP_DEFAULT_URL_WEBSOCKET)) {
            limitspeed = 1500;
        }
        if (ipAddress.contains("139.159.152.78") || ipAddress.contains(ApiInfo.IP_DEFAULT_URL_SOCKET)) {
            limitspeed = 1500;
        }

        MyLog.e("SHARED", "====获取的速度===" + limitspeed);
        return limitspeed;
    }

    //设置下载速度
    public static void setLimitSpeed(int limitspeed) {
//        MyLog.e("SHARED", "====设置的速度===" + limitspeed);
        EtvApplication.getInstance().saveData("limitspeed", limitspeed);
    }

    //获取限制下载的台数
    public static int getLimitDevNum() {
        int limitdevnum = ((int) EtvApplication.getInstance().getData("limitdevnum", 200));
        return limitdevnum;
    }

    //设置限制下载的限制台数
    public static void setLimitDevNum(int limitdevnum) {
        EtvApplication.getInstance().saveData("limitdevnum", limitdevnum);
    }

    /**
     * 获取设备昵称
     *
     * @return
     */
    public static String getDevNickName() {
        String codeUtil = CodeUtil.getUniquePsuedoID();
        String devusername = ((String) EtvApplication.getInstance().getData("devNickName", codeUtil));
        return devusername;
    }

    /**
     * 设置设备昵称
     *
     * @param devNickName
     */
    public static void setDevNickName(String devNickName) {
        EtvApplication.getInstance().saveData("devNickName", devNickName);
    }


    public static int getSingleSecondLayoutTag() {
        int singlelayouttag = 0;
        //获取副屏得方向
        boolean isHroVer = SystemManagerUtil.getSecondScreenHrorVer();
        if (isHroVer) { //横屏
            singlelayouttag = ((int) EtvApplication.getInstance().getData("singleSecondlayouttag", ViewPosition.VIEW_LAYOUT_HRO_VIEW));
        } else {//竖屏
            singlelayouttag = ((int) EtvApplication.getInstance().getData("singleSecondlayouttag", ViewPosition.VIEW_LAYOUT_VER_VIEW));
        }
        return singlelayouttag;
    }

    //设置单机模式的布局
    public static void setSingleSecondLayoutTag(int singleSecondlayouttag) {
        EtvApplication.getInstance().saveData("singleSecondlayouttag", singleSecondlayouttag);
    }


    //获取单机模式得布局
    public static int getSingleLayoutTag() {
        int singlelayouttag = 0;
        int width = getScreenWidth();
        int height = getScreenHeight();
        if (width - height > 0) {
            //横屏
            singlelayouttag = ((int) EtvApplication.getInstance().getData("singlelayouttag", ViewPosition.VIEW_LAYOUT_HRO_VIEW));
        } else {//竖屏
            singlelayouttag = ((int) EtvApplication.getInstance().getData("singlelayouttag", ViewPosition.VIEW_LAYOUT_VER_VIEW));
        }
        return singlelayouttag;
    }

    //设置单机模式的布局
    public static void setSingleLayoutTag(int singlelayouttag) {
        EtvApplication.getInstance().saveData("singlelayouttag", singlelayouttag);
    }

    public static String getUserName() {
        String defaultName = "admin";
        String userNameBack = ((String) EtvApplication.getInstance().getData("devusername", defaultName));
        return userNameBack;
    }

    public static void setUserName(String devusername, String tag) {
        MyLog.cdl("====setUserName====" + tag, true);
        EtvApplication.getInstance().saveData("devusername", devusername);
    }

    /***
     * 保存基本保存路径
     * @param
     */
    public static void setBaseSdPath(String BaseSdPath) {
        MyLog.cdl("保存素材存储目录==" + BaseSdPath, true);
        AppInfo.BASE_PATH = BaseSdPath;
        EtvApplication.getInstance().saveData("BaseSdPath", BaseSdPath);
    }

    public static String getBaseSdPath() {
        String savePath = ((String) EtvApplication.getInstance().getData("BaseSdPath", AppInfo.BASE_PATH_INNER));
        return savePath;
    }

    /***
     * 获取内存阈值的级别
     * 1：低级，删除ETV下面的所有文件
     * 2：中级，删除ETV下面所有的文件，删除所有的大型文件
     * 3：高级，删除SD卡下所有的文件
     * @return
     */
    public static int getSdcardManagerAuthor() {
        int SdcardManagerAuthor = ((int) EtvApplication.getInstance().getData("SdcardManagerAuthor", 1));
        return SdcardManagerAuthor;
    }

    /***
     * 设置内存阈值的级别
     * @param SdcardManagerAuthor
     */
    public static void setSdcardManagerAuthor(int SdcardManagerAuthor) {
        EtvApplication.getInstance().saveData("SdcardManagerAuthor", SdcardManagerAuthor);
    }

    /**
     * 获取端口号
     *
     * @return
     */
    public static String getWebPort() {
        String ipAddress = ((String) EtvApplication.getInstance().getData("webPort", "8899"));
        return ipAddress;
    }

    /**
     * 保存端口号
     *
     * @param webPort
     */
    public static void setWebPort(String webPort) {
        SharedPerUtil.WEBHOST_PORT = webPort;
        EtvApplication.getInstance().saveData("webPort", webPort);
    }

    /***
     * 获取保存的IP地址
     * @return
     */
    public static String getWebHost() {
        String ipAddressDefault = ApiInfo.IP_DEFAULT_URL_WEBSOCKET;
        if (SharedPerUtil.SOCKEY_TYPE() == AppConfig.SOCKEY_TYPE_WEBSOCKET) {
            ipAddressDefault = ApiInfo.IP_DEFAULT_URL_WEBSOCKET;
        } else if (SharedPerUtil.SOCKEY_TYPE() == AppConfig.SOCKEY_TYPE_SOCKET) {
            ipAddressDefault = ApiInfo.IP_DEFAULT_URL_SOCKET;
        }

        switch (AppConfig.CONSUMER_TYPE) {
            case AppConfig.CONSUMER_CNIN_MONEY:
                ipAddressDefault = "10.247.99.121";
                break;
            case AppConfig.CONSUMER_SNGHANG_VIDEO:
                ipAddressDefault = "192.168.210.200";
                break;
        }
        String ipAddress = ((String) EtvApplication.getInstance().getData("webHost", ipAddressDefault));
        return ipAddress;
    }

    /***
     * 保存服务器IP地址
     * @param webHost
     */
    public static void setWebHost(String webHost) {
        SharedPerUtil.WEBHOST_IP_ADDRESS = webHost;
        MyLog.cdl("=========设置的服务器IP地址===" + webHost);
        EtvApplication.getInstance().saveData("webHost", webHost);
    }

    public static int getScreenHeight() {
        int screenHeight = 1080;
        return ((Integer) EtvApplication.getInstance().getData("screenHeight", screenHeight));
    }

    public static void setScreenHeight(int screenHeight) {
        EtvApplication.getInstance().saveData("screenHeight", screenHeight);
    }

    public static int getScreenWidth() {
        int defaultWidth = 1920;
        return ((Integer) EtvApplication.getInstance().getData("screenWidth", defaultWidth));
    }

    public static void setScreenWidth(int screenWidth) {
        EtvApplication.getInstance().saveData("screenWidth", screenWidth);
    }

    public static String getmLatitude() {
        String mLatitude = ((String) EtvApplication.getInstance().getData("mLatitude", "116.413384"));
        MyLog.location("===获取得坐标==mLatitude==" + mLatitude);
        return mLatitude;
    }

    public static void setmLatitude(String mLatitude) {
        EtvApplication.getInstance().saveData("mLatitude", mLatitude);
    }

    public static String getmLongitude() {
        String mLongitude = ((String) EtvApplication.getInstance().getData("mLongitude", "39.910925"));
        MyLog.location("===获取得坐标==mLongitude==" + mLongitude);
        return mLongitude;
    }

    public static void setmLongitude(String mLongitude) {
        EtvApplication.getInstance().saveData("mLongitude", mLongitude);
    }

    public static void setLocalCity(String city) {
        EtvApplication.getInstance().saveData("city", city);
    }

    public static String getLocalCiti() {
        return ((String) EtvApplication.getInstance().getData("city", ""));
    }

    //==========================================================================================

    public static String getDevOnwer() {
        return ((String) EtvApplication.getInstance().getData("devOnwer", "官方设备,未注册授权"));
    }

    public static void setDevOnwer(String devOnwer) {
        EtvApplication.getInstance().saveData("devOnwer", devOnwer);
    }

    /***
     * 获取所有的地址
     * @return
     */
    public static String getAllAddress() {
        String address = getProvince() + getLocalCiti() + getArea() + getDetailAddress();
        if (address == null || address.length() < 2) {
            address = "No Address";
        }
        return address;
    }


    public static String getProvince() {
        return ((String) EtvApplication.getInstance().getData("province", ""));
    }

    public static void setProvince(String province) {
        EtvApplication.getInstance().saveData("province", province);
    }

    public static String getArea() {
        return ((String) EtvApplication.getInstance().getData("area", ""));
    }

    public static void setArea(String area) {
        EtvApplication.getInstance().saveData("area", area);
    }

    public static void setDetailAddress(String detailaddress) {
        EtvApplication.getInstance().saveData("detailaddress", detailaddress);
    }

    public static String getDetailAddress() {
        return ((String) EtvApplication.getInstance().getData("detailaddress", " "));
    }

    /***
     * 默认自动定位
     * true  自动定位
     * false 手动定位
     * @return
     */
    public static boolean getAutoLocation() {
        return ((boolean) EtvApplication.getInstance().getData("aotolocation", true));
    }

    public static void setAutoLocation(boolean aotolocation) {
        EtvApplication.getInstance().saveData("aotolocation", aotolocation);
    }

    /***
     * 获取设备的唯一值
     * @return
     */
    public static String getUniquePsuedoID() {
        String code = (String) EtvApplication.getInstance().getData("onlycode", "");
        return code;
    }

    /***
     * 保存设备的唯一值
     * @param onlycode
     */
    public static void setUniquePsuedoID(String onlycode) {
        EtvApplication.getInstance().saveData("onlycode", onlycode);
    }

    /**
     * 图片的间隔时间
     *
     * @return
     */
    public static int getPicDistanceTime() {
        int backTime = (int) EtvApplication.getInstance().getData("picDistanceTime", 10);
        return backTime;
    }

    public static void setPicDistanceTime(int picDistanceTime) {
        EtvApplication.getInstance().saveData("picDistanceTime", picDistanceTime);
    }


    /**
     * 获取WPS文件得切换时间
     *
     * @return
     */
    public static int getWpsDistanceTime() {
        return ((int) EtvApplication.getInstance().getData("wpsDistanceTime", 10));
    }

    public static void setWpsDistanceTime(int wpsDistanceTime) {
        EtvApplication.getInstance().saveData("wpsDistanceTime", wpsDistanceTime);
    }


    /**
     * 获取单机模式图片切换动画
     *
     * @return
     */
    public static int getSinglePicAnimiType() {
        //默认淡入淡出效果
        return ((int) EtvApplication.getInstance().getData("singlePicAnimiType", 0));
    }

    public static void setSinglePicAnimiType(int singlePicAnimiType) {
        EtvApplication.getInstance().saveData("singlePicAnimiType", singlePicAnimiType);
    }

    /**
     * 获取单机版本的音量
     *
     * @return
     */
    public static int getSingleVideoVoiceNum() {
        return ((int) EtvApplication.getInstance().getData("videoVoiceNum", 70));
    }

    public static void setSingleVideoVoiceNum(int videoVoiceNum) {
        EtvApplication.getInstance().saveData("videoVoiceNum", videoVoiceNum);
    }

    /**
     * 获取背景音乐的音量
     *
     * @return
     */
    public static int getSingleBackVoiceNum() {
        return ((int) EtvApplication.getInstance().getData("singleBackVoiceNum", 70));
    }

    /**
     * 设置背景音乐的音量
     *
     * @return
     */
    public static void setSingleBackVoiceNum(int singleBackVoiceNum) {
        EtvApplication.getInstance().saveData("singleBackVoiceNum", singleBackVoiceNum);
    }

    /**
     * 是否显示网络导入任务
     *
     * @return
     */
    public static boolean getShowNetDownTask() {
        return ((boolean) EtvApplication.getInstance().getData("showNetDownTask", false));
    }

    /**
     * @param showNetDownTask
     */
    public static void setShowNetDownTask(boolean showNetDownTask) {
        EtvApplication.getInstance().saveData("showNetDownTask", showNetDownTask);
    }

    public static String getPackageNameBySp() {
        return ((String) EtvApplication.getInstance().getData("packageName", "com.ys.etv"));
    }

    public static void setPackageName(String packageName, String tag) {
        EtvApplication.getInstance().saveData("packageName", packageName);
    }

    /**
     * 获取缓存的守护进程的状态
     *
     * @return
     */
    public static boolean getGuardianStatues() {
        return ((boolean) EtvApplication.getInstance().getData("guardianStatues", true));
    }

    /**
     * 开机启动的时候会设置守护进程的状态
     *
     * @param guardianStatues
     */
    public static void setGuardianStatues(boolean guardianStatues) {
        EtvApplication.getInstance().saveData("guardianStatues", guardianStatues);
    }

    /**
     * 是否使用浏览器内核
     * 硬件加速
     *
     * @return
     */
    public static boolean getDevSpeedStatues() {
        return ((boolean) EtvApplication.getInstance().getData("isUseWebViewImp", true));
    }

    /**
     * 设置浏览器内核
     * 硬件加速
     *
     * @param isUseWebViewImp
     */
    public static void setDevSpeedStatues(boolean isUseWebViewImp) {
        MyLog.cdl("======设置浏览器内核====" + isUseWebViewImp);
        EtvApplication.getInstance().saveData("isUseWebViewImp", isUseWebViewImp);
    }

    /**
     * 设置双屏联动
     *
     * @param isScreenSame
     */
    public static void setScreenSame(boolean isScreenSame) {
        EtvApplication.getInstance().saveData("isScreenSame", isScreenSame);
    }

    /**
     * 是否双屏联动
     *
     * @return
     */
    public static boolean getScreenSame() {
        boolean isDefault = true;
        return ((boolean) EtvApplication.getInstance().getData("isScreenSame", isDefault));
    }

    /**
     * 获取屏幕了类型
     *
     * @return
     */
    public static final int SCREEN_TYPE_DEFAULT = 0;                          //两个屏幕同一个方向
    public static final int SCREEN_TYPE_DOUBLE_VER_OTHER_HRO = 1;            //主副屏--横屏，是竖屏

    public static int getScreen_type() {
        int screenType = (int) EtvApplication.getInstance().getData("screentype", SCREEN_TYPE_DEFAULT);
        MyLog.screen("======获取屏幕方向类型====" + screenType);
        return (int) EtvApplication.getInstance().getData("screentype", SCREEN_TYPE_DEFAULT);
    }

    /***
     * 设置屏幕类型
     * @param screentype
     */
    public static void setScreen_type(int screentype) {
        MyLog.cdl("======设置屏幕方向类型====" + screentype);
        EtvApplication.getInstance().saveData("screentype", screentype);
    }


    //人脸授权信息
    public static String getAuthorId() {
        //        String authId = "xLZCDyRn";  //M8 授权
        //        String authId = "Q4GSLrcb";  //FACE88 授权
        return (String) EtvApplication.getInstance().getData("authorId", "");
    }

    public static void setAuthorId(String authorId) {
        EtvApplication.getInstance().saveData("authorId", authorId);
    }

    public static void setPersonOne(String personOne) {
        Log.e("cdl", "====setPersonOne===" + personOne);
        EtvApplication.getInstance().saveData("personOne", personOne);
    }

    public static String getPersonOne() {
        return (String) EtvApplication.getInstance().getData("personOne", "");
//        return (String) EtvApplication.getInstance().getData("personOne", "C78585022");
    }


    public static void setPersonTwo(String personTwo) {
        Log.e("cdl", "===setPersonTwo====" + personTwo);
        EtvApplication.getInstance().saveData("personTwo", personTwo);
    }

    public static String getPersonTwo() {
        return (String) EtvApplication.getInstance().getData("personTwo", "");
//        return (String) EtvApplication.getInstance().getData("personTwo", "202480468");
    }

    /***
     * 0 隐藏
     * 1：显示
     * 单机版本显示时间
     * @return
     */
    public static int getShowTimeEnable() {
        int ShowTimeEnable = (int) EtvApplication.getInstance().getData("ShowTimeEnable", 0);
        return ShowTimeEnable;
    }

    public static void setShowTimeEnable(int ShowTimeEnable) {
        EtvApplication.getInstance().saveData("ShowTimeEnable", ShowTimeEnable);
    }

    /***
     * 获取SOCKET 得IP address
     * @return
     */
    public static String getSocketIpAddress() {
        String SocketIpAddress = (String) EtvApplication.getInstance().getData("SocketIpAddress", ApiInfo.SOCKET_DEFAULR_IP_ADDRESS);
        return SocketIpAddress;
    }

    public static void setSocketIpAddress(String SocketIpAddress) {
        SharedPerUtil.DEFAULT_SOCKET_IP_ADDRESS = SocketIpAddress;
        EtvApplication.getInstance().saveData("SocketIpAddress", SocketIpAddress);
    }


    /***
     * 获取 SOCKET 得端口号
     * @return
     */
    public static int getSocketPort() {
        int SocketPort = (int) EtvApplication.getInstance().getData("SocketPort", ApiInfo.SOCKET_DEFAULR_PORT);
        return SocketPort;
    }

    public static void setSocketPort(int SocketPort) {
        SharedPerUtil.DEFAULT_SOCKET_PORT = SocketPort;
        EtvApplication.getInstance().saveData("SocketPort", SocketPort);
    }

    /***
     * 设置素材得保存路径
     * @param resourceServer
     */
    public static void setResourDownPath(String resourceServer) {
        EtvApplication.getInstance().saveData("resourceServer", resourceServer);
    }

    public static String getResourDownPath() {
        String resourceServer = (String) EtvApplication.getInstance().getData("resourceServer", ApiInfo.WEB_BASE_URL());
        return resourceServer;
    }


    private static int backMessageType = -1;

    /***
     * 同步方式
     * @return
     */
    public static int getMessageType() {
        if (backMessageType != -1) {
            return backMessageType;
        }
        backMessageType = (int) EtvApplication.getInstance().getData("getMessageType", AppInfo.MESSAGE_TYPE_UDP);
        return backMessageType;
    }

    public static void setMessageType(int getMessageType) {
        backMessageType = getMessageType;
        EtvApplication.getInstance().saveData("getMessageType", getMessageType);
    }


    private static int TTysPositionBack = -1;

    public static void setTTysPosition(int TTysPosition) {
        TTysPositionBack = TTysPosition;
        EtvApplication.getInstance().saveData("TTysPosition", TTysPosition);
    }

    public static int getTTysPosition() {
        if (TTysPositionBack != -1) {
            return TTysPositionBack;
        }
        TTysPositionBack = (int) EtvApplication.getInstance().getData("TTysPosition", 0);
        return TTysPositionBack;
    }

    /***
     * 获取工作模式
     * @return
     * 0 :网络模式
     * 1 :网络下发
     * 2 :单机模式
     */

    public static int WORKMODELBACK = -1;

    public static int getWorkModel() {
        if (WORKMODELBACK != -1) {
            return WORKMODELBACK;
        }
        int workmldeo = ((int) EtvApplication.getInstance().getData("workModel", AppInfo.WORK_MODEL_NET));
        WORKMODELBACK = workmldeo;
        return workmldeo;
    }

    public static void setWorkModel(int workModel, String printTag) {
        if (workModel == AppInfo.WORK_MODEL_NET) {
            MyLog.cdl("切换工作模式==网络下发 /" + printTag, true);
        } else if (workModel == AppInfo.WORK_MODEL_NET_DOWN) {
            MyLog.cdl("切换工作模式==网络导出 /" + printTag, true);
        } else if (workModel == AppInfo.WORK_MODEL_SINGLE) {
            MyLog.cdl("切换工作模式==单机模式 /" + printTag, true);
        }
        WORKMODELBACK = workModel;
        EtvApplication.getInstance().saveData("workModel", workModel);
    }
}
