package com.etv.util.system;

import com.etv.entity.ScheduleRecord;
import com.etv.entity.TimeEntity;
import com.etv.task.util.BubbleUtil;
import com.etv.util.MyLog;
import com.etv.util.SimpleDateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/***
 * 此类是用来获取定时开关机
 *的开机时间  和  关机时间
 *
 */
public class TSchedule {

    /***
     * 获取下一次最早的开关机时间
     * @param lists
     * @param isPowerOnOffTime
     * on  下一次最早的开机时间
     * off 下一次最早的关机时间
     * @return
     */
    public static long getLastPowerOnOffTime(List<ScheduleRecord> lists, boolean isPowerOnOffTime) {
        long backTime = -1;
        List<ScheduleRecord> listPowerOff = initTimerTask(lists, isPowerOnOffTime);
        if (listPowerOff.size() < 1 || listPowerOff == null) {
            MyLog.powerOnOff("====getLastPowerOnOffTime==listPowerOff.size==0");
            return backTime;
        }
        long[] powerOffArr = new long[listPowerOff.size()];
        for (int i = 0; i < listPowerOff.size(); i++) {
            ScheduleRecord mScheduleRecord = listPowerOff.get(i);
            long timeToLong = formatTimePowerOn(mScheduleRecord, isPowerOnOffTime);
            powerOffArr[i] = timeToLong;
        }
        powerOffArr = BubbleUtil.Bubblesort(powerOffArr);
        long currentTime = SimpleDateUtil.formatBig(System.currentTimeMillis());
        for (long number : powerOffArr) {
            MyLog.powerOnOff("====遍历纯属的时间==" + number + "   /   " + currentTime);
            if (number > currentTime) {
                backTime = number;
                MyLog.powerOnOff("====遍历纯属的时间=111=" + number + "   /   " + currentTime);
                break;
            }
        }
        MyLog.powerOnOff("====遍历纯属的时间=2222=" + backTime);
        return backTime;
    }

    /***
     * @param lists
     * @param powerOffTime
     * @return
     */
    public static long getLastPowerOnTime(List<ScheduleRecord> lists, long powerOffTime) {
        long backTime = -1;
        List<ScheduleRecord> listPowerOn = initTimerTask(lists, true);
        if (listPowerOn == null || listPowerOn.size() < 1) {
            return backTime;
        }
        long[] powerOnArr = new long[listPowerOn.size()];
        for (int i = 0; i < listPowerOn.size(); i++) {
            ScheduleRecord mScheduleRecord = listPowerOn.get(i);
            long timeToLong = formatTimePowerOn(mScheduleRecord, true);
            powerOnArr[i] = timeToLong;
        }
        powerOnArr = BubbleUtil.Bubblesort(powerOnArr);
        for (long number : powerOnArr) {
            MyLog.powerOnOff("======对比开关机时间：" + number + "/" + powerOffTime);
            if (number > powerOffTime) {
                backTime = number;
                break;
            }
        }
        if (backTime < 0) {  //用来判断，当前一周都没有定时开关机，取23小时，30分的时候，开机一次，下次在刷新
            backTime = jujleNoOnTime(powerOffTime);
        }
        MyLog.powerOnOff("====排序后获取的开机时间==" + backTime);
        return backTime;
    }


    /**
     * 提供一个统一的计算方法
     * 当用户设置的定时开关机时间  大于 7天，就先暂时使用这个时间
     *
     * @param powerOffTime
     * @return
     */
    public static long jujleNoOnTime(long powerOffTime) {
        long backTime = -1;
        long cachePowerOffTime = SimpleDateUtil.StringToLongTimePower(powerOffTime + "");
        long distanceTime = (1000 * 60 * 60 * 24) - (1000 * 60 * 5);
        backTime = SimpleDateUtil.formatBig(cachePowerOffTime + distanceTime);
        return backTime;
    }


