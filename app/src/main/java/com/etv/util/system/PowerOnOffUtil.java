package com.etv.util.system;

import android.content.Context;

import com.etv.entity.ScheduleRecord;
import com.etv.entity.TimeEntity;
import com.etv.entity.TimedTaskListEntity;
import com.etv.service.EtvService;
import com.etv.setting.db.TimeLocalEntity;
import com.etv.task.db.DbTimerUtil;
import com.etv.task.entity.TimerDbEntity;
import com.etv.util.MyLog;
import com.etv.util.SimpleDateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * 定时开关机管理类
 * 业务逻辑如下
 * 1：查询网络服务器用户设定的定时开关机时间
 * 网络查询失败，也从本地去获取
 * 2：保存用户设定的到本地数据库
 * 3：获取本地数据库数据
 * 4：去设定定时开关机时间
 */
public class PowerOnOffUtil {

    Context context;
    private Calendar c;
    PowerOnOffManager manager;

    public PowerOnOffUtil(Context context) {
        this.context = context;
        c = Calendar.getInstance();
        manager = PowerOnOffManager.getInstance(context);
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
    }

    /***
     * 根据工作模式来设定定时开关机
     */
    public void changePowerOnOffByWorkModel(String printTag) {
        MyLog.powerOnOff("修改定时开关机时间: " + printTag);
        clearPowerOnOffTime();
        List<TimerDbEntity> lists = DbTimerUtil.queryTimerList();
        setWebTimer(lists);
    }

    /***
     * 设置网络模式的定时开关机
     * @param lists
     */
    private void setWebTimer(List<TimerDbEntity> lists) {
        if (lists == null || lists.size() < 1) {
            return;
        }
        List<TimedTaskListEntity> timeListSet = new ArrayList<>();
        for (int i = 0; i < lists.size(); i++) {
            TimerDbEntity timerDbTentity = lists.get(i);
            String starttTime = timerDbTentity.getTtOnTime();
            String enTime = timerDbTentity.getTtOffTime();
            String mon = timerDbTentity.getTtMon();
            String tue = timerDbTentity.getTtTue();
            String wed = timerDbTentity.getTtWed();
            String thu = timerDbTentity.getTtThu();
            String fri = timerDbTentity.getTtFri();
            String sta = timerDbTentity.getTtSat();
            String sun = timerDbTentity.getTtSun();
            TimedTaskListEntity entity = new TimedTaskListEntity("-1", starttTime, enTime, mon, tue, wed, thu, fri, sta, sun);
            timeListSet.add(entity);
        }
        setPowerOnOffTime(timeListSet);
    }


    /***
     * 设置单机定时开关机
     * @param lists
     */
    private void setLocalTimer(List<TimeLocalEntity> lists) {
        if (lists == null || lists.size() < 1) {
            clearPowerOnOffTime();
            return;
        }
        List<TimedTaskListEntity> timeListSet = new ArrayList<>();
        for (int i = 0; i < lists.size(); i++) {
            TimeLocalEntity timerDbTentity = lists.get(i);
            String starttTime = timerDbTentity.getTtOnTime();
            String enTime = timerDbTentity.getTtOffTime();
            String mon = timerDbTentity.getTtMon();
            String tue = timerDbTentity.getTtTue();
            String wed = timerDbTentity.getTtWed();
            String thu = timerDbTentity.getTtThu();
            String fri = timerDbTentity.getTtFri();
            String sta = timerDbTentity.getTtSat();
            String sun = timerDbTentity.getTtSun();
            TimedTaskListEntity entity = new TimedTaskListEntity("-1", starttTime, enTime, mon, tue, wed, thu, fri, sta, sun);
            timeListSet.add(entity);
        }
        setPowerOnOffTime(timeListSet);
    }

    private void setPowerOnOffTime(List<TimedTaskListEntity> timeListSet) {
        PowerOnOffRunnable runnable = new PowerOnOffRunnable(PowerOnOffRunnable.TIMER_ACTION_SET_POWERONOFF_TIME, context, timeListSet);
        EtvService.getInstance().executor(runnable);
    }

    /***
     * 清理定时开关机任务
     */
    public void clearPowerOnOffTime() {
        try {
            //删除数据库中的数据
            manager.clearPowerOnOffTime();
        } catch (Exception e) {
            MyLog.powerOnOff("清理定时开关机error =" + e.toString(), true);
        }
    }

    /***
     * 获取定时开机时间
     * @return
     */
    public static String getPowerOnTime(Context context) {
        PowerOnOffManager powerOnOffManager = PowerOnOffManager.getInstance(context);
        String time = powerOnOffManager.getPowerOnTime();
        return time;
    }

