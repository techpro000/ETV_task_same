package com.etv.util;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.EtvApplication;
import com.etv.config.AppInfo;
import com.etv.entity.ScreenEntity;
import com.etv.listener.BitmapCaptureListener;
import com.etv.util.guardian.GuardianUtil;
import com.etv.util.rxjava.AppStatuesListener;
import com.etv.util.system.CpuModel;
import com.etv.util.system.SystemManagerInstance;

import java.util.List;

public class ScreenUtil {

    /**
     * 智能截图
     *
     * @param context
     * @param tag
     */
    public static void getScreenImage(Context context, String tag, BitmapCaptureListener listener) {
        int screenWidth = SharedPerManager.getScreenWidth();
        int screenHeight = SharedPerManager.getScreenHeight();
        String cpuMudel = CpuModel.getMobileType();
        if (cpuMudel.startsWith(CpuModel.CPU_MODEL_RK_3128)) {
            //3128 截图
            deal312844ScreenInfo(context, tag);
            return;
        }
        if (cpuMudel.startsWith(CpuModel.CPU_RK_3566)
            || cpuMudel.startsWith(CpuModel.CPU_MODEL_3568_11)) {
            //3566软件截图
            get356xCatptureImage(context, listener);
            return;
        }
        //旧版本使用守护进程截图
        GuardianUtil.getCaptureImage(context, screenWidth, screenHeight, tag);
    }

    private static void get356xCatptureImage(Context context, BitmapCaptureListener listener) {
        FileUtil.creatPathNotExcit("开始截图");
        MyLog.update("=截图=3566=开始截图==");
        boolean isSuccess = SystemManagerInstance.getInstance(context).screenShot(AppInfo.CAPTURE_MAIN);
        listener.backCaptureImage(isSuccess, AppInfo.CAPTURE_MAIN);
    }

    private static void deal312844ScreenInfo(Context context, String tag) {
        int cacheWidth = SharedPerManager.getScreenWidth();
        int cacheHeight = SharedPerManager.getScreenHeight();
        int screenWidth = cacheWidth;
        int screenHeight = cacheHeight;
        if (cacheWidth > cacheHeight) {
            screenWidth = cacheWidth;
            screenHeight = cacheHeight;
        } else {
            screenWidth = cacheHeight;
            screenHeight = cacheWidth;
        }
        GuardianUtil.getCaptureImage(context, screenWidth, screenHeight, tag);
    }

    public static String getScreenNum() {
        String screenNum = "1";
        List<ScreenEntity> screenEntityList = EtvApplication.getInstance().getListScreen();
        if (screenEntityList == null || screenEntityList.size() < 1) {
            screenNum = "1";
        } else {
            screenNum = screenEntityList.size() + "";
        }
        return screenNum;
    }

    public static String getresolution() {
        StringBuilder screenNumSize = new StringBuilder();
        List<ScreenEntity> screenEntityList = EtvApplication.getInstance().getListScreen();
        if (screenEntityList != null && screenEntityList.size() > 0) {
            for (int i = 0; i < screenEntityList.size(); i++) {
                ScreenEntity screenEntity = screenEntityList.get(i);
                int width = screenEntity.getScreenWidth();
                int height = screenEntity.getScreenHeight();
                if (i != 0) {
                    screenNumSize.append("(" + width + "x" + height + ")");
                } else {
                    screenNumSize.append(width + "x" + height);
                }
            }
        }
        return screenNumSize.toString();
    }


}
