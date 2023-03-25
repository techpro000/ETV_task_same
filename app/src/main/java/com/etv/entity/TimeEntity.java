package com.etv.entity;

/**
 * 用来封装详细的数据的事件
 */

public class TimeEntity {

    int year;
    int month;
    int day;
    int hour;
    int minite;


    public TimeEntity() {
    }

    public TimeEntity(int year, int month, int day, int hour, int minite) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minite = minite;
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

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinite() {
        return minite;
    }

    public void setMinite(int minite) {
        this.minite = minite;
    }

    @Override
    public String toString() {
        return "TimeEntity{" +
                "year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", hour=" + hour +
                ", minite=" + minite +
                '}';
    }
}
