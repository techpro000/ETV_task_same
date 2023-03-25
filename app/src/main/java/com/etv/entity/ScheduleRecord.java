package com.etv.entity;

/***
 * 定时开关机实体类
 */
public class ScheduleRecord {

    int year;
    int month;
    int day;

    int dayOfWeek;
    int powerHour;
    int powerMinute;
    int closeHour;
    int closeMinute;
    String id;

    public ScheduleRecord(int dayOfWeek, int powerHour, int powerMinute, int closeHour, int closeMinute) {
        this(dayOfWeek, powerHour, powerMinute, closeHour, closeMinute, "");
    }

    private ScheduleRecord(int dayOfWeek, int powerHour, int powerMinute, int closeHour, int closeMinute, String id) {
        this(0, 0, 0, dayOfWeek, powerHour, powerMinute, closeHour, closeMinute, "");
    }

    public ScheduleRecord(int year, int month, int day, int dayOfWeek, int powerHour, int powerMinute, int closeHour, int closeMinute, String id) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.dayOfWeek = dayOfWeek;
        this.powerHour = powerHour;
        this.powerMinute = powerMinute;
        this.closeHour = closeHour;
        this.closeMinute = closeMinute;
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setPowerHour(int powerHour) {
        this.powerHour = powerHour;
    }

    public void setPowerMinute(int powerMinute) {
        this.powerMinute = powerMinute;
    }

    public void setCloseHour(int closeHour) {
        this.closeHour = closeHour;
    }

    public void setCloseMinute(int closeMinute) {
        this.closeMinute = closeMinute;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public int getPowerHour() {
        return powerHour;
    }

    public int getPowerMinute() {
        return powerMinute;
    }

    public int getCloseHour() {
        return closeHour;
    }

    public int getCloseMinute() {
        return closeMinute;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "ScheduleRecord{" +
                "year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", dayOfWeek=" + dayOfWeek +
                ", powerHour=" + powerHour +
                ", powerMinute=" + powerMinute +
                ", closeHour=" + closeHour +
                ", closeMinute=" + closeMinute +
                ", id='" + id + '\'' +
                '}';
    }
}
