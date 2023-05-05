package com.etv.http;

import android.content.Intent;
import android.text.TextUtils;

import com.EtvApplication;
import com.etv.config.AppInfo;
import com.etv.db.DbBggImageUtil;
import com.etv.db.DbDevMedia;
import com.etv.entity.BggImageEntity;
import com.etv.entity.ControlMediaVoice;
import com.etv.entity.PowerOnOffEntity;
import com.etv.entity.TimedTaskListEntity;
import com.etv.service.listener.SysSettingAllInfoListener;
import com.etv.util.MyLog;
import com.etv.util.SharedPerManager;
import com.etv.util.guardian.GuardianUtil;
import com.etv.util.rxjava.AppStatuesListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PersenerDevAllInfoRunnable implements Runnable {

    String dataInfo;
    SysSettingAllInfoListener settingAllInfoListener;

    public PersenerDevAllInfoRunnable(String dataInfo, SysSettingAllInfoListener settingAllInfoListene) {
        this.dataInfo = dataInfo;
        this.settingAllInfoListener = settingAllInfoListene;
    }

    @Override
    public void run() {
        if (dataInfo == null || TextUtils.isEmpty(dataInfo)) {
            return;
        }
        parsenerData();
    }

    /***
     * 解析所有得数据
     */
    private void parsenerData() {
        try {
            JSONObject jsonObject = new JSONObject(dataInfo);
            String ramInfo = jsonObject.getString("ramInfo");
            parsenerRamInfo(ramInfo);
            String timedTask = jsonObject.getString("timedTask");
            parsenerPowerOnOffInfo(timedTask);
            String systemTime = jsonObject.getString("systemTime");
            setLocalTimerFromWeb(systemTime);
            String bgLogoVol = jsonObject.getString("bgLogoVol");
            parsenerBgLogoVolInfo(bgLogoVol);
            String systemSet = jsonObject.optString("systemSet");  //设备设置信息
            logInfo("===解析设备===设置信息000==" + systemSet);
            parsenerSytemSettingInfo(systemSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 解析背景。logo.声音信息
     * @param bgLogoVol
     */
    private void parsenerBgLogoVolInfo(String bgLogoVol) {
        if (bgLogoVol == null || bgLogoVol.length() < 3) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(bgLogoVol);
            String nickName = jsonObject.optString("clName");
            SharedPerManager.setDevNickName(nickName);  //设备昵称
            String speedLimit = jsonObject.optString("speedLimit");  //下载速度
            if (speedLimit == null || speedLimit.length() < 1) {
                speedLimit = "5000";
            }
            int speedSave = Integer.parseInt(speedLimit);
            SharedPerManager.setLimitSpeed(speedSave);
            String clientDiyLst = jsonObject.optString("clientDiyLst");  //背景图
            parsenerClientDiyLst(clientDiyLst);  //设置背景图
            String clientVolLst = jsonObject.optString("clientVolLst");  //区间音量
            parsenerClienVolList(clientVolLst);
            String clientLogoInfo = jsonObject.optString("clientLogoInfo");  //logo 信息
            parsenerClientLogoInfo(clientLogoInfo);   //logo 信息
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parsenerSytemSettingInfo(String systemSet) {
        if (systemSet == null || systemSet.length() < 5) {
            logInfo("===解析设备===没有设置信息==");
            return;
        }
        logInfo("===解析设备===设置信息==" + systemSet);
        try {
            JSONObject jsonObjectData = new JSONObject(systemSet);
            String nickName = jsonObjectData.getString("clName");
            SharedPerManager.setDevNickName(nickName);
            if (systemSet.contains("appId")) {
                String appId = jsonObjectData.getString("appId");
                SharedPerManager.setAuthorId(appId);
                logInfo("===解析设备===设置信息=appId=" + appId);
            }
            boolean infoFrom = SharedPerManager.getInfoFrom();
            if (!infoFrom) {
                logInfo("===解析设备===设置信息=用户设置得本地设置，这里中断操作=");
                return;
            }
            if (systemSet.contains("quitPwd")) {  //退出密码
                String exitPassword = jsonObjectData.getString("quitPwd");
                SharedPerManager.setExitpassword(exitPassword);
                logInfo("===解析设备===设置信息=quitPwd=" + exitPassword);
            }
            if (systemSet.contains("daemonProcessTime")) {
                String daemonProcessTime = jsonObjectData.getString("daemonProcessTime");
                GuardianUtil.setGuardianProjectTime(EtvApplication.getInstance(), daemonProcessTime);
                logInfo("===解析设备===设置信息=daemonProcessTime=" + daemonProcessTime);
            }
            //双屏异显加载得方式
            if (systemSet.contains("dualScreenAdapt")) {
                int dualScreenAdapt = jsonObjectData.getInt("dualScreenAdapt");
                SharedPerManager.setDoubleScreenMath(dualScreenAdapt);
            }
            if (systemSet.contains("spStatisticsPlay")) {
                //播放统计  0关闭 1开启
                int spStatisticsPlay = jsonObjectData.getInt("spStatisticsPlay");
                SharedPerManager.setPlayTotalUpdate(spStatisticsPlay == 1 ? true : false);
            }
        } catch (Exception e) {
            logInfo("====解析设备===设置信息==解析异常000：" + e.toString());
        }
    }

    /***
     * 解析logo信息
     * @param clientLogoInfo
     */
    private void parsenerClientLogoInfo(String clientLogoInfo) {
        if (clientLogoInfo == null || clientLogoInfo.length() < 5) {
            logInfo("===解析设备===没有logo信息==");
            return;
        }
        logInfo("===解析设备===解析 logo 信息 ==");
        DbBggImageUtil.clearLogoImageInfo();
        try {
            JSONObject jsonObject = new JSONObject(clientLogoInfo);
            String fileType = jsonObject.getString("type");
            long fileSize = jsonObject.getLong("logoSize");
            String imagePath = jsonObject.getString("logoUrl");
            String savePath = AppInfo.APP_LOGO_PATH();
            String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            BggImageEntity bgglogo = new BggImageEntity(fileType, fileSize, imagePath, savePath, imageName, BggImageEntity.STYPE_LOGO_IMAGE);
            logInfo("===解析设备===获取得logo信息==" + bgglogo.toString());
            DbBggImageUtil.addBggInfoToDb(bgglogo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 解析区间音量
     * @param clientVolLst
     */
    private void parsenerClienVolList(String clientVolLst) {
        if (clientVolLst == null || clientVolLst.length() < 5) {
            logInfo("====解析设备==区间音量==clientVolLst==null");
            backZeroVoiceToView("解析设备==区间音量==clientVolLst==null");
            return;
        }
        DbDevMedia.clearMediaVoiceInfo();
        try {
            JSONArray jsonArray = new JSONArray(clientVolLst);
            int mediaNum = jsonArray.length();
            if (mediaNum < 1) {
                backZeroVoiceToView("====解析设备==区间音量==没有数据");
                logInfo("====解析设备==区间音量==没有数据");
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
                logInfo("====解析设备==区间音量====保存数据库===" + controlMediaVoice.toString());
                DbDevMedia.addMediaVoiceInfoToDb(controlMediaVoice);
            }
            backZeroVoiceToView("====数据解析完毕");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void backZeroVoiceToView(String s) {
        AppStatuesListener.getInstance().UpdateMainMediaVoiceEvent.postValue(s);
    }

    /***
     *解析背景图
     * @param clientDiyLst
     */
    private void parsenerClientDiyLst(String clientDiyLst) {
        if (clientDiyLst == null || clientDiyLst.length() < 5) {
            DbBggImageUtil.clearBggImageInfo();
            backSuccessBggImageInfo("当前用户清除了背景图");
            return;
        }
        logInfo("====解析设备==获取得背景信息==" + clientDiyLst);
        try {
            JSONArray jsonArray = new JSONArray(clientDiyLst);
            int numSize = jsonArray.length();
            if (numSize < 1) {
                backSuccessBggImageInfo("当前没有背景图信息");
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
                logInfo("====解析设备==获取得背景信息==" + bggImageEntit.toString());
                DbBggImageUtil.addBggInfoToDb(bggImageEntit);
            }
            backSuccessBggImageInfo("解析加载图片成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 解析完背景图，这里需要返回数据
     * @param errorDesc
     */
    private void backSuccessBggImageInfo(String errorDesc) {
        List<BggImageEntity> bggImageLists = DbBggImageUtil.getBggImageDownListFromDb();
        if (settingAllInfoListener != null) {
            settingAllInfoListener.backSuccessBggImageInfo(bggImageLists, errorDesc);
        }
    }

    private void setLocalTimerFromWeb(String times) {
//        boolean isSwitchTimeFromWeb = SharedPerManager.getUpdateTimeFromWeb();
//        if (!isSwitchTimeFromWeb) {
//            MyLog.cdl("====同步本地时间=runnbale==开关未打开，拦截操作=");
//            SystemManagerInstance.getInstance(EtvApplication.getContext()).switchAutoTime(true, "用户关闭了同步服务器时间");
//            AppInfo.updateLocalTime = true;
//            return;
//        }
//        if (AppInfo.updateLocalTime) {
//            return;
//        }
//        if (times == null || times.length() < 3) {
//            return;
//        }
//        logInfo("====解析设备==服务器时间==" + times);
//        try {
//            int year = Integer.parseInt(times.substring(0, 4));
//            int month = Integer.parseInt(times.substring(4, 6));
//            int day = Integer.parseInt(times.substring(6, 8));
//            int hour = Integer.parseInt(times.substring(8, 10));
//            int minute = Integer.parseInt(times.substring(10, 12));
//            sendMyBroadcastWithLongExtra("com.ys.update_time", "current_time", TimeUtils.getTimeMills(year, month, day, hour, minute, 0));
//            AppStatuesListener.getInstance().timeChangeEvent.postValue(times);
//            logInfo("======解析设备=====服务器时间====发通知给界面===");
//            AppInfo.updateLocalTime = true;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


    /***
     * 解析定时开关机
     * @param timedTask
     */
    private void parsenerPowerOnOffInfo(String timedTask) {
        logInfo("====解析设备==定时开关机 timedTask =" + timedTask);
        if (timedTask == null || timedTask.length() < 3) {
            settingAllInfoListener.backPowerOnOffTimeInfo(false, null, "No Time Data Info !");
            return;
        }
        try {
            JSONArray jsonArray = new JSONArray(timedTask);
            int timerNum = jsonArray.length();
            if (timerNum < 1) {
                logInfo("====解析设备==定时开关机 timerNum <  0 =");
                settingAllInfoListener.backPowerOnOffTimeInfo(false, null, "No Time Data Info ! < 1");
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
            settingAllInfoListener.backPowerOnOffTimeInfo(true, powerOnOffEntity, "Save PowerOnOff Success ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 解析内存信息
     * 下载限速
     * 清理等级
     * @param ramInfo
     */
    private void parsenerRamInfo(String ramInfo) {
        if (ramInfo == null || ramInfo.length() < 3) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(ramInfo);

            int riLevel = Integer.parseInt(jsonObject.getString("riLevel"));
            String riSpeedLimit = jsonObject.getString("riSpeedLimit");  //下载速度
            String riDownNumLimit = jsonObject.getString("riDownNumLimit"); //下载台数限制
            if (riDownNumLimit == null || riDownNumLimit.length() < 1) {
                riDownNumLimit = "200";
            }
            if (riSpeedLimit == null || riSpeedLimit.length() < 1) {
                riSpeedLimit = "5000";
            }
            int limitNumLine = Integer.parseInt(riDownNumLimit);
            SharedPerManager.setLimitDevNum(limitNumLine);
            int limitSpeed = Integer.parseInt(riSpeedLimit);
            SharedPerManager.setLimitSpeed(limitSpeed);
            SharedPerManager.setSdcardManagerAuthor(riLevel);
            logInfo("====解析设备==下载限速=" + riSpeedLimit + " / 清理等级==" + riLevel + " /同时下载台数： " + riDownNumLimit);
        } catch (
            Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMyBroadcastWithLongExtra(String action, String key, long value) {
        try {
            Intent intent = new Intent();
            intent.setAction(action);
            intent.putExtra(key, value);
            EtvApplication.getInstance().sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logInfo(String desc) {
        MyLog.cdl(desc);
    }

}
