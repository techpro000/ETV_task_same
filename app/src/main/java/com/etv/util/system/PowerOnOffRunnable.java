package com.etv.util.system;

import android.content.Context;

import com.etv.db.PowerOnOffLogDb;
import com.etv.entity.PoOnOffLogEntity;
import com.etv.entity.ScheduleRecord;
import com.etv.entity.TimeEntity;
import com.etv.entity.TimedTaskListEntity;
import com.etv.task.db.DbTimerUtil;
import com.etv.task.entity.TimerDbEntity;
import com.etv.util.MyLog;
import com.etv.util.SimpleDateUtil;

import java.util.ArrayList;
import java.util.List;

public class PowerOnOffRunnable implements Runnable {

    List<TimedTaskListEntity> lists;
    Context context;
    List<ScheduleRecord> listPowerOnOff = new ArrayList<ScheduleRecord>();
    String action;

    public static final String TIMER_ACTION_SAVE_POWERONOFF_TIME = "savePowerOnOffTime";
    public static final String TIMER_ACTION_GET_POWERONOFF_TIME = "getPowerOnOffFromDb";
    public static final String TIMER_ACTION_SET_POWERONOFF_TIME = "setPowerOnOffTime";

    public PowerOnOffRunnable(String action, Context context, List<TimedTaskListEntity> lists) {
        this.context = context;
        this.lists = lists;
        this.action = action;
    }

    @Override
    public void run() {
        if (action.equals(TIMER_ACTION_SAVE_POWERONOFF_TIME)) {
            savePowerOnOffTime();
        } else if (action.equals(TIMER_ACTION_GET_POWERONOFF_TIME)) {
            getPowerOnOffFromDb();
        } else if (action.equals(TIMER_ACTION_SET_POWERONOFF_TIME)) {
            setPowerOnOffTime(lists);
        }
    }

    /***
     * 保存数据到本地数据库
     */
    public void savePowerOnOffTime() {
        listPowerOnOff.clear();
        DbTimerUtil.clearTimeDb();
        if (lists.size() < 1) {
            clearPowerOnOffTime();
            MyLog.powerOnOff("获取的定时开关机 < 1 ,用户删除定时开关机任务，终止操作");
            return;
        }
        for (int i = 0; i < lists.size(); i++) {
            TimedTaskListEntity entity = lists.get(i);
            String powerOnTime = entity.getTtOnTime();
            String powerOffTime = entity.getTtOffTime();
            String ttMon = entity.getTtMon();
            String ttTue = entity.getTtTue();
            String ttWed = entity.getTtWed();
            String ttThu = entity.getTtThu();
            String ttFri = entity.getTtFri();
            String ttSat = entity.getTtSat();
            String ttSun = entity.getTtSun();
            TimerDbEntity timerDbEntity = new TimerDbEntity(powerOnTime, powerOffTime, ttMon, ttTue, ttWed, ttThu, ttFri, ttSat, ttSun);
            DbTimerUtil.addTimerDb(timerDbEntity);
        }
        getPowerOnOffFromDb();
    }

    /***
     * 从数据库中获取数据
     */
    public void getPowerOnOffFromDb() {
        MyLog.powerOnOff("======冲本地数据库中查询数", true);
        List<TimerDbEntity> lists = DbTimerUtil.queryTimerList();
        if (lists == null || lists.size() < 1) {
            clearPowerOnOffTime();
            return;
        }
        List<TimedTaskListEntity> entntiList = new ArrayList<TimedTaskListEntity>();
        for (int i = 0; i < lists.size(); i++) {
            TimerDbEntity entity = lists.get(i);
            TimedTaskListEntity timerTaskListEntity = new TimedTaskListEntity();
            timerTaskListEntity.setTtOnTime(entity.getTtOnTime());
            timerTaskListEntity.setTtOffTime(entity.getTtOffTime());
            timerTaskListEntity.setTtMon(entity.getTtMon());
            timerTaskListEntity.setTtTue(entity.getTtTue());
            timerTaskListEntity.setTtWed(entity.getTtWed());
            timerTaskListEntity.setTtThu(entity.getTtThu());
            timerTaskListEntity.setTtFri(entity.getTtFri());
            timerTaskListEntity.setTtSat(entity.getTtSat());
            timerTaskListEntity.setTtSun(entity.getTtSun());
            entntiList.add(timerTaskListEntity);
        }
        setPowerOnOffTime(entntiList);
    }

