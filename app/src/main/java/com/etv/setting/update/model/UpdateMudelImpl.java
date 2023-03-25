package com.etv.setting.update.model;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import com.etv.config.ApiInfo;
import com.etv.entity.AppInfomation;
import com.etv.setting.SystemApkInstallActivity;
import com.etv.setting.update.entity.UpdateInfo;
import com.etv.util.CodeUtil;
import com.etv.util.MyLog;
import com.etv.util.NetWorkUtils;
import com.etv.util.PackgeUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class UpdateMudelImpl implements UpdateMudel {

    List<AppInfomation> appList = new ArrayList<>();
    List<UpdateInfo> listUpdateWeb = new ArrayList<>();  //用来封装需要下载升级的
    List<UpdateInfo> listCache = new ArrayList<>();      //用来封装需要下载升级的

    UpdateInfoListener listener;
    Context context;

    @Override
    public void getUpdateInfo(Context context, final UpdateInfoListener listener) {
        this.listener = listener;
        this.context = context;
        PackgeUtil.getPackage(context, new PackgeUtil.PackageListener() {

            @Override
            public void getSuccess(ArrayList<AppInfomation> list) {
                appList = list;
                if (appList.size() < 1) {
                    overApp("获取本地软件信息失败");
                    return;
                }
                requestUpdate();
            }

            @Override
            public void getFail(String error) {
                overApp("获取本地软件信息失败:" + error);
            }
        });
    }

    private void requestUpdate() {
        if (!NetWorkUtils.isNetworkConnected(context)) {
            overApp("当前无网络");
            return;
        }
        String requestUrl = ApiInfo.UPDATE_APP_SYSTEM();
        String clNo = CodeUtil.getUniquePsuedoID();
        OkHttpUtils
                .post()
                .url(requestUrl)
                .addParams("clNo", clNo + "")
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, String errorDesc, int id) {
                        MyLog.update(errorDesc);
                        overApp("请求失败:" + errorDesc);
                    }

                    @Override
                    public void onResponse(String json, int id) {
                        MyLog.update("检测升级success==" + json);
                        parsenerUpdateInfo(json);
                    }
                });
    }

    private void parsenerUpdateInfo(String json) {
        if (json == null) {
            overApp("请求升级信息==null:" + json);
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(json);
            int code = jsonObject.getInt("code");
            String msg = jsonObject.getString("msg");
            if (code != 0) {
                overApp(msg);
                return;
            }
            String data = jsonObject.getString("data");
            MyLog.update("====升级信息data==" + data);
            parsenerUpdateInfoData(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parsenerUpdateInfoData(String data) {
        listUpdateWeb.clear();
        try {
            JSONObject jsonData = new JSONObject(data);
            String serverUrl = jsonData.getString("serverUrl");
            JSONArray jsonArray = jsonData.getJSONArray("file");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectSon = (JSONObject) jsonArray.opt(i);
                String ufOgname = jsonObjectSon.getString("ufOgName");
                String ufPackageName = jsonObjectSon.getString("ufPackageName");
                int ufVersion = jsonObjectSon.getInt("ufVersion");
                String ufSysVerson = jsonObjectSon.getString("ufSysVersion");
                long ufSize = jsonObjectSon.getLong("ufSize");
                String ufSaveUrl = jsonObjectSon.getString("ufSaveUrl");
                int ufState = jsonObjectSon.getInt("ufState");
                UpdateInfo updateInfo = new UpdateInfo(ufOgname, ufPackageName, ufVersion, ufSysVerson, ufSize, ufSaveUrl, ufState, serverUrl);
                MyLog.update("====添加的升级信息==" + updateInfo.toString());
                listUpdateWeb.add(updateInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        matchUpdateInfo();
    }

    private void matchUpdateInfo() {
        listCache.clear();
        if (listUpdateWeb.size() < 1) {
            overApp("没有需要升级的软件");
            return;
        }
        for (int i = 0; i < listUpdateWeb.size(); i++) {
            UpdateInfo updateInfo = listUpdateWeb.get(i);
            String ufOgname = updateInfo.getUfOgname();
            MyLog.update("=======升级的名字===" + ufOgname);
            if (ufOgname.endsWith(".apk")) {
                matchApkInfo(updateInfo);
            } else if (ufOgname.endsWith(".img") || ufOgname.endsWith(".zip")) {
                matchSystemImg(updateInfo);
            }
        }
        listener.getUpdateInfoSuccess(listCache);
    }

    /**
     * 匹配系统版本号
     *
     * @param updateInfo
     */
    private void matchSystemImg(UpdateInfo updateInfo) {
        String sysCodeLocal = CodeUtil.getImgVersionDate();
        String webCode = updateInfo.getUfSysVerson().trim();
        if (webCode.contains("版本号")) {
            MyLog.update("用户没有设置版本号");
            return;
        }
        MyLog.update("服务器版本号： " + webCode + "  /  本地版本号： " + sysCodeLocal);
        if (TextUtils.isEmpty(webCode) || webCode.length() < 2) {
            MyLog.update("获取版本号失败，不操作");
            return;
        }
        if (sysCodeLocal.contains(webCode)) {  //系统版本相同
            MyLog.update("固件版本相同，不需要升级");
            return;
        }
        String downUrl = updateInfo.getUfSaveUrl();
        int androidCode = Build.VERSION.SDK_INT;
        if (androidCode > Build.VERSION_CODES.M) {  //7.0,固件升级使用 zip
            if (downUrl.endsWith(".img")) {
                return;
            }
        }
        addList(updateInfo);
    }

    /***
     * 匹配APK信息
     * @param updateInfo
     */
    private void matchApkInfo(UpdateInfo updateInfo) {
        boolean isExistence = false;
        String packageName = updateInfo.getUfPackageName();
        MyLog.update("====检测app的包名===" + packageName);
        for (int i = 0; i < appList.size(); i++) {
            AppInfomation appinfomation = appList.get(i);
            String appPackageName = appinfomation.getPackageName();
            int appVersion = appinfomation.getVersionCode();
            if (appPackageName.contains(packageName)) {
                MyLog.update("====检测相同的包名===");
                isExistence = true;
                int webCode = updateInfo.getUfVersion();
                MyLog.update("=======设备安装了该APK， 去检查版本号webCode :" + webCode + " /localVersion= " + appVersion);
                if (webCode > appVersion) {  //服务器版本大于本地版本，去更新下载
                    addList(updateInfo);
                    MyLog.update("=====服务器版本号未最新，需要下载更新");
                }
            }
        }
        if (!isExistence) { //设备没有安装该APP ，去下载安装
            MyLog.update("========设备没有安装该APK 。直接去下载安装");
            addList(updateInfo);
        }
    }

    private void addList(UpdateInfo updateInfo) {
        listCache.add(updateInfo);
    }

    private void overApp(String s) {
        if (listener == null) {
            return;
        }
        listener.overApp(s);
    }

    @Override
    public void installApk(Context context, String savePath, UpdateInfoListener listener) {
        try {
            MyLog.update("APK的安装路径===" + savePath, true);
            Intent intent = new Intent(context, SystemApkInstallActivity.class);
            intent.putExtra(SystemApkInstallActivity.FILE_UPDATE_PATH, savePath);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void installImg(Context context, String savePath, UpdateInfoListener listener) {
        try {
            File file = new File(savePath);
            MyLog.update("===========升级固件的文件是否存在==" + file.exists() + "/" + file.length() + " / " + savePath, true);
            Intent intent = new Intent();
            intent.setAction("android.intent.action.YS_UPDATE_FIRMWARE");
            intent.putExtra("img_path", savePath);
            context.sendBroadcast(intent);
            listener.overApp("准备升级固件");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 如果没有升级信息，就把当前下载的所有的APK进度升级到100%
     */
    @Override
    public void updateOverProgressToWeb() {
        String requestUrl = ApiInfo.UPDATE_APP_SYSTEM_TO_OVER();
        String clNo = CodeUtil.getUniquePsuedoID();
        OkHttpUtils
                .post()
                .url(requestUrl)
                .addParams("clNo", clNo + "")
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, String errorDesc, int id) {
                        MyLog.update("检测升级修改升级进度success==" + errorDesc);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        MyLog.update("检测升级修改升级进度success==" + response);
                    }
                });
    }
}
