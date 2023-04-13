package com.etv.config;

import android.os.Environment;

import com.etv.task.entity.SceneEntity;
import com.etv.util.CodeUtil;
import com.etv.util.FileUtil;
import com.etv.util.SharedPerManager;

import org.json.JSONObject;

public class AppInfo {

    public static final String BASE_PATH_INNER = Environment.getExternalStorageDirectory().getPath();
    public static final String CAPTURE_MAIN = AppInfo.BASE_PATH_INNER + "/capture.jpg";
    public static final String CAPTURE_SECOND = AppInfo.BASE_PATH_INNER + "/capture_second.jpg";

    //网络模式
    public static final int WORK_MODEL_NET = 0;
    //网络导出
    public static final int WORK_MODEL_NET_DOWN = 1;
    //单机模式
    public static final int WORK_MODEL_SINGLE = 2;

    //验证程序是否起来了
    public static boolean isAppRun = false;
    //设备是否已经注册到服务器
    public static boolean isDevRegister = false;
    //标记本次开机是否定位了，每次开机只定位一次
    public static boolean isLocationSuccess = false;
    //标记是否同步了服务器时间
    public static boolean updateLocalTime = false;

    public static final String IP_FILE_NAME = "etv-ip.txt";                //USB ip文件名字
    public static final String DISONLINE_TASK_DIR_ZIP = "etv_disonline";   //USB离线文件使用
    public static final String SINGLE_TASK_DIR_ZIP = "etv-media";          //USB 单机任务

    //    etv-start.txt
    public static final String VOICE_MEDIA = "etv-welcome.mp3";
    public static final String WELCOME_SAVE_PATH = BASE_PATH_INNER + "/Android/welcome.mp3";

    //客户定制 type类型
    public static final int MESSAGE_TYPE_CUSTOM_MADE = 6666;

    //是否可以检测定时任务
    public static boolean startCheckTaskTag = false;
    //服务器发给设备的操控指令类型
    public static final int MESSAGE_TYPE_ORDER_WEB = 1000;
    //服务器特殊指令
    public static final int MESSAGE_TYPE_SOCKET_DEV = 2000;
    //小程序遥控指令
    public static final int MESSAGE_TYPE_WINCHAT_SOFT = 4000;
    //设备端用来检查心跳得回收消息
    public static final int SERVER_BACK_DEV_IS_ONLINE = 4002;
    //发送指令给服务器，心跳包


    //同步通讯方式
    public static final int MESSAGE_TYPE_UDP = 0;  //局域网信号传输
    public static final int MESSAGE_TYPE_SERIALPORT = 1;  //串口通讯

    /***
     * 心跳消息
     * @return
     */
    public static String HEART_SEND_TO_WEB() {
        String msg = "{\"code\":\"6002\",\"msg\":\"\",\"clNo\":\"" + CodeUtil.getUniquePsuedoID() + "\"}";
        return msg;
    }

    //6001  普通socket指令
    //6002  心跳机制
    //6003  指令相应
    //发相应给服务器
    public static String SendCodeToServer(String messageId) {
        return "{\"code\":\"6003\", \"msg\":\"" + messageId + "\"}";
    }

