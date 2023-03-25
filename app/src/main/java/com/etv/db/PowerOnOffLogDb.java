package com.etv.db;

import android.content.ContentValues;

import com.etv.entity.PoOnOffLogEntity;
import com.etv.util.MyLog;

import org.litepal.LitePal;

import java.util.List;

/***
 * 用来保存定时开关机Log的文件
 */
public class PowerOnOffLogDb {

    /***
     * 保存控件数据库
     * @param entity
     * @return
     */
    public static boolean savePowerOnOffToWeb(PoOnOffLogEntity entity) {
        if (entity == null) {
            return false;
        }
        String onTime = entity.getOnTime();
        String offTime = entity.getOffTime();
        try {
            List<PoOnOffLogEntity> cpList = LitePal.where("onTime=? and offTime=?", onTime + "", offTime + "").find(PoOnOffLogEntity.class);
            if (cpList == null || cpList.size() < 1) {
                MyLog.db("===powerOnOff====没有数据，添加到数据库");
                return addPowerInfoToDb(entity);
            } else {
                MyLog.db("===powerOnOff====有数据，直接跟保存时间");
                return modifyPowerInfo(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyLog.db("===0000====不知道为什么");
        return false;
    }

    private static boolean modifyPowerInfo(PoOnOffLogEntity entity) {
        if (entity == null) {
            return false;
        }
        MyLog.db("===powerOnOff====修改数据库==");
        String onTime = entity.getOnTime();
        String offTime = entity.getOffTime();
        String createTime = entity.getCreateTime();
        ContentValues values = new ContentValues();
        values.put("createTime", createTime);
        int modifyNum = LitePal.updateAll(PoOnOffLogEntity.class, values, "onTime=? and offTime=?", onTime + "", offTime + "");
        MyLog.db("===powerOnOff====修改数据库statues==" + modifyNum);
        if (modifyNum > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 保存数据到数据库
     *
     * @param entity
     * @return
     */
    public static boolean addPowerInfoToDb(PoOnOffLogEntity entity) {
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


    public static List<PoOnOffLogEntity> getPowerInfoList() {
        List<PoOnOffLogEntity> txtList = null;
        try {
            txtList = LitePal.findAll(PoOnOffLogEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return txtList;
    }

    public static void clearAllData() {
        LitePal.deleteAll(PoOnOffLogEntity.class);
    }

}
