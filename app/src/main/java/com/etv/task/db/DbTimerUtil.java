package com.etv.task.db;

import com.etv.task.entity.TimerDbEntity;

import org.litepal.LitePal;

import java.util.List;

/**
 * 定时开关机数据库
 */

public class DbTimerUtil {

    public static boolean addTimerDb(TimerDbEntity entity) {
        boolean isSave = false;
        try {
            isSave = entity.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSave;
    }

    public static boolean clearTimeDb() {
        try {
            int delNum = LitePal.deleteAll(TimerDbEntity.class);
            if (delNum > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /***
     * 查询时间数据库
     * @return
     */
    public static List<TimerDbEntity> queryTimerList() {
        List<TimerDbEntity> lists = null;
        try {
            lists = LitePal.findAll(TimerDbEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lists;
    }
}
