package com.etv.setting.db;

import android.content.ContentValues;

import org.litepal.LitePal;

import java.util.List;

/**
 * 本地定时开关机定时开关机数据库
 */

public class DbTimerLocalUtil {

    public static boolean clearTimeDb() {
        List<TimeLocalEntity> list = queryTimerList();
        if (list == null || list.size() < 1) {
            return true;
        }
        int delNum = LitePal.deleteAll(TimeLocalEntity.class);
        if (delNum > 0) {
            return true;
        }
        return false;
    }

    public static boolean addTimerDb(TimeLocalEntity entity) {
        boolean isSave = false;
        try {
            isSave = entity.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSave;
    }

    /***
     * 查询时间数据库
     * @return
     */
    public static List<TimeLocalEntity> queryTimerList() {
        List<TimeLocalEntity> lists = null;
        try {
            lists = LitePal.findAll(TimeLocalEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lists;
    }

    /***
     * 删除定时开关机
     * @param timerId
     */
    public static boolean delTimerById(long timerId) {
        int delInfo = LitePal.delete(TimeLocalEntity.class, timerId);
        if (delInfo > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static TimeLocalEntity getTimeById(long timeId) {
        TimeLocalEntity enrity = null;
        try {
            enrity = LitePal.find(TimeLocalEntity.class, timeId);
        } catch (Exception e) {
        }
        return enrity;
    }

    public static boolean modifyTimeById(TimeLocalEntity timeDbEntity) {
        boolean isModify = false;
        try {
            ContentValues values = new ContentValues();
            long timerId = timeDbEntity.getId();
            String OnTime = timeDbEntity.getTtOnTime();
            String offTime = timeDbEntity.getTtOffTime();
            String mon = timeDbEntity.getTtMon();
            String tue = timeDbEntity.getTtTue();
            String wed = timeDbEntity.getTtWed();
            String thu = timeDbEntity.getTtThu();
            String fri = timeDbEntity.getTtFri();
            String sat = timeDbEntity.getTtSat();
            String sun = timeDbEntity.getTtSun();
//            values.put("id", timerId);
            values.put("ttOnTime", OnTime);
            values.put("ttOffTime", offTime);
            values.put("ttMon", mon);
            values.put("ttTue", tue);
            values.put("ttWed", wed);
            values.put("ttThu", thu);
            values.put("ttFri", fri);
            values.put("ttSat", sat);
            values.put("ttSun", sun);
            int updateNum = LitePal.update(TimeLocalEntity.class, values, timerId);
            if (updateNum > 0) {
                isModify = true;
            }
        } catch (Exception e) {
        }
        return isModify;
    }
}