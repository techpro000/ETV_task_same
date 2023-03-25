package com.etv.http.util;

import android.os.Handler;

import com.etv.config.AppInfo;
import com.etv.entity.ScheduleRecord;
import com.etv.entity.TimeComparEntity;
import com.etv.service.listener.EtvServerListener;
import com.etv.task.db.DbTimerUtil;
import com.etv.task.entity.TimerDbEntity;
import com.etv.util.MyLog;
import com.etv.util.SimpleDateUtil;
import com.etv.util.system.PowerOnOffUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * 定时开关机检测时间得线程
 */
public class CheckTimeRunnable implements Runnable {

    EtvServerListener listener;

    public CheckTimeRunnable(EtvServerListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        checkTimerShotDown();
    }

    public void checkTimerShotDown() {
        jujleNetWorkModelTimer();
    }

    private void jujleNetWorkModelTimer() {
        List<TimerDbEntity> timerList = DbTimerUtil.queryTimerList();
        if (timerList == null || timerList.size() < 1) {
            MyLog.powerOnOff("0000======数据库获取的定时开关机==null");
            backStatues(true);
            return;
        }
        List<ScheduleRecord> listTimer = PowerOnOffUtil.getPowerDateNetList(timerList);
        if (listTimer == null || listTimer.size() < 1) {
            MyLog.powerOnOff("0000======解析定时开关机==null");
            backStatues(true);
            return;
        }
        parsenerTimerTask(listTimer);
    }


