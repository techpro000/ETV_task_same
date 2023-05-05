package com.etv.service.util;

import android.content.Context;
import android.content.Intent;

import com.etv.activity.InitViewActivity;
import com.etv.activity.SplashLowActivity;
import com.etv.activity.StartActivity;
import com.etv.activity.sdcheck.SdCheckActivity;
import com.etv.config.ApiInfo;
import com.etv.config.AppConfig;
import com.etv.config.AppInfo;
import com.etv.db.DbStatiscs;
import com.etv.entity.StorageInfo;
import com.etv.util.system.CpuModel;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.etv.setting.StorageActivity;
import com.ys.model.util.ActivityCollector;
import com.etv.util.CodeUtil;
import com.etv.util.MyLog;
import com.etv.util.NetWorkUtils;
import com.etv.util.ScreenUtil;
import com.etv.util.SharedPerManager;
import com.etv.util.guardian.GuardianUtil;
import com.etv.util.sdcard.FileFilter;
import com.etv.util.sdcard.MySDCard;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;

public class EtvServerModuleImpl implements EtvServerModule {

    @Override
    public void upodateDevInfoToAuthorServer(String clVersion) {
        String requestUrl = ApiInfo.getUpdateDevToAuthorServer();
        String mac = CodeUtil.getUniquePsuedoID();
        String address = SharedPerManager.getAllAddress();
        String getmLongitude = SharedPerManager.getmLongitude();
        String getmLatitude = SharedPerManager.getmLatitude();
        OkHttpUtils
                .post()
                .url(requestUrl)
                .addParams("mac", mac)
                .addParams("address", address)
                .addParams("version", clVersion)
                .addParams("longitude", getmLongitude)
                .addParams("latitude", getmLatitude)
                .addParams("useApp", "ETV")
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, String errorDesc, int id) {
                        MyLog.http("提交设备信息Author failed=" + errorDesc);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        MyLog.http("提交设备信息Author success=" + response);
                    }
                });
    }

    /**
     * 查询设备信息
     */
    @Override
    public void queryDeviceInfoFromWeb(Context context, TaskServiceView taskServiceView) {
        if (!AppConfig.isOnline) {
            taskServiceView.getDevInfoFromWeb(false, "设备不在线");
            return;
        }
        String devId = CodeUtil.getUniquePsuedoID();
        String requestUrl = ApiInfo.QUERY_DEV_INFO();
        OkHttpUtils
                .post()
                .url(requestUrl)
                .addParams("clNo", devId)
                .addParams("linkType", "3")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, String errorDesc, int id) {
                        taskServiceView.getDevInfoFromWeb(false, errorDesc);
                        MyLog.cdl("获取的设备信息,错误信息===" + errorDesc);
                    }

                    @Override
                    public void onResponse(String json, int id) {
                        MyLog.cdl("获取的设备信息===queryDeviceInfoFromWeb==" + json);
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            int code = jsonObject.getInt("code");
                            String msg = jsonObject.getString("msg");
                            if (code != 0) {
                                taskServiceView.getDevInfoFromWeb(false, msg);
                                return;
                            }
                            String data = jsonObject.getString("data");
                            JSONObject jsonObjectData = new JSONObject(data);
                            String nickName = jsonObjectData.getString("clName");
                            SharedPerManager.setDevNickName(nickName);

                            boolean infoFrom = SharedPerManager.getInfoFrom();
                            if (!infoFrom) {
                                MyLog.cdl("获取的设备信息===用户设置得本地设置，这里中断操作");
                                return;
                            }

                            if (data.contains("quitPwd")) {  //退出密码
                                String exitPassword = jsonObjectData.getString("quitPwd");
                                SharedPerManager.setExitpassword(exitPassword);
                            }
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
                            taskServiceView.getDevInfoFromWeb(true, "Request Success");
                        } catch (Exception e) {
                            MyLog.http("====queryDeviceInfoFromWeb==解析异常000：" + e.toString());
                        }
                    }
                });
    }


    @Override
    /***
     * 修改信息给服务器
     * @param context
     */
    public void updateDevInfoToWeb(Context context, String tag) {
        try {
            if (!AppConfig.isOnline) {
                return;
            }
            if (!NetWorkUtils.isNetworkConnected(context)) { //网络未连接
                return;
            }
            String clLatitude = SharedPerManager.getmLatitude();
            String clLongitude = SharedPerManager.getmLongitude();
            String url = ApiInfo.UPDATE_DEV_INFO();
            String clNo = CodeUtil.getUniquePsuedoID();
            MySDCard mySDCard = new MySDCard(context);
            String clMac = CodeUtil.getEthMAC();
            String clScreenNum = ScreenUtil.getScreenNum();  //屏幕得个数
            String clResolution = ScreenUtil.getresolution(); //屏幕得分辨率
            MyLog.http("更新设备信息=" + clScreenNum + " / " + clResolution);
            String clIp = CodeUtil.getIpAddress(context, tag);
            String sdcardPath = AppInfo.BASE_SD_PATH();
            long sizeLast = mySDCard.getAvailableExternalMemorySize(sdcardPath, 1024 * 1024);
            String sysCodeVersion = CodeUtil.getSystCodeVersion(context);
            String address = SharedPerManager.getAllAddress();   //获取设备详细地址
            MyLog.http("提交设备sysCodeVersion信息=" + sysCodeVersion);
            OkHttpUtils
                    .post()
                    .url(url)
                    .addParams("clNo", clNo)
                    .addParams("clDisk", sizeLast + "M")
                    .addParams("clMac", clMac)
                    .addParams("clScreenNum", clScreenNum)
                    .addParams("clResolution", clResolution)
                    .addParams("clLatitude", clLatitude + "")
                    .addParams("clLongitude", clLongitude + "")
                    .addParams("clAddress", address)
                    .addParams("clName", SharedPerManager.getDevNickName())
                    .addParams("clIp", clIp)
                    .addParams("clSystemVersion", sysCodeVersion + "")
                    .build()
                    .execute(new StringCallback() {

                        @Override
                        public void onError(Call call, String errorDesc, int id) {
                            MyLog.http("提交设备信息failed=" + errorDesc);
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            MyLog.http("提交设备信息success=" + response);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addFileNumTotal(String midId, String addType, int pmTime, int count, String timeUpdate) {
        String requestUrl = ApiInfo.ADD_FILE_NUMBERS_TOTAL();
        String devCode = CodeUtil.getUniquePsuedoID();
        OkHttpUtils
                .post()
                .url(requestUrl)
                .addParams("stMtId", midId + "")                         //素材ID
                .addParams("stAtId", addType)                            //素材类型
                .addParams("stClientNo", devCode)                        //设备编号
                .addParams("stTimes", pmTime + "")                       //素材的时间
                .addParams("stCount", count + "")                       //素材的时间
                .addParams("stOwner", SharedPerManager.getDevOnwer())    //设备归属
                .addParams("stPhone", SharedPerManager.getUserName())  //拥有者电话
                .addParams("stProvince", SharedPerManager.getProvince())  //省份
                .addParams("stCity", SharedPerManager.getLocalCiti())     //市区
                .addParams("stArea", SharedPerManager.getArea())     //区域
                .addParams("stMark", "官方备注，没有争议")
                .addParams("dateStr", timeUpdate)
                .addParams("isStats", "1")          //新参数没有实际意义，用来区分新旧版本
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, String errorDesc, int id) {
                        MyLog.update("=====添加统计requestFailed=00=" + errorDesc);
                    }

                    @Override
                    public void onResponse(String json, int id) {
                        MyLog.update("=====添加统计onResponse=00=" + json);
                        try {
                            if (json == null || json.length() < 5) {
                                return;
                            }
                            if (!json.contains("code")) {
                                return;
                            }
                            JSONObject jsonObject = new JSONObject(json);
                            int code = jsonObject.getInt("code");
                            if (code == 0) {
                                boolean isDel = DbStatiscs.delInfoById(midId);
                                MyLog.update("=====删除已经统计得数量==" + isDel);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void deleteEquipmentTaskServer(String taskId) {
//        MyLog.task("=====删除任务===" + taskId);
//        String deviceId = CodeUtil.getUniquePsuedoID();
//        String requestUrl = ApiInfo.DEL_TASK_REQUEST_URL();
//        OkHttpUtils
//                .post()
//                .url(requestUrl)
//                .addParams("eid", taskId + "")
//                .addParams("cid", deviceId)
//                .build()
//                .execute(new StringCallback() {
//
//                    @Override
//                    public void onError(Call call, String errorDesc, int id) {
//                        MyLog.task("====删除任务返失败==" + errorDesc);
//                    }
//
//                    @Override
//                    public void onResponse(String response, int id) {
//                        MyLog.task("====删除任务返回的参数==" + response);
//                    }
//                });
    }

    @Override
    public void SDorUSBcheckIn(Context context, String sdUsbPath) {
        try {
            List<String> listName = new ArrayList<>();
            listName.add(StartActivity.class.getName());
            listName.add(SplashLowActivity.class.getName());
            listName.add(InitViewActivity.class.getName());
            listName.add(StorageActivity.class.getName());
            if (ActivityCollector.isForegroundList(context, listName)) {
                MyLog.usb("界面在前端，终止操作");
                return;
            }
            if (!CpuModel.getMobileType().startsWith(CpuModel.CPU_MODEL_3568_11)) {
                MySDCard mySDCard = new MySDCard(context);
                List<String> list = mySDCard.getAllExternalStorage();  //获取所有的磁盘路径
                if (list.size() < 2 || list == null) {
                    MyLog.usb("当前没有插入SD卡，USB设备");
                    return;
                }
            }
            StorageInfo storageInfo = FileFilter.jujleFileIsExict(context, sdUsbPath);
            if (storageInfo == null) {
                createEtvMediaFileDir(context, sdUsbPath);
                MyLog.usb("获取的设备==null");
                return;
            }
            String isHasFile = storageInfo.getPath();
            if (isHasFile == null || isHasFile.length() < 2) {
                MyLog.usb("isHasFile==null");
                return;
            }
            MyLog.usb("=======外置存储卡是否有当前文件====" + isHasFile);
            Intent intent = new Intent(context, SdCheckActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(SdCheckActivity.TAG_CHECK_URL, sdUsbPath);
            context.startActivity(intent);
        } catch (Exception e) {
            MyLog.usb("======准备执行SD卡操作error=====" + e.toString());
            e.printStackTrace();
        }
    }

    /***
     * 创建etvMedia
     */
    private void createEtvMediaFileDir(Context context, String path) {
//        try {
//            List<String> listsPath = new ArrayList<String>();
//            String basePath = path + "/etv-media";
//            listsPath.add(basePath);
//            listsPath.add(basePath + "/main");     //sdcard/etv/single/main
//            listsPath.add(basePath + "/main/1");   //sdcard/etv/single/main/1
//            listsPath.add(basePath + "/main/2");   //sdcard/etv/single/main/2
//            listsPath.add(basePath + "/main/3");   //sdcard/etv/single/main/3
//            listsPath.add(basePath + "/main/4");   //sdcard/etv/single/main/5
//            listsPath.add(basePath + "/double");  //sdcard/etv/single/double
//            listsPath.add(basePath + "/double/1");   //sdcard/etv/single/double/1
//            listsPath.add(basePath + "/double/2");   //sdcard/etv/single/double/2
//            listsPath.add(basePath + "/double/3");   //sdcard/etv/single/double/3
//            listsPath.add(basePath + "/double/4");   //sdcard/etv/single/double/4
//            for (int i = 0; i < listsPath.size(); i++) {
//                String filePath = listsPath.get(i).toString();
//                File file = new File(filePath);
//                if (!file.exists()) {
//                    boolean isCreate = file.mkdirs();
//                    Log.e("cdl", "==========" + isCreate + "/" + filePath);
//                }
//            }
//            MyToastView.getInstance().Toast(context, "创建单机文件夹成功,请拔U盘");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


    /**
     * 提交Apk.img的上传进度
     *
     * @param percent
     * @param downKb
     * @param fileName
     */
    @Override
    public void updateDownApkImgProgress(int percent, int downKb, String fileName) {
        String devId = CodeUtil.getUniquePsuedoID();
        String requestUrl = ApiInfo.getUpdateApkProgress();
        OkHttpUtils
                .post()
                .url(requestUrl)
                .addParams("clNo", devId)
                .addParams("percent", percent + "")
                .addParams("downKb", downKb + "")
                .addParams("fileName", fileName)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, String errorDesc, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                    }
                });
    }

    /***
     *
     * @param taskId
     * -1 未完成
     * 1  完成
     * @param progress
     * @param type
     * 1  更新进度
     * -1 进入界面的时候加判断
     */
    @Override
    public void updateProgressToWebRegister(String taskId, String titalDoanNum, int progress, int downKb, String type) {
        try {
            String state = "-1";
            if (progress > 99) {
                state = "1";
                downKb = 0;
            } else {
                state = "-1";
            }
            updateProgressDownTask(taskId, titalDoanNum, progress, state, downKb, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateProgressDownTask(String taskId, String titalDoanNum, int progress, String state, int downKb, String type) {
        try {
            if (taskId == null || taskId.length() < 2) {
                return;
            }
            if (!AppConfig.isOnline) {
                MyLog.test("设备未链接，中止更新进度");
                return;
            }
            String requestUrl = ApiInfo.UPDATE_DOWN_PROGRESS();
            String devId = CodeUtil.getUniquePsuedoID();
            int randomNum = new Random().nextInt(10);
            downKb = downKb + randomNum;
            OkHttpUtils
                    .post()
                    .url(requestUrl)
                    .addParams("clNo", devId + "")
                    .addParams("etId", taskId + "")
                    .addParams("percentProg", progress + "")
                    .addParams("downKb", downKb + "")
                    .addParams("state", state)
                    .addParams("userName", SharedPerManager.getUserName())
                    .addParams("finishFileNum", "0")
                    .addParams("totalFileNum", titalDoanNum)
                    .addParams("type", type)
                    .build()
                    .execute(new StringCallback() {

                        @Override
                        public void onError(Call call, String errorDesc, int id) {
                            MyLog.update("========上传进度失败===" + errorDesc);
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            MyLog.update("========上传进度===" + response);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