    public static final int ORDER_PLAY = 5001;           //播放
    public static final int ORDER_STOP = 5003;             //停止
    public static final int ORDER_VOICE = 5004;             //声音大小
    public static final int ORDER_CKEAR_SDCARD = 5005;   //清理磁盘
    public static final int ORDER_CLEAR_PROJECT = 5006;  //清理任务
    public static final int ORDER_REBOOT_DEV = 5007;      //重启设备
    public static final int ORDER_UPDATE = 5008;          //升级
    public static final int ORDER_CLEAR_POWER_ON_OFF = 5009; //刷新定时开关机
    public static final int ORDER_MONITOR = 5010;            //监控
    public static final int ORDER_WORK = 5011;               //新任务任务
    public static final int ORDER_CLEAR_TIMER_TASK = 5012;   //定时开关机任务
    public static final int ORDER_SYSTEM_SETTING = 5013;    //系统设置，重新去拉去信息
    public static final int ORDER_CLEAR_UPDATE = 5014;       //清理升级任务
    public static final int UPDATE_NICK_NAME = 5015;         //更新昵称信息
    public static final int ORDER_UPDATE_LOCATION = 5016;  //跟新设备定位信息
    public static final int ORDER_WEB_DEL_DEV = 5017;      //服务器删除设备
    public static final int ORDER_SHUT_DOWMN = 5018;      //设备关机
    public static final int ORDER_TURN_OFF_BACKLIGHT = 5019;      //屏幕休眠
    public static final int ORDER_TURN_ON_BACKLIGHT = 5020;      //屏幕唤醒
    //屏幕唤醒
    public static final int ORDER_TASK_SAME = 5021;       //同步任务
    public static final int ORDER_MODIFY_BGG_IMAGE = 5022;  //修改主界面背景图
    public static final int ORDER_CLEAR_BGG_IMAGE = 5023;  //清除主界面背景图
    public static final int ORDER_CHANGE_DEV_TO_OTHER_USER = 5024;  //修改设备归属
    public static final int ORDER_UPDATE_WORK_INFO_TO_WEB = 5025;  //上传设备日志
    public static final int WET_SCAN_REGISTER_DEV_WEB = 5026;  //小程序扫码注册
    public static final int ORDER_TTS_MESSAGE = 5027;  //TTS语音
    public static final int ORDER_MODIFY_SERVER = 5028;          //修改服务器配置选项

    //双屏显示类型
    public static final int DOUBLE_SCREEN_SHOW_DEFAULT = 0; //原比例显示
    public static final int DOUBLE_SCREEN_SHOW_ADAPTER = 1; //强制拉伸
    public static final int DOUBLE_SCREEN_SHOW_GT_TRANS = 2; //高通翻转
    public static final int DOUBLE_SCREEN_SHOW_PX30 = 3;      //PX 30

    /***
     * 播放模式
     * ETLEVEL
     */
    //1 替换   2 追加   3 插播  4：同步 5: 触发任务  6：同屏任务
    public static final String TASK_PLAY_REPLACE = "1";     // 1替换模式
    public static final String TASK_PLAY_ADD_TASK = "2";    // 2追加模式
    public static final String TASK_PLAY_CALL_WAIT = "3";   // 3插播模式
    public static final String TASK_PLAY_PLAY_SAME = "4";   // 4同步模式
    public static final String TASK_PLAY_SAME_SCREEN = "6"; // 6同屏模式

    /**
     * 节目类型
     */
    public static final String PROGRAM_DEFAULT = "1";          //普通节目
    public static final String PROGRAM_TOUCH = "2";            //互动节目
//    public static final String PROGRAM_TEXT_INSERT = "3";      //消息插播

    /**
     * 任务模式
     * etType
     */
    public static final String TASK_TYPE_DEFAULT = "1";        //普通任务
    public static final String TASK_TYPE_DOUBLE = "2";         //双屏任务
    public static final String TASK_TYPE_TOUCH = "3";          //互动任务

    public static final String PROGRAM_POSITION_MAIN = "1";   //主屏
    public static final String PROGRAM_POSITION_SECOND = "2"; //副屏
    /***
     * 控件格式
     */
    public static final String VIEW_IMAGE = "img";                 //图片格式
    public static final String VIEW_VIDEO = "video";               //视屏格式
    public static final String VIEW_IMAGE_VIDEO = "mixture";       //混播格式

    /***
     * 获取基本路径
     * @return
     */
    public static String BASE_PATH = "";

    public static String BASE_SD_PATH() {
        if (BASE_PATH != null && BASE_PATH.length() > 2) {
            return BASE_PATH;
        }
        String basePath = SharedPerManager.getBaseSdPath();
        BASE_PATH = basePath;
        return basePath;
    }

    /***
     * 这里可能用户频繁插拔SD卡，导致路径切换失败的情况
     * 先取值，如果不存在的话，设置内存路径
     * 再次取值
     * @return
     */
    public static String BASE_PATH() {
        String path = BASE_SD_PATH() + "/etv";
        return path;
    }

    public static String BASE_FONT_PATH() {
        String path = BASE_PATH() + "/font";
        return path;
    }

    public static String APK_PATH() {
        return BASE_PATH() + "/apk";
    }


    public static String LINE_SCREEN_PATH() {
        return BASE_PATH() + "/line";
    }