    /***
     *
     * @param listSch
     * 定时开关机列表
     * @param isOnOff
     * on   表示开机时间
     * off  表示关机时间
     * @return
     */
    public static List<ScheduleRecord> initTimerTask(List<ScheduleRecord> listSch, boolean isOnOff) {
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
            }
            if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10) {
                if (day > 31) {
                    day = day - 31;
                    month = month + 1;
                }
            } else if (month == 12) {
                if (day > 31) {
                    day = day - 31;
                    month = 1;
                }
            } else if (month == 4 || month == 6 || month == 9 || month == 11) {
                if (day > 30) {
                    day = day - 30;
                    month = month + 1;
                }
            } else if (month == 2) {
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
        }
        return matchPowerTime(listSch, isOnOff);
    }

    private static List<ScheduleRecord> matchPowerTime(List<ScheduleRecord> listSch, boolean isOnOff) {
        List<ScheduleRecord> listPowerOn = new ArrayList<ScheduleRecord>();
        List<ScheduleRecord> listPowerOff = new ArrayList<ScheduleRecord>();
        for (int k = 0; k < listSch.size(); k++) {
            ScheduleRecord schduleRecorder = listSch.get(k);
            int year = schduleRecorder.getYear();
            int month = schduleRecorder.getMonth();
            int day = schduleRecorder.getDay();
            int powerOnTHour = schduleRecorder.getPowerHour();
            int powerOnMin = schduleRecorder.getPowerMinute();

            int powerOffTHour = schduleRecorder.getCloseHour();
            int powerOffMin = schduleRecorder.getCloseMinute();
            String firstHour = "";
            String firstMin = "";
            if (powerOnTHour < 10) {
                firstHour = "0";
            }
            if (powerOnMin < 10) {
                firstMin = "0";
            }
            String endHour = "";
            String endMin = "";
            if (powerOffTHour < 10) {
                endHour = "0";
            }
            if (powerOffMin < 10) {
                endMin = "0";
            }
            String firstMonth = "";
            String firstDay = "";
            if (month < 10) {
                firstMonth = "0";
            }
            if (day < 10) {
                firstDay = "0";
            }
            String onTime = year + firstMonth + month + firstDay + day + firstHour + powerOnTHour + firstMin + powerOnMin + "00";
            String offTime = year + firstMonth + month + firstDay + day + endHour + powerOffTHour + endMin + powerOffMin + "00";

            listPowerOn.add(new ScheduleRecord(year, month, day, schduleRecorder.getDayOfWeek(), schduleRecorder.getPowerHour(), schduleRecorder.getPowerMinute(), 0, 0, ""));
            listPowerOff.add(new ScheduleRecord(year, month, day, schduleRecorder.getDayOfWeek(), 0, 0, schduleRecorder.getCloseHour(), schduleRecorder.getCloseMinute(), ""));
//            MyLog.powerOnOff("===========封装的开机的时间===" + onTime);
//            MyLog.powerOnOff("===========封装的关机的时间===" + offTime);
        }
        if (isOnOff) {
            return listPowerOn;
        } else {
            return listPowerOff;
        }
    }


    /***
     *
     * @param schduleRecorder
     * @param timeOn
     * true 解析开机列表
     * false 解析关机列表
     * @return
     */
    private static long formatTimePowerOn(ScheduleRecord schduleRecorder, boolean timeOn) {
        int year = schduleRecorder.getYear();
        int month = schduleRecorder.getMonth();
        int day = schduleRecorder.getDay();
        String firstMonth = "";
        String firstDay = "";
        String firstHour = "";
        String firstMin = "";
        String onTime = "";
        if (month < 10) {
            firstMonth = "0";
        }
        if (day < 10) {
            firstDay = "0";
        }
        if (timeOn) {  //开机时间
            int powerOnTHour = schduleRecorder.getPowerHour();
            int powerOnMin = schduleRecorder.getPowerMinute();
            if (powerOnTHour < 10) {
                firstHour = "0";
            }
            if (powerOnMin < 10) {
                firstMin = "0";
            }
            onTime = year + firstMonth + month + firstDay + day + firstHour + powerOnTHour + firstMin + powerOnMin + "00";
//            MyLog.powerOnOff("====haha===" + year + "/ " + firstMonth + "/ " + month + "/ " + firstDay + "/ " + day + "/ " + firstHour + "/ " + powerOnTHour + "/ " + firstMin + "/ " + powerOnMin + "/ " + "00");
        } else {
            int powerOffTHour = schduleRecorder.getCloseHour();
            int powerOffMin = schduleRecorder.getCloseMinute();
            String endHour = "";
            String endMin = "";
            if (powerOffTHour < 10) {
                endHour = "0";
            }
            if (powerOffMin < 10) {
                endMin = "0";
            }
            onTime = year + firstMonth + month + firstDay + day + endHour + powerOffTHour + endMin + powerOffMin + "00";
        }
        long timeBack = Long.parseLong(onTime);
        return timeBack;
    }


}
