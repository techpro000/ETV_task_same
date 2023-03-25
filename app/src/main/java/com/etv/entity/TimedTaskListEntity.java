package com.etv.entity;

/**
 * 定时开关机任务实体类
 */

public class TimedTaskListEntity {
    /**
     * ttOffTime : 15:32
     * ttThu : false
     * ttClId : 44572038f3fa46ecbcaaf5af001cec97
     * ttMon : true
     * ttOnTime : 02:31
     * ttTue : true
     * ttPsId : 1
     * ttState : true
     * id : e0cdfbc99fe74296a7dd234cee039b2f
     * ttFri : false
     * ttSat : false
     * ttSun : false
     * ttExcState : -1
     * ttWed : false
     */
    private String id;
    private String ttOffTime;
    private String ttThu;
    private String ttMon;
    private String ttOnTime;
    private String ttTue;
    private String ttFri;
    private String ttSat;
    private String ttSun;
    private String ttWed;

    public TimedTaskListEntity() {

    }

    //    starttTime, enTime, mon, tue, wed, thu, fri, sta, sun
    public TimedTaskListEntity(String id, String ttOnTime, String ttOffTime, String ttMon, String ttTue,
                               String ttWed, String ttThu, String ttFri, String ttSat, String ttSun) {
        this.id = id;
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

    public String getTtOffTime() {
        return ttOffTime;
    }

    public void setTtOffTime(String ttOffTime) {
        this.ttOffTime = ttOffTime;
    }

    public String getTtThu() {
        return ttThu;
    }

    public void setTtThu(String ttThu) {
        this.ttThu = ttThu;
    }

    public String getTtMon() {
        return ttMon;
    }

    public void setTtMon(String ttMon) {
        this.ttMon = ttMon;
    }

    public String getTtOnTime() {
        return ttOnTime;
    }

    public void setTtOnTime(String ttOnTime) {
        this.ttOnTime = ttOnTime;
    }

    public String getTtTue() {
        return ttTue;
    }

    public void setTtTue(String ttTue) {
        this.ttTue = ttTue;
    }

    public String getTimerId() {
        return id;
    }

    public void setTimerId(String id) {
        this.id = id;
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

    public String getTtWed() {
        return ttWed;
    }

    public void setTtWed(String ttWed) {
        this.ttWed = ttWed;
    }
}