    /***
     * 获取关机的时间
     * @param context
     * @return
     */
    public static String getPowerOffTime(Context context) {
        PowerOnOffManager powerOnOffManager = PowerOnOffManager.getInstance(context);
        String time = powerOnOffManager.getPowerOffTime();
        return time;
    }

    /***
     * 把星期 开关机时间转化成时间戳形式
     * @param timerList
     * @return
     */
    public static List<ScheduleRecord> getPowerDateLocalList(List<TimeLocalEntity> timerList) {
        List<ScheduleRecord> listBack = new ArrayList<ScheduleRecord>();
        try {
            if (timerList == null || timerList.size() < 1) {
                return listBack;
            }
            List<TimedTaskListEntity> timeListSet = new ArrayList<>();
            for (int i = 0; i < timerList.size(); i++) {
                TimeLocalEntity timerDbTentity = timerList.get(i);
                String starttTime = timerDbTentity.getTtOnTime();
                String enTime = timerDbTentity.getTtOffTime();
                String mon = timerDbTentity.getTtMon();
                String tue = timerDbTentity.getTtTue();
                String wed = timerDbTentity.getTtWed();
                String thu = timerDbTentity.getTtThu();
                String fri = timerDbTentity.getTtFri();
                String sta = timerDbTentity.getTtSat();
                String sun = timerDbTentity.getTtSun();
                TimedTaskListEntity entity = new TimedTaskListEntity("-1", starttTime, enTime, mon, tue, wed, thu, fri, sta, sun);
                timeListSet.add(entity);
            }
            listBack = addTimerInfoToList(timeListSet);
        } catch (Exception E) {
            E.printStackTrace();
        }
        return listBack;
    }

