package com.etv.db;

import com.etv.entity.ControlMediaVoice;
import com.etv.util.MyLog;
import com.etv.util.SimpleDateUtil;

import org.litepal.LitePal;

import java.util.List;

public class DbDevMedia {

    public static int getCurrentMediaVoice() {
        List<ControlMediaVoice> list = getMediaVoiceNum();
        if (list == null || list.size() < 1) {
            return -1;
        }
        int currenTime = SimpleDateUtil.getHourMin();
        for (int i = 0; i < list.size(); i++) {
            ControlMediaVoice controlMediaVoice = list.get(i);
            int starTime = Integer.parseInt(controlMediaVoice.getStartTime());
            int endTime = Integer.parseInt(controlMediaVoice.getEndTime());
            int backVoiceNum = Integer.parseInt(controlMediaVoice.getVolume());
            MyLog.cdl("====保存数据库===" + starTime + " / " + currenTime + " / " + endTime);
            if (starTime < endTime) {
                if (starTime < currenTime && currenTime < endTime) {
                    MyLog.cdl("====保存数据库===跳出循环==000");
                    return backVoiceNum;
                }
            } else {
                if ((currenTime > starTime && currenTime < 235959) || (currenTime > 0 && currenTime < endTime)) {
                    MyLog.cdl("====保存数据库===跳出循环==111");
                    return backVoiceNum;
                }
            }
        }
        return -1;
    }

    /**
     * 保存数据到数据库
     *
     * @param entity
     * @return
     */
    public static boolean addMediaVoiceInfoToDb(ControlMediaVoice entity) {
        boolean isSave = false;
        if (entity == null) {
            return isSave;
        }
        try {
            isSave = entity.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSave;
    }

    public static List<ControlMediaVoice> getMediaVoiceNum() {
        List<ControlMediaVoice> txtList = null;
        try {
            txtList = LitePal.findAll(ControlMediaVoice.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return txtList;
    }


    public static void clearMediaVoiceInfo() {
        LitePal.deleteAll(ControlMediaVoice.class);
    }


}
