package com.etv.util;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.EtvApplication;
import com.etv.config.AppInfo;
import com.etv.entity.ScreenEntity;
import com.etv.util.guardian.GuardianUtil;
import com.etv.util.system.CpuModel;

import java.util.List;

public class ScreenUtil {

    /**
     * 智能截图
     *
     * @param context
     * @param tag
     */
    public static void getScreenImage(Context context, String tag) {
        String cpuMudel = CpuModel.getMobileType();
        //判断是否是4.4的3128
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (cpuMudel.contains("rk312x")) {
                deal312844ScreenInfo(context, tag);
                return;
            }
        }
        if (cpuMudel.contains("smd") || cpuMudel.contains("msm")) {
            deal312844ScreenInfo(context, tag);
            return;
        }
        int screenWidth = SharedPerManager.getScreenWidth();
        int screenHeight = SharedPerManager.getScreenHeight();
        GuardianUtil.getCaptureImage(context, screenWidth, screenHeight, tag);
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
        if (CpuModel.getMobileType().startsWith(CpuModel.CPU_MODEL_MTK_M11)) {
            return getM11Resolution();
        }
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

    /***
     * 获取M11-分辨率
     * @return
     */
    private static String getM11Resolution() {
        int screenWidth = SharedPerManager.getScreenWidth();
        int screenHeight = SharedPerManager.getScreenHeight();
        if (screenWidth == 1280 && screenHeight == 720) {
            screenWidth = 1920;
            screenHeight = 1080;
        }
        if (screenWidth == 720 && screenHeight == 1280) {
            screenWidth = 1080;
            screenHeight = 1920;
        }
        return screenWidth + "x" + screenHeight;
    }

}
