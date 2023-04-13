package com.etv.util.poweronoff.entity;

import org.litepal.crud.LitePalSupport;

/***
 * 用来打印定时开关机日志的实体类
 */
public class PoOnOffLogEntity extends LitePalSupport {
    String onTime;
    String offTime;
    String createTime;


    public PoOnOffLogEntity(String offTime, String onTime, String createTime) {
        this.onTime = onTime;
        this.offTime = offTime;
        this.createTime = createTime;
    }

    public String getOnTime() {
        return onTime;
    }

    public void setOnTime(String onTime) {
        this.onTime = onTime;
    }

    public String getOffTime() {
        return offTime;
    }

    public void setOffTime(String offTime) {
        this.offTime = offTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "\nPoOnOffLogEntity{" +
                "onTime='" + onTime + '\'' +
                ", offTime='" + offTime + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
