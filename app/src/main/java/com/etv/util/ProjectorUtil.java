package com.etv.util;

import android.content.Context;

import com.etv.config.AppInfo;
import com.etv.service.EtvService;
import com.etv.util.sdcard.MySDCard;

import java.util.List;

public class ProjectorUtil {

    /**
     * 设置保存路径
     *
     * @param context
     */
    public static void setProjectorSavePath(final Context context, String tag) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                setProjector(context, tag);
            }
        };
        EtvService.getInstance().executor(runnable);
    }

    /**
     * 设置存储设备信息
     *
     * @param context
     */
    private static void setProjector(Context context, String tag) {
        MyLog.cdl("准备切换素材存储目录==" + tag, true);
        try {
            MySDCard mySDCard = new MySDCard(context);
            List<String> saveList = mySDCard.getAllExternalStorage();
            if (saveList == null || saveList.size() < 2) {  //只有一个存储设备
                setProSavePath(AppInfo.BASE_PATH_INNER);
                return;
            }
            String sdPath = MySDCard.getSDcardPath(context);            //外置SD卡
            if (sdPath == null || sdPath.length() < 6) {
                setProSavePath(AppInfo.BASE_PATH_INNER);
                return;
            }
            MyLog.e("sdcard", "=======" + sdPath);
            boolean ifHasSdcard = false;
            for (int i = 0; i < saveList.size(); i++) {
                String pathSave = saveList.get(i);
                MyLog.e("sdcard", "=======便利" + pathSave + "  sdpath==" + sdPath);
                if (sdPath.contains(pathSave)) {
                    ifHasSdcard = true;
                }
            }
            if (ifHasSdcard) {   //挂置了SD卡
                setProSavePath(sdPath);
            } else {  //没有检测到SD卡
                setProSavePath(AppInfo.BASE_PATH_INNER);
            }
        } catch (Exception e) {
            MyLog.e("sdcard", "=======" + e.toString());
            e.printStackTrace();
        }
    }


    /**
     * 保存节目lujing
     *
     * @param savePath
     */
    private static void setProSavePath(String savePath) {
        SharedPerManager.setBaseSdPath(savePath);
    }

}
