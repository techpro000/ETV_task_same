package com.etv.entity;

/**
 * 设备信息实体类
 */

public class ClientEntity {

    /**
     * clUpdateTime : 2019-04-15 20:38:46
     * clLineState : -1
     * clName : yisheng55
     * clMac : 30:1F:9A:64:55:CD
     * clGpId : 8bd1391969a445899758d9b9e85de8cc
     * clPhone : 13888888888
     * clNo : 301F9A6455CD
     * clAddress :
     * clVersion : rk312x_4.4.4_20190304.162824(V2.1.0.5)
     * clCreateTime : 2019-04-15 17:07:18
     * id : 44572038f3fa46ecbcaaf5af001cec97
     * clPsId : 1
     * clDisk : 5374M
     * clState : 1
     * clIp : 192.168.1.90
     * clAuthState : -1
     */

    private String clUpdateTime;
    private String clLineState;
    private String clName;
    private String clMac;
    private String clGpId;
    private String clPhone;
    private String clNo;
    private String clAddress;
    private String clVersion;
    private String clCreateTime;
    private String id;
    private String clPsId;
    private String clDisk;
    private String clState;
    private String clIp;
    private String clAuthState;

    public String getClUpdateTime() {
        return clUpdateTime;
    }

    public void setClUpdateTime(String clUpdateTime) {
        this.clUpdateTime = clUpdateTime;
    }

    public String getClLineState() {
        return clLineState;
    }

    public void setClLineState(String clLineState) {
        this.clLineState = clLineState;
    }

    public String getClName() {
        return clName;
    }

    public void setClName(String clName) {
        this.clName = clName;
    }

    public String getClMac() {
        return clMac;
    }

    public void setClMac(String clMac) {
        this.clMac = clMac;
    }

    public String getClGpId() {
        return clGpId;
    }

    public void setClGpId(String clGpId) {
        this.clGpId = clGpId;
    }

    public String getClPhone() {
        return clPhone;
    }

    public void setClPhone(String clPhone) {
        this.clPhone = clPhone;
    }

    public String getClNo() {
        return clNo;
    }

    public void setClNo(String clNo) {
        this.clNo = clNo;
    }

    public String getClAddress() {
        return clAddress;
    }

    public void setClAddress(String clAddress) {
        this.clAddress = clAddress;
    }

    public String getClVersion() {
        return clVersion;
    }

    public void setClVersion(String clVersion) {
        this.clVersion = clVersion;
    }

    public String getClCreateTime() {
        return clCreateTime;
    }

    public void setClCreateTime(String clCreateTime) {
        this.clCreateTime = clCreateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClPsId() {
        return clPsId;
    }

    public void setClPsId(String clPsId) {
        this.clPsId = clPsId;
    }

    public String getClDisk() {
        return clDisk;
    }

    public void setClDisk(String clDisk) {
        this.clDisk = clDisk;
    }

    public String getClState() {
        return clState;
    }

    public void setClState(String clState) {
        this.clState = clState;
    }

    public String getClIp() {
        return clIp;
    }

    public void setClIp(String clIp) {
        this.clIp = clIp;
    }

    public String getClAuthState() {
        return clAuthState;
    }

    public void setClAuthState(String clAuthState) {
        this.clAuthState = clAuthState;
    }

    @Override
    public String toString() {
        return "ClientEntity{" +
                "clUpdateTime='" + clUpdateTime + '\'' +
                ", clLineState='" + clLineState + '\'' +
                ", clName='" + clName + '\'' +
                ", clMac='" + clMac + '\'' +
                ", clGpId='" + clGpId + '\'' +
                ", clPhone='" + clPhone + '\'' +
                ", clNo='" + clNo + '\'' +
                ", clAddress='" + clAddress + '\'' +
                ", clVersion='" + clVersion + '\'' +
                ", clCreateTime='" + clCreateTime + '\'' +
                ", id='" + id + '\'' +
                ", clPsId='" + clPsId + '\'' +
                ", clDisk='" + clDisk + '\'' +
                ", clState='" + clState + '\'' +
                ", clIp='" + clIp + '\'' +
                ", clAuthState='" + clAuthState + '\'' +
                '}';
    }
}
