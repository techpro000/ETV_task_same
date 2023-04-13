package com.etv.service.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import com.EtvApplication;
import com.etv.activity.model.RegisterDevListener;
import com.etv.config.ApiInfo;
import com.etv.config.AppConfig;
import com.etv.config.AppInfo;
import com.etv.db.DbBggImageUtil;
import com.etv.db.DbDevMedia;
import com.etv.db.DbFontInfo;
import com.etv.entity.BggImageEntity;
import com.etv.entity.ControlMediaVoice;
import com.etv.entity.FontEntity;
import com.etv.entity.PowerOnOffEntity;
import com.etv.entity.ScreenEntity;
import com.etv.entity.TimedTaskListEntity;
import com.etv.http.PersenerDevAllInfoRunnable;
import com.etv.http.util.CaptureRunnable;
import com.etv.http.util.CompressImageRunnable;
import com.etv.http.util.ZipImageRunnable;
import com.etv.listener.CompressImageListener;
import com.etv.listener.TaskChangeListener;
import com.etv.listener.WriteBitmapToLocalListener;
import com.etv.service.EtvService;
import com.etv.service.listener.SysSettingAllInfoListener;
import com.etv.service.listener.TcpServerListener;
import com.etv.util.CodeUtil;
import com.etv.util.MyLog;
import com.etv.util.ScreenUtil;
import com.etv.util.SharedPerManager;
import com.etv.util.SharedPerUtil;
import com.etv.util.SimpleDateUtil;
import com.etv.util.guardian.GuardianUtil;
import com.etv.util.image.CaptureImageListener;
import com.etv.util.image.ImageRotateUtil;
import com.etv.util.rxjava.AppStatuesListener;
import com.etv.util.rxjava.RxBus;
import com.etv.util.rxjava.event.RxEvent;
import com.etv.util.sdcard.MySDCard;
import com.etv.util.system.CpuModel;
import com.etv.util.system.SystemManagerUtil;
import com.etv.util.upload.UpdateFileRunnable;
import com.etv.util.upload.UpdateImageListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class TcpServerModuleImpl implements TcpServerModule {

    /***
     * 获取所有得设备属性
     * @param listener
     */
    @Override
    public void getSystemSettingAllInfo(SysSettingAllInfoListener listener) {
        String requestUrl = ApiInfo.getDevSettingAllInfo();
        String clNo = CodeUtil.getUniquePsuedoID();
        String userName = SharedPerManager.getUserName();
        if (userName.contains("null") || userName.length() < 3) {
            return;
        }


        MyLog.cdl("====获取设备信息全部==" + requestUrl + "?userName=" + userName + "&clNo=" + clNo);
        OkHttpUtils
                .post()
                .url(requestUrl)
                .addParams("clNo", clNo)
                .addParams("userName", userName)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, String errorDesc, int id) {
                        MyLog.cdl("====getSystemSettingAllInfo==errorDesc===" + errorDesc);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (response == null || TextUtils.isEmpty(response)) {
                            MyLog.cdl("===getSystemSettingAllInfo==response==null==");
                            return;
                        }
                        MyLog.cdl("===getSystemSettingAllInfo==response====" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int code = jsonObject.getInt("code");
                            String msg = jsonObject.getString("msg");
                            if (code != 0) {
                                MyLog.cdl("===getSystemSettingAllInfo==解析 Code!=0 ====" + msg);
                                return;
                            }
                            String data = jsonObject.getString("data");
                            if (data == null || TextUtils.isEmpty(data)) {
                                MyLog.cdl("===getSystemSettingAllInfo==data==null==");
                                return;
                            }
                            parsenerDeviceAllData(data, listener);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void parsenerDeviceAllData(String data, SysSettingAllInfoListener listener) {
        PersenerDevAllInfoRunnable runnable = new PersenerDevAllInfoRunnable(data, listener);
        EtvService.getInstance().executor(runnable);
    }

    @Override
    public void getBggImageInfoStatues(TcpServiceView taskServiceView) {
        MyLog.bgg("====开始检测背景图信息==11111=");
        String requestUrl = ApiInfo.getBggImageStatuesUrl();
        // http://119.23.220.53:8899/etv/webservice/selectClientDiySet?clientNo=301F9A64C672D41243FE746A
        String clNO = CodeUtil.getUniquePsuedoID();
        OkHttpUtils
                .post()
                .url(requestUrl)
                .addParams("clientNo", clNO)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, String errorrDesc, int id) {
                        MyLog.bgg("====获取背景errorrDesc==" + errorrDesc);
                        backSuccessBggImageInfo(errorrDesc, taskServiceView);
                    }

                    @Override
                    public void onResponse(String json, int id) {
                        MyLog.bgg("====获取背景success==" + json);
                        try {
                            if (json == null || json.length() < 5) {
                                backSuccessBggImageInfo("JSON返回信息为null", taskServiceView);
                                return;
                            }
                            JSONObject object = new JSONObject(json);
                            int code = object.getInt("code");
                            String desc = object.getString("msg");
                            if (code != 0) {
                                backSuccessBggImageInfo(desc, taskServiceView);
                                return;
                            }
                            String data = object.getString("data");
                            JSONObject jsonObjectData = new JSONObject(data);
                            String clientDiyLst = jsonObjectData.getString("clientDiyLst");
                            if (data.contains("clientLogoInfo")) {
                                String clientLogoInfo = jsonObjectData.getString("clientLogoInfo");
                                //解析设备 logo 信息
                                parsenerClientLogoInfo(clientLogoInfo);
                            }
                            if (data.contains("clientVolLst")) {
                                String clientVolLst = jsonObjectData.getString("clientVolLst");
                                //解析定时音量
                                parsenerMediaVoiceNum(clientVolLst);
                            }
                            //背景图保存到集合
                            saveBggImageToList(clientDiyLst, taskServiceView);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /***
     * 解析logo信息
     * @param clientLogoInfo
     */
    private void parsenerClientLogoInfo(String clientLogoInfo) {
        DbBggImageUtil.clearLogoImageInfo();
        if (clientLogoInfo == null || clientLogoInfo.length() < 5) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(clientLogoInfo);
            String fileType = jsonObject.getString("type");
            long fileSize = jsonObject.getLong("logoSize");
            String imagePath = jsonObject.getString("logoUrl");
            String savePath = AppInfo.APP_LOGO_PATH();
            String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            BggImageEntity bgglogo = new BggImageEntity(fileType, fileSize, imagePath, savePath, imageName, BggImageEntity.STYPE_LOGO_IMAGE);
            MyLog.bgg("======获取得背景logo信息==" + bgglogo.toString());
            DbBggImageUtil.addBggInfoToDb(bgglogo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parsenerMediaVoiceNum(String clientVolLst) {
        DbDevMedia.clearMediaVoiceInfo();
        if (clientVolLst == null || clientVolLst.length() < 5) {
            backMediaVoiceToView("clientVolLst == null");
            return;
        }
        try {
            JSONArray jsonArray = new JSONArray(clientVolLst);
            int mediaNum = jsonArray.length();
            if (mediaNum < 1) {
                backMediaVoiceToView("jsonArray.length() < 1 ");
                return;
            }
            for (int i = 0; i < mediaNum; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String startTime = jsonObject.getString("startTime");
                startTime = startTime.replace(":", "");
                String endTime = jsonObject.getString("endTime");
                endTime = endTime.replace(":", "");
                String volume = jsonObject.getString("volume");
                ControlMediaVoice controlMediaVoice = new ControlMediaVoice(startTime, endTime, volume);
                MyLog.bgg("======保存数据库===" + controlMediaVoice.toString());
                DbDevMedia.addMediaVoiceInfoToDb(controlMediaVoice);
            }
            backMediaVoiceToView("解析完毕: 返回给界面View ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void backMediaVoiceToView(String desc) {
        AppStatuesListener.getInstance().UpdateMainMediaVoiceEvent.postValue(desc);
    }

    /***
     * 解析背景图信息
     * @param clientDiyLst
     * @param taskServiceView
     */
    private void saveBggImageToList(String clientDiyLst, TcpServiceView taskServiceView) {
        if (clientDiyLst == null || clientDiyLst.length() < 5) {
            DbBggImageUtil.clearBggImageInfo();
            backSuccessBggImageInfo("当前用户清除了背景图", taskServiceView);
            return;
        }
        MyLog.bgg("======获取得背景信息==" + clientDiyLst);
        try {
            JSONArray jsonArray = new JSONArray(clientDiyLst);
            int numSize = jsonArray.length();
            if (numSize < 1) {
                backSuccessBggImageInfo("当前没有背景图信息", taskServiceView);
                return;
            }
            DbBggImageUtil.clearBggImageInfo();
            for (int i = 0; i < numSize; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String fileSizeString = jsonObject.getString("fileSize");
                String type = jsonObject.getString("type");
                long fileSize = Long.parseLong(fileSizeString);
                String diyUrl = jsonObject.getString("diyUrl");
                String fileName = diyUrl.substring(diyUrl.lastIndexOf("/") + 1);
                BggImageEntity bggImageEntit = new BggImageEntity(type, fileSize, diyUrl, AppInfo.BASE_BGG_IMAGE(), fileName, BggImageEntity.STYPE_BGG_IMAGE);
                MyLog.bgg("======获取得背景信息==" + bggImageEntit.toString());
                DbBggImageUtil.addBggInfoToDb(bggImageEntit);
            }
            backSuccessBggImageInfo("解析加载图片成功", taskServiceView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void backSuccessBggImageInfo(String desc, TcpServiceView taskServiceView) {
        List<BggImageEntity> bggImageLists = DbBggImageUtil.getBggImageDownListFromDb();
        taskServiceView.backSuccessBggImageInfo(bggImageLists, desc);
    }

    /***
     * 获取定时开关机任务===========================================================================================
     */
    @Override
    public void getPowerOnOffTask(final TaskChangeListener listener) {
        String devId = CodeUtil.getUniquePsuedoID();
        String requestUrl = ApiInfo.POWERONOFF_QUERY() + "?clNo=" + devId;
        OkHttpUtils
                .get()
                .url(requestUrl)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, String errorDesc, int id) {
                        MyLog.netty(errorDesc);
                        backListener(false, null, errorDesc, listener);
                    }

                    @Override
                    public void onResponse(String json, int id) {
                        MyLog.powerOnOff("获取定时开关机信息：" + json, true);
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            int code = jsonObject.getInt("code");
                            String errorrDesc = jsonObject.getString("msg");
                            if (code == 0) {
                                String data = jsonObject.getString("data");
                                parsenerTimerTask(data, listener);
                            } else {
                                backListener(false, null, errorrDesc, listener);
                            }
                        } catch (Exception e) {
                            MyLog.powerOnOff("====定时开关机解析异常：" + e.toString(), true);
                        }
                    }
                });
    }

    /**
     * 解析定时开关机数据
     */
    private void parsenerTimerTask(String jsonTimer, TaskChangeListener listener) {
        if (jsonTimer == null || jsonTimer.length() < 5) {
            backListener(false, null, "解析data 为 null", listener);
            return;
        }
        if (!jsonTimer.contains("TimedTaskList")) {
            backListener(false, null, "JSON不包含定时数据", listener);
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonTimer);
            String timeListJson = jsonObject.getString("TimedTaskList");
            JSONArray jsonArray = new JSONArray(timeListJson);
            int timerNum = jsonArray.length();
            if (timerNum < 1) {
                backListener(false, null, "当前没有定时数据", listener);
                return;
            }
            PowerOnOffEntity powerOnOffEntity = new PowerOnOffEntity();
            List<TimedTaskListEntity> timeList = new ArrayList<TimedTaskListEntity>();
            for (int i = 0; i < timerNum; i++) {
                JSONObject jsonTime = jsonArray.getJSONObject(i);
                String id = jsonTime.getString("id");
                String ttOnTime = jsonTime.getString("ttOnTime");
                String ttOffTime = jsonTime.getString("ttOffTime");
                String ttMon = jsonTime.getString("ttMon");
                String ttTue = jsonTime.getString("ttTue");
                String ttWed = jsonTime.getString("ttWed");
                String ttThu = jsonTime.getString("ttThu");
                String ttFri = jsonTime.getString("ttFri");
                String ttSat = jsonTime.getString("ttSat");
                String ttSun = jsonTime.getString("ttSun");
                TimedTaskListEntity timedTaskListEntity = new TimedTaskListEntity(id, ttOnTime, ttOffTime, ttMon, ttTue, ttWed, ttThu, ttFri, ttSat, ttSun);
                timeList.add(timedTaskListEntity);
            }
            powerOnOffEntity.setTimedTaskList(timeList);
            backListener(true, powerOnOffEntity, "解析成功", listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 联网获取sd卡设置的内存阈值
     */
    @Override
    public void getSystemSettingInfoTCP() {
        String requestUrl = ApiInfo.getSdManagerCheckUrl();
        OkHttpUtils
                .post()
                .url(requestUrl)
                .addParams("userName", SharedPerManager.getUserName())
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, String errorDesc, int id) {
                        MyLog.netty("===========getSystemSettingInfo============" + errorDesc);
                    }

                    @Override
                    public void onResponse(String json, int id) {
                        MyLog.netty("===========getSystemSettingInfo============" + json);
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            int code = jsonObject.getInt("code");
                            if (code == 0) {
                                String data = jsonObject.getString("data");
                                JSONObject jsonData = new JSONObject(data);
                                int riLevel = Integer.parseInt(jsonData.getString("riLevel"));
                                String riSpeedLimit = jsonData.getString("riSpeedLimit");  //下载速度
                                String riDownNumLimit = jsonData.getString("riDownNumLimit"); //下载台数限制
                                if (riDownNumLimit == null || riDownNumLimit.length() < 1) {
                                    riDownNumLimit = "200";
                                }
                                if (riSpeedLimit == null || riSpeedLimit.length() < 1) {
                                    riSpeedLimit = "5000";
                                }
                                int limitSpeed = Integer.parseInt(riSpeedLimit);
                                int limitNumLine = Integer.parseInt(riDownNumLimit);
                                SharedPerManager.setLimitDevNum(limitNumLine);
                                SharedPerManager.setLimitSpeed(limitSpeed);
                                SharedPerManager.setSdcardManagerAuthor(riLevel);
//                               SharedPerManager.setSdcardCacheSize(riRam);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /***
     * 上传工作日志
     * 每次联网成功，会上传一次
     */
    @Override
    public void updateWorkInfoTxt() {
        try {
            File fileDel = new File(AppInfo.BASE_CRASH_LOG());
            if (!fileDel.exists()) {
                return;
            }
            File[] filelIST = fileDel.listFiles();
            if (filelIST == null || filelIST.length < 1) {
                return;
            }
            String currentDate = SimpleDateUtil.getCurrentDateLong() + "";
            String updatePath = "";
            for (int i = 0; i < filelIST.length; i++) {
                String filePath = filelIST[i].getPath();
                if (filePath.contains(currentDate)) {
                    updatePath = filePath;
                }
            }
            if (updatePath == null || updatePath.length() < 2) {
                return;
            }
            MyLog.update("===上传文件的路径==" + updatePath);
            String requestUrl = ApiInfo.getUpdateDevInfo();
            UpdateFileRunnable runnable = new UpdateFileRunnable(requestUrl, updatePath);
            EtvService.getInstance().executor(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerDevToWeb(Context context, String userName,
                                 final RegisterDevListener listener) {
        //这个注册方法，SOCKET 多了一个参数
        if (SharedPerUtil.SOCKEY_TYPE() == AppConfig.SOCKEY_TYPE_WEBSOCKET) {
            registerDevToWebWebSocket(context, userName, listener);
        } else if (SharedPerUtil.SOCKEY_TYPE() == AppConfig.SOCKEY_TYPE_SOCKET) {
            registerDevToSocket(context, userName, listener);
        }
    }

    private void registerDevToSocket(Context context, String userName, RegisterDevListener
            listener) {
        MySDCard sdcard = new MySDCard(context);
        String requestUrl = ApiInfo.REGISTER_WEB_TO_DEV();
        MyLog.netty("======设备注册调用===" + requestUrl);
        String clNO = CodeUtil.getUniquePsuedoID();
        String clName = SharedPerManager.getDevNickName();
        String clMac = CodeUtil.getUniquePsuedoID();
        String clIp = CodeUtil.getIpAddress(context, "====设备注册调用===");
        String clLatitude = SharedPerManager.getmLatitude();
        String clLongitude = SharedPerManager.getmLongitude();

        String clScreenNum = ScreenUtil.getScreenNum();  //屏幕得个数
        String clResolution = ScreenUtil.getresolution(); //屏幕得分辨率
        String sdcardPath = Environment.getExternalStorageDirectory().getPath();
        long sizeLast = Long.parseLong(sdcard.getLastSpace(2, sdcardPath));
        sizeLast = sizeLast / 1024 / 1024;
        String clVersion = CodeUtil.getSystCodeVersion(context);
        String clLineState = "1";  //在线状态
        String clAddress = SharedPerManager.getAllAddress();
        OkHttpUtils
                .post()
                .url(requestUrl)
                .addHeader("Accept-Language", "zh")
                .addParams("clNo", clNO)
                .addParams("clName", clName)
                .addParams("clMac", clMac)
                .addParams("linkType", "3")
                .addParams("clLatitude", clLatitude + "")
                .addParams("clLongitude", clLongitude + "")
                .addParams("clIp", clIp)
                .addParams("clScreenNum", clScreenNum)
                .addParams("clResolution", clResolution)
                .addParams("userName", userName)
                .addParams("clDisk", sizeLast + "M")
                .addParams("clLineState", clLineState)
                .addParams("clVersion", clVersion)
                .addParams("clAddress", clAddress)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, String errorrDesc, int id) {
                        listener.registerDevState(false, errorrDesc, -1);
                        MyLog.netty("====注册errorDesc==" + errorrDesc);
                    }

                    @Override
                    public void onResponse(String json, int id) {
                        MyLog.netty("====注册success==" + json, true);
                        try {
                            if (json == null || json.length() < 5) {
                                listener.registerDevState(false, "JSON==NULL", -1);
                                MyLog.netty("====注册success====json=null", true);
                                return;
                            }
                            JSONObject object = new JSONObject(json);
                            int code = object.getInt("code");
                            String msg = object.getString("msg");
                            if (code == 0 || code == 2) {
                                String data = object.optString("data");
                                parsenerDataInfo(data);
                                listener.registerDevState(true, "Success", 0);
                            } else if (code == 1) {  //注册失败
                                listener.registerDevState(false, msg, 2);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void parsenerDataInfo(String data) {
        if (data == null || data.length() < 5) {
            logInfo("====注册success====parsenerDataInfo====");
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(data);
            String resourceServer = jsonObject.getString("resourceServer");
            String socketServer = jsonObject.getString("socketServer");
            if (!TextUtils.isEmpty(resourceServer) && !resourceServer.endsWith("/")) {
                resourceServer = resourceServer + "/";
            }
            SharedPerManager.setResourDownPath(resourceServer);
            logInfo("====注册success====保存资源地址====" + resourceServer);
            if (!socketServer.contains(":")) {
                logInfo("====注册success===心跳地址.数据错误=");
                return;
            }
            String ipAddress = socketServer.substring(0, socketServer.indexOf(":"));
            String port = socketServer.substring(socketServer.indexOf(":") + 1, socketServer.length());
            int portNum = ApiInfo.SOCKET_DEFAULR_PORT;
            try {
                portNum = Integer.parseInt(port);
            } catch (Exception e) {
                e.printStackTrace();
            }
            logInfo("====注册success===心跳地址=" + ipAddress + " / " + port);
            String ipaddressSave = SharedPerManager.getSocketIpAddress();
            int socketPort = SharedPerManager.getSocketPort();
            if (ipAddress.equals(ipaddressSave) && portNum == socketPort) {
                logInfo("====注册success===心跳地址。获取得数据和本地数据保持一致，不用变更本地数据");
                return;
            }
            logInfo("====注册success===保存心跳服务器地址=" + ipAddress + " / " + portNum);
            SharedPerManager.setSocketIpAddress(ipAddress);
            SharedPerManager.setSocketPort(portNum);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logInfo(String s) {
        MyLog.netty(s);
    }

    private void registerDevToWebWebSocket(Context context, String
            userName, RegisterDevListener listener) {
        MySDCard sdcard = new MySDCard(context);
        String requestUrl = ApiInfo.REGISTER_WEB_TO_DEV();
        String clNO = CodeUtil.getUniquePsuedoID();
        String clName = SharedPerManager.getDevNickName();
        String clMac = CodeUtil.getUniquePsuedoID();
        String clIp = CodeUtil.getIpAddress(context, "====设备注册调用===");
        String clLatitude = SharedPerManager.getmLatitude();
        String clLongitude = SharedPerManager.getmLongitude();

        String clScreenNum = ScreenUtil.getScreenNum();  //屏幕得个数
        String clResolution = ScreenUtil.getresolution(); //屏幕得分辨率
        String sdcardPath = Environment.getExternalStorageDirectory().getPath();
        long sizeLast = Long.parseLong(sdcard.getLastSpace(2, sdcardPath));
        sizeLast = sizeLast / 1024 / 1024;
        String clVersion = CodeUtil.getSystCodeVersion(context);
        String clLineState = "1";  //在线状态
        String clAddress = SharedPerManager.getAllAddress();

        OkHttpUtils
                .post()
                .url(requestUrl)
                .addParams("clNo", clNO)
                .addParams("clName", clName)
                .addParams("clMac", clMac)
                .addParams("clLatitude", clLatitude + "")
                .addParams("clLongitude", clLongitude + "")
                .addParams("clIp", clIp)
                .addParams("clScreenNum", clScreenNum)
                .addParams("clResolution", clResolution)
                .addParams("userName", userName)
                .addParams("clDisk", sizeLast + "M")
                .addParams("clLineState", clLineState)
                .addParams("clVersion", clVersion)
                .addParams("clAddress", clAddress)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, String errorrDesc, int id) {
                        listener.registerDevState(false, errorrDesc, -1);
                        MyLog.netty("====注册errorDesc==" + errorrDesc);
                    }

                    @Override
                    public void onResponse(String json, int id) {
                        MyLog.netty("====注册success==" + json, true);
                        try {
                            if (json == null || json.length() < 5) {
                                listener.registerDevState(false, "JSON==NULL", -1);
                                MyLog.netty("====注册success====json=null", true);
                                return;
                            }
                            JSONObject object = new JSONObject(json);
                            int code = object.getInt("code");
                            String msg = object.getString("msg");
                            if (code == 0 || code == 2) {
                                listener.registerDevState(true, "Success", 0);
                            } else if (code == 1) {  //注册失败
                                listener.registerDevState(false, msg, 2);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void getDevHartInfo(final Context context, final TcpServerListener listener) {
        try {
            String clNO = CodeUtil.getUniquePsuedoID();
            String request = ApiInfo.QUERY_DEV_INFO();
            OkHttpUtils
                    .post()
                    .url(request)
                    .addParams("clNo", clNO)
                    .addParams("linkType", "3")
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, String errorDesc, int id) {
                            MyLog.netty("====获取设备信息error===" + errorDesc);
                            listener.getDevHartInfoStatues(false, errorDesc);
                        }

                        @Override
                        public void onResponse(String json, int id) {
                            MyLog.netty("====获取设备信息===" + json);
                            if (json == null || json.length() < 5) {
                                listener.getDevHartInfoStatues(false, "http json is null !");
                                return;
                            }
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                int code = jsonObject.getInt("code");
                                if (code != 0) {
                                    listener.getDevHartInfoStatues(false, "Device registration required");
                                    return;
                                }
                                listener.getDevHartInfoStatues(true, "The device already exists");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 用来区分指令测类型
     *
     * @param tag username   修改用户名字
     *            location   修改定位信息
     *            changeUsername    修改用户归属
     *            default            查询设备信息
     */
    @Override
    public void updateDevInformation(Context context, final String tag) {
        String devId = CodeUtil.getUniquePsuedoID();
        String requestUrl = ApiInfo.QUERY_DEV_INFO();
        MyLog.http("======查询设备信息=" + requestUrl);
        OkHttpUtils
                .post()
                .url(requestUrl)
                .addParams("clNo", devId)
                .addParams("linkType", "3")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, String errorDesc, int id) {
                    }

                    @Override
                    public void onResponse(String json, int id) {
                        MyLog.cdl("获取的设备信息===updateDevInformation==" + json);
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            int code = jsonObject.getInt("code");
                            if (code != 0) {
                                return;
                            }
                            String data = jsonObject.getString("data");
                            JSONObject jsonObjectData = new JSONObject(data);
                            //=====================================
                            String nickName = jsonObjectData.getString("clName");
                            SharedPerManager.setDevNickName(nickName);

                            boolean infoFrom = SharedPerManager.getInfoFrom();
                            if (!infoFrom) {
                                MyLog.cdl("获取的设备信息===用户设置得本地设置，这里中断操作");
                                return;
                            }

                            //设置退出密码
                            if (data.contains("quitPwd")) {  //退出密码
                                String exitPassword = jsonObjectData.getString("quitPwd");
                                SharedPerManager.setExitpassword(exitPassword);
                            }
                            //守护进程得时间
                            if (data.contains("daemonProcessTime")) {
                                String daemonProcessTime = jsonObjectData.getString("daemonProcessTime");
                                GuardianUtil.setGuardianProjectTime(context, daemonProcessTime);
                            }
                            String appId = SharedPerManager.getAuthorId();
                            if (data.contains("appId")) {
                                appId = jsonObjectData.getString("appId");
                            }
                            SharedPerManager.setAuthorId(appId);
                            //双屏异显加载得方式
                            if (data.contains("dualScreenAdapt")) {
                                int dualScreenAdapt = jsonObjectData.getInt("dualScreenAdapt");
                                SharedPerManager.setDoubleScreenMath(dualScreenAdapt);
                            }
                            if (data.contains("spStatisticsPlay")) {
                                //播放统计  0关闭 1开启
                                int spStatisticsPlay = jsonObjectData.getInt("spStatisticsPlay");
                                SharedPerManager.setPlayTotalUpdate(spStatisticsPlay == 1 ? true : false);
                            }
                            if (data.contains("spStatisticsFlow")) {
                                //统计流量  0关闭 1开启
                                int spStatisticsFlow = jsonObjectData.getInt("spStatisticsFlow");
                                SharedPerManager.setIfUpdateTraffToWeb(spStatisticsFlow == 1 ? true : false);
                            }

                            if (tag.contains("location")) {
                                SharedPerManager.setAutoLocation(false);
                            } else if (tag.contains("changeUsername")) {  //修改用户名字
                                //这里需要重启设备了
                                SystemManagerUtil.rebootApp(context);
                            }
                        } catch (Exception e) {
                            MyLog.netty("====定时开关机解析异常000：" + e.toString());
                        }
                    }
                });
    }

    @Override
    public void getProjectFontInfoFromWeb(String id) {
        String requestUrl = ApiInfo.getFontRequestInfo();
        OkHttpUtils
            .get()
            .url(requestUrl)
            .build()
            .execute(new StringCallback() {
                @Override
                public void onError(Call call, String errorDesc, int id) {
                }

                @Override
                public void onResponse(String json, int id) {
                    if (json == null || json.length() < 5) {
                        return;
                    }
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        int code = jsonObject.getInt("code");
                        if (code != 0) {
                            return;
                        }
                        String data = jsonObject.getString("data");
                        if (data == null || data.length() < 5) {
                            //获取得数据==null
                            return;
                        }
                        parsenerFontTextInfo(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
    }


    private void backListener(boolean isSuccess, Object object, String
            errorrDesc, TaskChangeListener listener) {
        if (listener == null) {
            return;
        }
        if (isSuccess) {
            listener.taskRequestSuccess(object, errorrDesc);
        } else {
            listener.requestFailed(errorrDesc);
        }
    }


    /***
     * 监控截图
     * @param context
     */
    @Override
    public void monitorUpdateImage(Context context, String filePath) {
        //先判断是不是3128——4.4的操作系统
        String cpuMudel = CpuModel.getMobileType();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (cpuMudel.contains("rk312x")) {
                deal312844ScreenInfo(context, filePath);
                return;
            }
        }
        //处理其他的屏幕
        dealMonitorZipOrCapture(context, filePath);
    }

    /**
     * 优先处理3128 4.4的
     * 因为系统没有正确的截图方法
     * 会变形，所以需要旋转图片
     * 单独处理
     *
     * @param context
     * @param filePath
     */
    ImageRotateUtil imageRotateUtil;

    private void deal312844ScreenInfo(Context context, String filePath) {
        if (imageRotateUtil == null) {
            imageRotateUtil = new ImageRotateUtil(context);
        }
        imageRotateUtil.rotateBitmap3128(filePath, new WriteBitmapToLocalListener() {
            @Override
            public void writeStatues(boolean isSuccess, String path) {
                MyLog.cdl("===========屏幕的角度===" + isSuccess + " / " + path);
                if (!isSuccess) {
                    return;
                }
                dealMonitorZipOrCapture(context, path);
            }
        });
    }

    private void dealMonitorZipOrCapture(Context context, String filePath) {
        List<ScreenEntity> screenEntityList = EtvApplication.getInstance().getListScreen();
        if (screenEntityList == null || screenEntityList.size() < 1) {
            return;
        }
        Runnable runnable = null;
        int roateNum = SharedPerManager.getCapturequilty();
        float width = SharedPerManager.getScreenWidth();
        float height = SharedPerManager.getScreenHeight();
        int qulity = 100;
        if (roateNum == 0) { //强力压缩
            width = SharedPerManager.getScreenWidth() / 4;
            height = SharedPerManager.getScreenHeight() / 4;
            qulity = 10;
            runnable = new CompressImageRunnable(new File(filePath), width, height, qulity, new CompressImageListener() {
                @Override
                public void backErrorDesc(String desc) {
                    MyLog.update("截图异常==" + desc);
                }

                @Override
                public void backImageSuccess(String oldPath, String imagePath) {
                    updateFileToWeb(imagePath, 1);
                }
            });
        } else if (roateNum == 1) { //性能压缩
            width = SharedPerManager.getScreenWidth() / 3;
            height = SharedPerManager.getScreenHeight() / 3;
            qulity = 50;
            runnable = new CompressImageRunnable(new File(filePath), width, height, qulity, new CompressImageListener() {
                @Override
                public void backErrorDesc(String desc) {
                    MyLog.update("截图异常==" + desc);
                }

                @Override
                public void backImageSuccess(String oldPath, String imagePath) {
                    updateFileToWeb(imagePath, 1);
                }
            });
        } else if (roateNum == 2) { //原图压缩
            runnable = new ZipImageRunnable(context, filePath, new CaptureImageListener() {
                @Override
                public void getCaptureImagePath(boolean isSuucess, String imagePath) {
                    if (isSuucess) {
                        updateFileToWeb(imagePath, 1);
                    } else {
                        MyLog.update("截图异常==");
                    }
                }
            });
        }
        EtvService.getInstance().executor(runnable);
        //单屏任务就不往下操作了
        if (screenEntityList.size() < 2) {
            return;
        }
        MyLog.update("=====双屏截图标记======");
        CaptureRunnable runnableCapture = new CaptureRunnable(context, new CaptureImageListener() {
            @Override
            public void getCaptureImagePath(boolean isSuucess, String imagePath) {
                MyLog.update("=====双屏截图标记======" + isSuucess + " / " + imagePath);
                if (isSuucess) {
                    updateFileToWeb(imagePath, 2);
                } else {
                    MyLog.update("截图异常==");
                }
            }
        });
        EtvService.getInstance().executor(runnableCapture);
    }

    /****
     * 上传文件到服务器
     * @param imagePath
     */
    private void updateFileToWeb(String imagePath, int displayPosition) {
        MyLog.update("===开始上传==" + System.currentTimeMillis());

        File fileTest = new File(imagePath);
        if (fileTest.exists()) {
            MyLog.cdl("=====文件上传 = " + imagePath + " / " + fileTest.length());
        } else {
            MyLog.cdl("=====文件上传 =0000 " + imagePath);
        }

        String code = CodeUtil.getUniquePsuedoID();
        String requestUrl = ApiInfo.MONITOR_IMAGE_UPDATE() + "?clientNo=" + code;
        UpdateFileRunnable runnable = new UpdateFileRunnable(requestUrl, imagePath, UpdateFileRunnable.TAG_IMAGE, new UpdateImageListener() {

            @Override
            public void updateImageFailed(String errorDesc) {
                MyLog.update("===上传失败==" + System.currentTimeMillis());
            }

            @Override
            public void updateImageProgress(int progress) {
                MyLog.update("截图上传progress==" + progress);
            }

            @Override
            public void updateImageSuccess(String desc) {
                MyLog.update("===上传updateImageSuccesss==" + System.currentTimeMillis());
            }
        });
        runnable.setImagePosition(displayPosition);
        EtvService.getInstance().executor(runnable);
    }

    /***
     * 解析字体库
     * @param data
     */
    private void parsenerFontTextInfo(String data) {
        MyLog.db("========字体信息保存状态==json=" + data);
        try {
            JSONArray jsonArray = new JSONArray(data);
            int num = jsonArray.length();
            if (num < 1) {
                return;
            }
            List<FontEntity> localFontSize = DbFontInfo.getFontInfoList();
            if (localFontSize != null && localFontSize.size() > 0) {
                if (localFontSize.size() == num) {
                    MyLog.db("====数据是一样得，不用重复添加=====");
                    return;
                }
            }
            DbFontInfo.clearAllData();
            for (int i = 0; i < num; i++) {
                JSONObject jsonDate = jsonArray.getJSONObject(i);
                int fontId = jsonDate.getInt("id");
                String fontName = jsonDate.getString("fontName");
                long fontSize = jsonDate.getLong("fontSize");
                String downUrl = jsonDate.getString("downUrl");
                String createTime = jsonDate.getString("createTime");
                String downName = jsonDate.getString("downName");
                FontEntity fontEntity = new FontEntity(fontId, fontName, fontSize, downUrl, downName, createTime);
                boolean isSave = DbFontInfo.saveFontInfoToLocal(fontEntity);
                MyLog.db("========字体信息保存状态===" + isSave + "  /" + fontEntity.toString());
            }
        } catch (Exception e) {
            MyLog.db("========字体信息保存状态error===" + e.toString());
            e.printStackTrace();
        }
    }
}
