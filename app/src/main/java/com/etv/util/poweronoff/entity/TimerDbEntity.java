package com.etv.util.poweronoff.entity;

import org.litepal.crud.LitePalSupport;

/***
 * 定时开关机的任务
 */
public class TimerDbEntity extends LitePalSupport {

    /**
     * ttOnTime : 00:30
     * ttOffTime : 00:45
     * ttMon : true
     * ttTue : true
     * ttWed : true
     * ttThu : true
     * ttFri : true
     * ttSat : true
     * ttSun : true
     */
    private String timeId;
    private String ttOnTime;
    private String ttOffTime;
    private String ttMon;
    private String ttTue;
    private String ttWed;
    private String ttThu;
    private String ttFri;
    private String ttSat;
    private String ttSun;

    public TimerDbEntity() {
    }

    public TimerDbEntity(String timeId, String ttOnTime, String ttOffTime, String ttMon, String ttTue, String ttWed, String ttThu, String ttFri, String ttSat, String ttSun) {
        this.timeId = timeId;
        this.ttOnTime = ttOnTime;
        this.ttOffTime = ttOffTime;
        this.ttMon = ttMon;
        this.ttTue = ttTue;
        this.ttWed = ttWed;
        this.ttThu = ttThu;
        this.ttFri = ttFri;
        this.ttSat = ttSat;
        this.ttSun = ttSun;
    }

    public String getTimneId() {
        return timeId;
    }

    public void setTimneId(String timeId) {
        this.timeId = timeId;
    }

    public String getTtOnTime() {
        return ttOnTime;
    }

    public void setTtOnTime(String ttOnTime) {
        this.ttOnTime = ttOnTime;
    }

    public String getTtOffTime() {
        return ttOffTime;
    }

    public void setTtOffTime(String ttOffTime) {
        this.ttOffTime = ttOffTime;
    }

    public String getTtMon() {
        return ttMon;
    }

    public void setTtMon(String ttMon) {
        this.ttMon = ttMon;
    }

    public String getTtTue() {
        return ttTue;
    }

    public void setTtTue(String ttTue) {
        this.ttTue = ttTue;
    }

    public String getTtWed() {
        return ttWed;
    }

    public void setTtWed(String ttWed) {
        this.ttWed = ttWed;
    }

    public String getTtThu() {
        return ttThu;
    }

    public void setTtThu(String ttThu) {
        this.ttThu = ttThu;
    }

    public String getTtFri() {
        return ttFri;
    }

    public void setTtFri(String ttFri) {
        this.ttFri = ttFri;
    }

    public String getTtSat() {
        return ttSat;
    }

    public void setTtSat(String ttSat) {
        this.ttSat = ttSat;
    }

    public String getTtSun() {
        return ttSun;
    }

    public void setTtSun(String ttSun) {
        this.ttSun = ttSun;
    }

    @Override
    public String toString() {
        return "TimerDbEntity{" +
                "ttOnTime='" + ttOnTime + '\'' +
                ", ttOffTime='" + ttOffTime + '\'' +
                ", ttMon='" + ttMon + '\'' +
                ", ttTue='" + ttTue + '\'' +
                ", ttWed='" + ttWed + '\'' +
                ", ttThu='" + ttThu + '\'' +
                ", ttFri='" + ttFri + '\'' +
                ", ttSat='" + ttSat + '\'' +
                ", ttSun='" + ttSun + '\'' +
                '}';
    }

    //"ttMon":"true","ttTue":"true","ttWed":"true","ttThu":"true","ttFri":"true","ttSat":"true","ttSun":"true",

}