    /**
     * 设置系统定时开关机
     * 亿晟定时开关机规律
     * 1：分组智能设定一组
     * 2：采取获取当前最近的一组，设定，
     * 3：下次开机重新获取离下次最近的一组
     *
     * @param lists
     */
    public void setPowerOnOffTime(List<TimedTaskListEntity> lists) {
        try {
            listPowerOnOff.clear();
            if (lists.size() < 1) {
                clearPowerOnOffTime();    //===清理定时开关机====
                MyLog.powerOnOff("获取的定时开关机 < 1 ,用户删除定时开关机任务，终止操作", true);
                return;
            }
            for (int i = 0; i < lists.size(); i++) {
                int dayOfWeek = -1;
                TimedTaskListEntity entity = lists.get(i);
                String powerOnTime = entity.getTtOnTime();
                String powerOffTime = entity.getTtOffTime();
                if (powerOnTime.length() < 1 || powerOffTime.length() < 1) {
                    //如果本次的数据为null，跳过本次循环
                    continue;
                }
                MyLog.powerOnOff("=====获取的开机关机时间===" + powerOnTime + " / " + powerOffTime, true);
                int onHour = Integer.parseInt(powerOnTime.substring(0, powerOnTime.indexOf(":")));
                int onMin = Integer.parseInt(powerOnTime.substring(powerOnTime.indexOf(":") + 1, powerOnTime.length()));
                int offHour = Integer.parseInt(powerOffTime.substring(0, powerOffTime.indexOf(":")));
                int offMin = Integer.parseInt(powerOffTime.substring(powerOffTime.indexOf(":") + 1, powerOffTime.length()));
                MyLog.powerOnOff("=====获取的开关机机小时==" + onHour + " / " + onMin + "  /关机时间==" + offHour + " / " + offMin, true);
                boolean ttMon = Boolean.parseBoolean(entity.getTtMon());
                if (ttMon) {
                    dayOfWeek = 2;
                    saveTimerTask(dayOfWeek, onHour, onMin, offHour, offMin);
                }
                boolean ttTue = Boolean.parseBoolean(entity.getTtTue());
                if (ttTue) {
                    dayOfWeek = 3;
                    saveTimerTask(dayOfWeek, onHour, onMin, offHour, offMin);
                }
                boolean ttWed = Boolean.parseBoolean(entity.getTtWed());
                if (ttWed) {
                    dayOfWeek = 4;
                    saveTimerTask(dayOfWeek, onHour, onMin, offHour, offMin);
                }
                boolean ttThu = Boolean.parseBoolean(entity.getTtThu());
                if (ttThu) {
                    dayOfWeek = 5;
                    saveTimerTask(dayOfWeek, onHour, onMin, offHour, offMin);
                }
                boolean ttFri = Boolean.parseBoolean(entity.getTtFri());
                if (ttFri) {
                    dayOfWeek = 6;
                    saveTimerTask(dayOfWeek, onHour, onMin, offHour, offMin);
                }
                boolean ttSat = Boolean.parseBoolean(entity.getTtSat());
                if (ttSat) {
                    dayOfWeek = 7;
                    saveTimerTask(dayOfWeek, onHour, onMin, offHour, offMin);
                }
                boolean ttSun = Boolean.parseBoolean(entity.getTtSun());
                if (ttSun) {
                    dayOfWeek = 1;
                    saveTimerTask(dayOfWeek, onHour, onMin, offHour, offMin);
                }
            }
            long powerOffTime = TSchedule.getLastPowerOnOffTime(listPowerOnOff, false);
            long powerOnTime = TSchedule.getLastPowerOnOffTime(listPowerOnOff, true);
            MyLog.powerOnOff("===获取的关机时间===" + powerOffTime + " /最早的开机时间==" + powerOnTime, true);
            if (powerOffTime < 0 && powerOnTime < 0) {   //这里可能获取时间异常了 ，不设置定时开关机
                MyLog.powerOnOff("===获取的开关机时间异常了，这里清理定时开关机", true);
                clearPowerOnOffTime();
                return;
            }
            if (powerOffTime > 0 && powerOnTime < 0) {
                //这里表示设置的开关时间已经超过一周了，这里需要做一个预判。第二天这个时候开机i一次，然后重新获取
                powerOnTime = TSchedule.jujleNoOnTime(powerOffTime);
            }
            MyLog.powerOnOff("=====最终的时间===" + powerOnTime + " / " + powerOffTime, true);
            long currentTimeSimple = SimpleDateUtil.getCurrentTimelONG(); //獲取當前的時間
            //如果下一次的定时开关机時間大於當前3分鐘，才能設定，不然就區下一次的定時開關機
            if (powerOnTime < powerOffTime && (powerOnTime - currentTimeSimple) > 300) {
                //如果最近的一次开机时间《关机时间，以最近的一次为准.倒退两分钟开机
                long onTimeLong = SimpleDateUtil.StringToLongTimePower(powerOnTime + "");
                long onTimeLongOff = onTimeLong - (1000 * 120);  //关机时间在此时间前减去2分钟再去换算时间
                MyLog.powerOnOff("===onTimeLongOff===" + onTimeLongOff);
                powerOffTime = SimpleDateUtil.formatBig(onTimeLongOff);
                MyLog.powerOnOff("===合理的时间===" + powerOnTime + " / " + powerOffTime);
                timeLongChangeEntitytoSetTime(powerOffTime, powerOnTime);
                return;
            }
            powerOnTime = TSchedule.getLastPowerOnTime(listPowerOnOff, powerOffTime);
            MyLog.powerOnOff("===获取的开机时间===" + powerOnTime);
            if (powerOffTime < 0 || powerOnTime < 0) {
                //这里可能获取时间异常了 ，不设置定时开关机
                MyLog.powerOnOff("===获取的开关机时间异常了，这里清理定时开关机");
                clearPowerOnOffTime();
                return;
            }
            timeLongChangeEntitytoSetTime(powerOffTime, powerOnTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 用来转化时间参数
     * 将long 类型的定时开关机转化成Entity 类型的数据
     *
     * @param powerOffTime
     * @param powerOnTime
     */
    private void timeLongChangeEntitytoSetTime(long powerOffTime, long powerOnTime) {
        MyLog.powerOnOff("===设定的时间===" + powerOnTime + " / " + powerOffTime);
        TimeEntity entityOff = SimpleDateUtil.getFormatLongTime(powerOffTime);
        TimeEntity entityOn = SimpleDateUtil.getFormatLongTime(powerOnTime);
        MyLog.powerOnOff("===设定的开机时间===" + powerOnTime + " / " + entityOn.toString());
        MyLog.powerOnOff("===设定的关机时间===" + powerOffTime + " / " + entityOff.toString());
        setToPowerOnOffTime(entityOn, entityOff);
    }

    private void setToPowerOnOffTime(TimeEntity entityOn, TimeEntity entityOff) {
        String onTimeSave = entityOn.getYear() + "-" + entityOn.getMonth() + "-" + entityOn.getDay() + " " + entityOn.getHour() + ":" + entityOn.getMinite() + ":00";
        String offTimeSave = entityOff.getYear() + "-" + entityOff.getMonth() + "-" + entityOff.getDay() + " " + entityOff.getHour() + ":" + entityOff.getMinite() + ":00";
        String createTime = SimpleDateUtil.formatTaskTimeShow(System.currentTimeMillis());
        MyLog.powerOnOff("保存数据库的时间==" + offTimeSave + " / " + onTimeSave + " / " + createTime);
        PoOnOffLogEntity poOnOffLogEntity = new PoOnOffLogEntity(offTimeSave, onTimeSave, createTime);
        boolean isSave = PowerOnOffLogDb.savePowerOnOffToWeb(poOnOffLogEntity);
        MyLog.powerOnOff("设定的定时开关机数据状态==" + isSave + " /createTime =  " + createTime);
        int[] timeoffArray = new int[]{entityOff.getYear(), entityOff.getMonth(), entityOff.getDay(), entityOff.getHour(), entityOff.getMinite()};
        int[] timeonArray = new int[]{entityOn.getYear(), entityOn.getMonth(), entityOn.getDay(), entityOn.getHour(), entityOn.getMinite()};
        if (context != null) {
            PowerOnOffManager.getInstance(context).setPowerOnOff(timeonArray, timeoffArray);
        }
    }

    private void saveTimerTask(int dayOfWeek, int onHour, int onMin, int offHour, int offMin) {
        ScheduleRecord scheduleRecord = new ScheduleRecord(dayOfWeek, onHour, onMin, offHour, offMin);
//        MyLog.powerOnOff("===设定的定时开关机时间列表==" + scheduleRecord.toString());
        listPowerOnOff.add(scheduleRecord);
    }

    /***
     * 清理定时开关机任务
     */
    public void clearPowerOnOffTime() {
        try {
            //删除数据库中的数据
            if (context != null) {
                PowerOnOffManager.getInstance(context).clearPowerOnOffTime();
            }
        } catch (Exception e) {
            MyLog.powerOnOff("清理定时开关机error =" + e.toString(), true);
        }
    }

}