    public static List<ScheduleRecord> getPowerDateNetList(List<TimerDbEntity> timerList) {
        List<ScheduleRecord> listBack = new ArrayList<ScheduleRecord>();
        try {
            if (timerList == null || timerList.size() < 1) {
                return listBack;
            }
            List<TimedTaskListEntity> timeListSet = new ArrayList<>();
            for (int i = 0; i < timerList.size(); i++) {
                TimerDbEntity timerDbTentity = timerList.get(i);
                String starttTime = timerDbTentity.getTtOnTime();
                String enTime = timerDbTentity.getTtOffTime();
                String mon = timerDbTentity.getTtMon();
                String tue = timerDbTentity.getTtTue();
                String wed = timerDbTentity.getTtWed();
                String thu = timerDbTentity.getTtThu();
                String fri = timerDbTentity.getTtFri();
                String sta = timerDbTentity.getTtSat();
                String sun = timerDbTentity.getTtSun();
                TimedTaskListEntity entity = new TimedTaskListEntity("-1", starttTime, enTime, mon, tue, wed, thu, fri, sta, sun);
                timeListSet.add(entity);
            }
            listBack = addTimerInfoToList(timeListSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listBack;
    }

    private static List<ScheduleRecord> addTimerInfoToList(List<TimedTaskListEntity> timeListSet) {
        List<ScheduleRecord> listBack = new ArrayList<ScheduleRecord>();
        try {
            for (int i = 0; i < timeListSet.size(); i++) {
                int dayOfWeek = -1;
                TimedTaskListEntity entity = timeListSet.get(i);
                String powerOnTime = entity.getTtOnTime();
                String powerOffTime = entity.getTtOffTime();
                if (powerOnTime.length() < 1 || powerOffTime.length() < 1) { //如果本次的数据为null，跳过本次循环
                    continue;
                }
                int onHour = Integer.parseInt(powerOnTime.substring(0, powerOnTime.indexOf(":")));
                int onMin = Integer.parseInt(powerOnTime.substring(powerOnTime.indexOf(":") + 1, powerOnTime.length()));
                int offHour = Integer.parseInt(powerOffTime.substring(0, powerOffTime.indexOf(":")));
                int offMin = Integer.parseInt(powerOffTime.substring(powerOffTime.indexOf(":") + 1, powerOffTime.length()));
                boolean ttMon = Boolean.parseBoolean(entity.getTtMon());
                if (ttMon) {
                    dayOfWeek = 2;
                    ScheduleRecord scheduleRecord = new ScheduleRecord(dayOfWeek, onHour, onMin, offHour, offMin);
                    listBack.add(scheduleRecord);
                }
                boolean ttTue = Boolean.parseBoolean(entity.getTtTue());
                if (ttTue) {
                    dayOfWeek = 3;
                    ScheduleRecord scheduleRecord = new ScheduleRecord(dayOfWeek, onHour, onMin, offHour, offMin);
                    listBack.add(scheduleRecord);
                }
                boolean ttWed = Boolean.parseBoolean(entity.getTtWed());
                if (ttWed) {
                    dayOfWeek = 4;
                    ScheduleRecord scheduleRecord = new ScheduleRecord(dayOfWeek, onHour, onMin, offHour, offMin);
                    listBack.add(scheduleRecord);
                }
                boolean ttThu = Boolean.parseBoolean(entity.getTtThu());
                if (ttThu) {
                    dayOfWeek = 5;
                    ScheduleRecord scheduleRecord = new ScheduleRecord(dayOfWeek, onHour, onMin, offHour, offMin);
                    listBack.add(scheduleRecord);
                }
                boolean ttFri = Boolean.parseBoolean(entity.getTtFri());
                if (ttFri) {
                    dayOfWeek = 6;
                    ScheduleRecord scheduleRecord = new ScheduleRecord(dayOfWeek, onHour, onMin, offHour, offMin);
                    listBack.add(scheduleRecord);
                }
                boolean ttSat = Boolean.parseBoolean(entity.getTtSat());
                if (ttSat) {
                    dayOfWeek = 7;
                    ScheduleRecord scheduleRecord = new ScheduleRecord(dayOfWeek, onHour, onMin, offHour, offMin);
                    listBack.add(scheduleRecord);
                }
                boolean ttSun = Boolean.parseBoolean(entity.getTtSun());
                if (ttSun) {
                    dayOfWeek = 1;
                    ScheduleRecord scheduleRecord = new ScheduleRecord(dayOfWeek, onHour, onMin, offHour, offMin);
                    listBack.add(scheduleRecord);
                }
            }
            if (listBack == null || listBack.size() < 1) {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parsenerTimerListSch(listBack);
    }

    /***
     * @param listSch
     * 定时开关机列表
     * on   表示开机时间
     * off  表示关机时间
     * @return
     */
    private static List<ScheduleRecord> parsenerTimerListSch(List<ScheduleRecord> listSch) {
        if (listSch == null || listSch.size() < 1) {
            return null;
        }
        List<ScheduleRecord> listBack = new ArrayList<ScheduleRecord>();
        try {
            Calendar calendar = Calendar.getInstance();
            int currentDayWeek = calendar.get(Calendar.DAY_OF_WEEK);  //今天的工作星期
            Long currentTimeMillis = System.currentTimeMillis();
            long simpleTime = SimpleDateUtil.formatBig(currentTimeMillis);
            //以今天基准，遍历所有日期，补全所有的时间数据
            TimeEntity entity = SimpleDateUtil.getFormatLongTime(simpleTime);
            for (int i = 0; i < listSch.size(); i++) {
                ScheduleRecord scheduleRecord = listSch.get(i);
                int weekList = scheduleRecord.getDayOfWeek();
                int distanceDay = 0;
                int year = entity.getYear();
                int month = entity.getMonth();
                int day = entity.getDay();

                if (weekList < currentDayWeek) {
                    distanceDay = 7 - currentDayWeek + weekList;
                } else {  //星期属大于今天
                    distanceDay = weekList - currentDayWeek;
                }
                day = day + distanceDay;
                if (month == 12 && day > 31) {  //跨年了
                    year = year + 1;
                    month = 1;
                    day = 1;
                } else if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {   //判断31天月份
                    if (day > 31) {
                        day = day - 31;
                        month = month + 1;
                    }
                } else if (month == 4 || month == 6 || month == 9 || month == 11) {           //判断30天的月份
                    if (day > 30) {
                        day = day - 30;
                        month = month + 1;
                    }
                } else if (month == 2) {                            //判断二月
                    if (year % 4 == 0 && year % 100 != 0) {//闰年
                        if (day > 29) {
                            day = day - 29;
                            month = month + 1;
                        }
                    } else if (year % 400 == 0) { //闰年
                        if (day > 29) {
                            day = day - 29;
                            month = month + 1;
                        }
                    } else {  //平年
                        if (day > 28) {
                            day = day - 28;
                            month = month + 1;
                        }
                    }
                }
                scheduleRecord.setYear(year);
                scheduleRecord.setMonth(month);
                scheduleRecord.setDay(day);
                listBack.add(scheduleRecord);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listBack;
    }

    public void savePowerOnOffTime(List<TimedTaskListEntity> timedTaskList) {
        PowerOnOffRunnable runnable = new PowerOnOffRunnable(PowerOnOffRunnable.TIMER_ACTION_SAVE_POWERONOFF_TIME, context, timedTaskList);
        EtvService.getInstance().executor(runnable);
    }

    public void getPowerOnOffFromDb() {
        PowerOnOffRunnable runnable = new PowerOnOffRunnable(PowerOnOffRunnable.TIMER_ACTION_GET_POWERONOFF_TIME, context, null);
        EtvService.getInstance().executor(runnable);
    }

}
