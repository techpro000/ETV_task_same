package com.etv.entity;

import org.litepal.crud.LitePalSupport;

/**
 * 用来控制整体media音量的
 */
public class ControlMediaVoice extends LitePalSupport {

    String startTime;
    String endTime;
    String volume;

    public ControlMediaVoice(String startTime, String endTime, String volume) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.volume = volume;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    @Override
    public String toString() {
        return "ControlMediaVoice{" +
                "startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", volume='" + volume + '\'' +
                '}';
    }
}