    /**
     * 单机模式的存储地址
     *
     * @return
     */
    public static String TASK_SINGLE_PATH() {
        return BASE_PATH() + "/single";
    }

    /***
     * 用来缓存 logo 的位置
     * @return
     */
    public static String APP_LOGO_PATH() {
        return BASE_PATH() + "/logo";
    }

    /***
     * 保存视频得位置
     * @return
     */
    public static String APP_VIDEO_PATH() {
        return BASE_PATH() + "/video";
    }

    /**
     * 保存log的位置
     *
     * @return
     */
    public static String BASE_CRASH_LOG() {
        String path = BASE_PATH_INNER + "/crashlog";
        return path;
    }

    public static String BASE_APK() {
        FileUtil.MKDIRSfILE("/sdcard/etv");
        FileUtil.MKDIRSfILE("/sdcard/etv/apk");
        return BASE_PATH_INNER + "/etv/apk";
    }

    public static String BASE_IMAGE_RECEIVER() {
        return BASE_PATH() + "/receiver";
    }

    public static String FILE_RECEIVER_PATH() {
        return BASE_IMAGE_RECEIVER();
    }

    /**
     * 背景图得保存地址
     *
     * @return
     */
    public static String BASE_BGG_IMAGE() {
        return BASE_PATH() + "/bggimg";
    }

    public static String BASE_CACHE() {
        return BASE_PATH() + "/cache";
    }

    public static String BASE_TASK_URL() {
        return BASE_PATH() + "/task";
    }

    public static String ER_CODE_PATH() {
        return BASE_CACHE() + "/bindErcode.jpg";
    }

    public static String ER_CODE_IP_PATH() {
        return BASE_CACHE() + "/ipshow.jpg";
    }

    public static String TAG_UPDATE = "TAG_UPDATE";  //截图上传
    public static String TAG_SHOW = "TAG_SHOW";      //页面加载

    //hDMIN 得软件包名
    public static final String HDMI_IN_PACKAGE_NAME = "teaonly.rk.droidipcam";
    public static final String HDMI_IN_SERVICE_NAME = "com.hdmi.service.HdmiInScreenService";

    //守护进程的包名
    public static final String GUARDIAN_PACKAGE_NAME = "com.guardian";
    public static final String GUARDIAN_APP_NAME = "guardian.apk";

    //APK跳转需要的SO文件
    public static final String APK_BACK_FILE_NAME = "libinputflinger.so";

    //* 网络连接成功
    public static final String NET_ONLINE = "com.ys.receiver.NET_ONLINE";
    // * 网络连接断开
    public static final String NET_DISONLINE = "com.reeman.receiver.NET_DISONLINE";
    public static final String BAIDU_LOCATION_BROAD = "com.etv.config.BAIDU_LOCATION_BROAD";                //定位成功广播
    public static final String STOP_DOWN_TASK_RECEIVER = "com.etv.config.STOP_DOWN_TASK_RECEIVER";   //停止下载任务
    public static final String SOCKET_LINE_STATUS_CODE = "com.etv.config.SOCKET_LINE_STATUS_CODE";          //sock连接状态==code
    public static final String SOCKET_LINE_STATUS_CHANGE = "com.etv.config.SOCKET_LINE_STATUS_CHANGE";      //sock连接状态改变广播
    public static final String DEV_ONLINE_NOT_LOGIN = "当前设备在线,不用重复登录";
    public static final String MESSAGE_CLEAR_UPDATE_IMG_APK = "com.etv.config.MESSAGE_CLEAR_UPDATE_IMG_APK";  //接受到清理升级任务指令

    public static final String RECEIVE_STOP_PLAY_TO_VIEW = "com.etv.config.RECEIVE_STOP_PLAY_TO_VIEW"; //收到停止的指令
    public static final String DOWN_TASK_SUCCESS = "com.etv.config.DOWN_TASK_SUCCESS"; //任务下载完成d
    //设备检索服务器，服务器返回结果，通知界面
    public static final String UDP_SERVER_SEND_IP_PORT = "UDP_SERVER_SEND_IP_PORT";
    //获取的任务==null
    public static final String TASK_GET_INFO_NULL = "com.etv.service.TaskService.TASK_GET_INFO_NULL";
    //息屏广播，通知播放界面副屏也需要暂停功能
    public static final String MESSAGE_RECEIVE_SCREEN_CLOSE = "teaonly.rk.droidipcam.MESSAGE_RECEIVE_SCREEN_CLOSE";

