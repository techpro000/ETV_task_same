package com.etv.util.poweronoff.db;

import android.content.ContentValues;

import com.etv.util.MyLog;
import com.etv.util.poweronoff.entity.PoOnOffLogEntity;
import com.etv.util.poweronoff.entity.TimerDbEntity;

import org.litepal.LitePal;

import java.util.List;

public class PowerDbManager {

    public static boolean clearTimeDb(String printTag) {
        MyLog.powerOnOff("清理数据库: " + printTag, true);
        int delNum = LitePal.deleteAll(TimerDbEntity.class);
        if (delNum > 0) {
            return true;
        }
        return false;
    }

    public static boolean addTimerDb(TimerDbEntity entity) {
        return entity.save();
    }

    /***
     * 查询时间数据库
     * @return
     */
    public static List<TimerDbEntity> queryTimerList() {
        List<TimerDbEntity> lists = LitePal.findAll(TimerDbEntity.class);
        return lists;
    }

    /***
     * 删除定时开关机
     * @param timerId
     */
    public static boolean delTimerById(String timerId) {
        int delInfo = LitePal.deleteAll(TimerDbEntity.class, "timeId=?", timerId);
        if (delInfo > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static TimerDbEntity getTimeById(String timeId) {
        List<TimerDbEntity> timerDbEntityList = LitePal.where("timeId=?", timeId).find(TimerDbEntity.class);
        if (timerDbEntityList == null || timerDbEntityList.size() < 1) {
            return null;
        }
        return timerDbEntityList.get(0);
    }

    public static boolean modifyTimeById(TimerDbEntity timeDbEntity) {
        boolean isModify = false;
        try {
            ContentValues values = new ContentValues();
            String timerId = timeDbEntity.getTimneId();
            String OnTime = timeDbEntity.getTtOnTime();
            String offTime = timeDbEntity.getTtOffTime();
            String mon = timeDbEntity.getTtMon();
            String tue = timeDbEntity.getTtTue();
            String wed = timeDbEntity.getTtWed();
            String thu = timeDbEntity.getTtThu();
            String fri = timeDbEntity.getTtFri();
            String sat = timeDbEntity.getTtSat();
            String sun = timeDbEntity.getTtSun();
            values.put("ttOnTime", OnTime);
            values.put("ttOffTime", offTime);
            values.put("ttMon", mon);
            values.put("ttTue", tue);
            values.put("ttWed", wed);
            values.put("ttThu", thu);
            values.put("ttFri", fri);
            values.put("ttSat", sat);
            values.put("ttSun", sun);
            int updateNum = LitePal.updateAll(TimerDbEntity.class, values, "timeId = ?", timerId);
            if (updateNum > 0) {
                isModify = true;
            }
        } catch (Exception e) {
        }
        return isModify;
    }

    //============================================================================================

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

    public static void clearAllPowerData() {
        LitePal.deleteAll(PoOnOffLogEntity.class);
    }

}
