package com.etv.entity;

public class DeviceTaskSameInfoEntity {

    int lineState;
    String ip;
    String mac;

    public DeviceTaskSameInfoEntity(int lineState, String ip, String mac) {
        this.lineState = lineState;
        this.ip = ip;
        this.mac = mac;
    }

    public int getLineState() {
        return lineState;
    }

    public void setLineState(int lineState) {
        this.lineState = lineState;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    @Override
    public String toString() {
        return "DeviceTaskSameInfoEntity{" +
                "lineState=" + lineState +
                ", ip='" + ip + '\'' +
                ", mac='" + mac + '\'' +
                '}';
    }
}