    /***
     * 开启守护进程App
     * 通知N秒后开始
     * 这几个广播不能改动
     */
    public static final String START_PROJECTOR_GUARDIAN_TIMER = "START_PROJECTOR_GUARDIAN_TIMER";  //通知守护进程，从现在开始计时
    public static final String RECEIVE_BROAD_CAST_LIVE = "RECEIVE_BROAD_CAST_LIVE"; //守护进程发通知过来
    public static final String SEND_BROAD_CAST_LIVE = "SEND_BROAD_CAST_LIVE"; //告诉守护进程自己还活着
    public static final String CHANGE_GUARDIAN_STATUES = "CHANGE_GUARDIAN_STATUES"; //ETV发过来的广播修改守护进程的状态
    public static final String MODIFY_GUARDIAN_TIME = "MODIFY_GUARDIAN_TIME"; //ETV发过来的广播修改守护进程的守护时间
    public static final String MODIFY_GUARDIAN_POWER_START_ETV = "MODIFY_GUARDIAN_POWER_START_ETV";  //修改开机启动ETV

    public static final String CHANGE_ORDER_COMPANY = "CHANGE_ORDER_COMPANY";    //修改公司用户的广播

    public static final String CAPTURE_IMAGE_RECEIVE = "CAPTURE_IMAGE_RECEIVE";  //发送截图的广播
    public static final String SEND_IMAGE_CAPTURE_SUCCESS = "SEND_IMAGE_CAPTURE_SUCCESS";  //截图成功，返回的广播

    public static final String CHECK_BGG_IMAGE_TO_DOWN_SHOW = "CHECK_BGG_IMAGE_TO_DOWN_SHOW"; //下载刷新背景图UI
    //    public static final String DOWN_BGG_IMAGE_UPDATE_MAIN_VIEW = "DOWN_BGG_IMAGE_UPDATE_MAIN_VIEW"; //下载背景图，这里率先你主界面背景图
    public static final String MODIFY_GUARDIAN_PACKAGENAME = "MODIFY_GUARDIAN_PACKAGENAME";  //修改守护进程包名
    public static final String RED_LINE_LISTENER_IN = "RED_LINE_LISTENER_IN";  //红外感应有东西
    public static final String RED_LINE_LISTENER_OUT = "RED_LINE_LISTENER_OUT";  //红外感应没有东西
    public static final String SET_LISTENER_PERSON_OPEN = "SET_LISTENER_PERSON_OPEN";  //设置触发感应
    public static final String SET_LISTENER_PERSON_IO_CHOOICE = "SET_LISTENER_PERSON_IO_CHOOICE";  //设置触发IO
    public static final String SET_LISTENER_MESSAGE_BACK = "SET_LISTENER_MESSAGE_BACK";  //设置触发通知反向
    public static final String SET_TRIGGLE_SPEED = "SET_TRIGGLE_SPEED";  //设置IO响应速度
    public static final String SET_LISTENER_PERSON_INFO = "SET_LISTENER_PERSON_INFO";  //设置触发感应一次提交所有的信息
    public static final String SET_LISTENER_IO_PERMISSION = "SET_LISTENER_IO_PERMISSION";  //设置请求权限
    public static final String SCREEN_BACK_LIGHT_OPEN = "com.etv.task.activity.SCREEN_BACK_LIGHT_OPEN";  //屏幕亮起来了。

    //=======================================================================
    public static final String CHECK_BACE_PATH = "/data/user/0/com.ys.etv/app_tbs/core_share/";
    public static final String CHECK_TECENT_APP_PATH = CHECK_BACE_PATH + "videores.apk"; //用来判断tecentX5 是否安装

    //home按键被监听
    public static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    public static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    public static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

    //MLOGIC hdmi插口节点
    public static String HDMIIN1 = "com.droidlogic.tvinput/.services.Hdmi1InputService/HW5";
    public static String HDMIIN2 = "com.droidlogic.tvinput/.services.Hdmi2InputService/HW6";


}
