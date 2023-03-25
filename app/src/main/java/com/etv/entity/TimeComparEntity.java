package com.etv.entity;


/***
 * 用于比对关机
 * 开机时间
 * 比对
 */
public class TimeComparEntity {

    long powerOnTime;
    long powerOffTime;
    String powerOnTimeString;
    String powerOffTimeString;


    public TimeComparEntity(long powerOnTime, long powerOffTime, String powerOnTimeString, String powerOffTimeString) {
        this.powerOnTime = powerOnTime;
        this.powerOffTime = powerOffTime;
        this.powerOnTimeString = powerOnTimeString;
        this.powerOffTimeString = powerOffTimeString;
    }

    public String getPowerOnTimeString() {
        return powerOnTimeString;
    }

    public void setPowerOnTimeString(String powerOnTimeString) {
        this.powerOnTimeString = powerOnTimeString;
    }

    public String getPowerOffTimeString() {
        return powerOffTimeString;
    }

    public void setPowerOffTimeString(String powerOffTimeString) {
        this.powerOffTimeString = powerOffTimeString;
    }

    public long getPowerOnTime() {
        return powerOnTime;
    }

    public void setPowerOnTime(long powerOnTime) {
        this.powerOnTime = powerOnTime;
    }

    public long getPowerOffTime() {
        return powerOffTime;
    }

    public void setPowerOffTime(long powerOffTime) {
        this.powerOffTime = powerOffTime;
    }

    @Override
    public String toString() {
        return "TimeComparEntity{" +
                "powerOnTime=" + powerOnTime +
                ", powerOffTime=" + powerOffTime +
                '}';
    }
}