    private void parsenerTimerTask(List<ScheduleRecord> listTimer) {
        if (listTimer == null || listTimer.size() < 1) {
            backStatues(true);
            return;
        }
        List<TimeComparEntity> listTimeCompair = new ArrayList<>();
        for (int i = 0; i < listTimer.size(); i++) {
            ScheduleRecord schduleRecorder = listTimer.get(i);
            int year = schduleRecorder.getYear();
            int month = schduleRecorder.getMonth();
            int day = schduleRecorder.getDay();
            int powerOnTHour = schduleRecorder.getPowerHour();
            int powerOnMin = schduleRecorder.getPowerMinute();

            int powerOffTHour = schduleRecorder.getCloseHour();
            int powerOffMin = schduleRecorder.getCloseMinute();
            String firstMonth = "";
            String firstDay = "";
            String firstHour = "";
            String firstMin = "";
            String endHour = "";
            String endMin = "";
            if (month < 10) {
                firstMonth = "0";
            }
            if (day < 10) {
                firstDay = "0";
            }
            if (powerOnTHour < 10) {
                firstHour = "0";
            }
            if (powerOnMin < 10) {
                firstMin = "0";
            }
            if (powerOffTHour < 10) {
                endHour = "0";
            }
            if (powerOffMin < 10) {
                endMin = "0";
            }
            String ssTime = "00";          //秒
            String saveOnTime = year + firstMonth + month + firstDay + day + firstHour + powerOnTHour + firstMin + powerOnMin + ssTime;
            String saveOffTime = year + firstMonth + month + firstDay + day + endHour + powerOffTHour + endMin + powerOffMin + ssTime;
            long onTime = Long.parseLong(saveOnTime);
            long offTime = Long.parseLong(saveOffTime);
            listTimeCompair.add(new TimeComparEntity(onTime, offTime, saveOnTime, saveOffTime));
        }
        long currentTimeLong = System.currentTimeMillis();
        boolean isShutTime = true;
        for (int k = 0; k < listTimeCompair.size(); k++) {
            String powerOnTimeString = listTimeCompair.get(k).getPowerOnTimeString();
            String ppowerOffTimeString = listTimeCompair.get(k).getPowerOffTimeString();
            MyLog.powerOnOff("计算开关机时间======便利开机时间String==" + powerOnTimeString + " /当前时间=" + SimpleDateUtil.formatBig(currentTimeLong) + " /关机时间==" + ppowerOffTimeString);
            long powerOnTime = SimpleDateUtil.StringToLongTimePower(powerOnTimeString);
            long powerOffTime = SimpleDateUtil.StringToLongTimePower(ppowerOffTimeString);
            if (powerOnTime < powerOffTime) {
                MyLog.powerOnOff("计算开关机时间======开机时间 < 关机时间====跨天操作=");
                //当前开机时间小于关机时间
                //判断当前时间只要在 开关机时间之内就可以
                //这里提前三分钟，防止不开机，关机后一分钟的时间误差
                long onTimeCompair = powerOnTime - (1000 * 60 * 3);
                long offTimeCompair = powerOffTime + (59 * 1000);
                long current24Time = SimpleDateUtil.getCurrentDay24Time();
                if (currentTimeLong > offTimeCompair && currentTimeLong < current24Time) {
                    isShutTime = false;
                    MyLog.powerOnOff("计算开关机时间===判断时间====" + "开机 < 关机 条件符合 =" + offTimeCompair + "/" + currentTimeLong + " / " + onTimeCompair);
                    break;
                }
                if (currentTimeLong < offTimeCompair && currentTimeLong > current24Time) {
                    isShutTime = false;
                    MyLog.powerOnOff("计算开关机时间===判断时间====" + "开机 < 关机 条件符合 =" + offTimeCompair + "/" + currentTimeLong + " / " + onTimeCompair);
                    break;
                }
                MyLog.powerOnOff("计算开关机时间===判断时间====" + "开机 < 关机 条件符合 =" + offTimeCompair + "/" + currentTimeLong + " / " + onTimeCompair);
            } else {
                //  一天之内得 正常定时开关机设置
                //  开机时间 大于 关机时间
                //  这个时间肯定在关机时间范围内
                long onTimeCompair = powerOnTime - (1000 * 60 * 3);
                long offTimeCompair = powerOffTime + (59 * 1000);
                if (currentTimeLong > offTimeCompair && currentTimeLong < onTimeCompair) {
                    MyLog.powerOnOff("计算开关机时间===判断时间====" + "开机 > 关机 =" + offTimeCompair + "/" + currentTimeLong + " / " + onTimeCompair);
                    isShutTime = false;
                    break;
                }
            }
        }
//        boolean isShutTime = false;
//        for (int k = 0; k < listTimeCompair.size(); k++) {
//            String powerOnTimeString = listTimeCompair.get(k).getPowerOnTimeString();
//            String ppowerOffTimeString = listTimeCompair.get(k).getPowerOffTimeString();
//            MyLog.powerOnOff("计算开关机时间======便利开机时间String==" + powerOnTimeString + " /当前时间=" + SimpleDateUtil.formatBig(currentTimeLong) + " /关机时间==" + ppowerOffTimeString);
//            long powerOnTime = SimpleDateUtil.StringToLongTimePower(powerOnTimeString);
//            long powerOffTime = SimpleDateUtil.StringToLongTimePower(ppowerOffTimeString);
//            if (powerOnTime < powerOffTime) {
//                MyLog.powerOnOff("计算开关机时间======开机时间 < 关机时间====跨天操作=");
//                //当前开机时间小于关机时间
//                //判断当前时间只要在 开关机时间之内就可以
//                //这里提前三分钟，防止不开机，关机后一分钟的时间误差
//                long onTimeCompair = powerOnTime - (1000 * 60 * 3);
//                long offTimeCompair = powerOffTime + (59 * 1000);
//                long current24Time = SimpleDateUtil.getCurrentDay24Time();
//                if (currentTimeLong < current24Time) {
//                    if (currentTimeLong < offTimeCompair) {
//                        isShutTime = true;
//                        MyLog.powerOnOff("计算开关机时间===判断时间====" + "开机 < 关机 条件符合 =" + offTimeCompair + "/" + currentTimeLong + " / " + onTimeCompair);
//                        break;
//                    }
//                } else {
//                    if (currentTimeLong > onTimeCompair) {
//                        isShutTime = true;
//                        MyLog.powerOnOff("计算开关机时间===判断时间====" + "开机 < 关机 条件符合 =" + offTimeCompair + "/" + currentTimeLong + " / " + onTimeCompair);
//                        break;
//                    }
//                }
//                MyLog.powerOnOff("计算开关机时间===判断时间====" + "开机 < 关机 条件符合 =" + offTimeCompair + "/" + currentTimeLong + " / " + onTimeCompair);
//            } else {
//                //  一天之内得 正常定时开关机设置
//                //  开机时间 大于 关机时间
//                //  这个时间肯定在关机时间范围内
//                long onTimeCompair = powerOnTime - (1000 * 60 * 3);
//                long offTimeCompair = powerOffTime + (59 * 1000);
//                if (currentTimeLong > onTimeCompair || currentTimeLong < offTimeCompair) {
//                    MyLog.powerOnOff("计算开关机时间===判断时间====" + "开机 > 关机 =" + offTimeCompair + "/" + currentTimeLong + " / " + onTimeCompair);
//                    isShutTime = true;
//                    break;
//                }
//            }
//        }
        MyLog.powerOnOff("计算开关机时间==当前时间是 " + isShutTime);
        if (isShutTime) {
            backStatues(true);   //开机时间
        } else {
            backStatues(false);   //关机时间
        }
    }

    private void backStatues(final boolean b) {
        try {
            if (listener == null) {
                return;
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.jujleCurrentIsShutDownTime(b);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler();

}
