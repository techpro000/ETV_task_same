package com.etv.entity;

/**
 * Created by jsjm on 2018/11/9.
 */
public class RawSourceEntity {

    int rawId;
    long fileLength;
    String name;
    int apkVersion;

    public RawSourceEntity() {
    }

    public RawSourceEntity(int rawId, long fileLength, String name, int apkVersion) {
        this.rawId = rawId;
        this.fileLength = fileLength;
        this.name = name;
        this.apkVersion = apkVersion;
    }

    public int getApkVersion() {
        return apkVersion;
    }

    public void setApkVersion(int apkVersion) {
        this.apkVersion = apkVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRawId() {
        return rawId;
    }

    public void setRawId(int rawId) {
        this.rawId = rawId;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    @Override
    public String toString() {
        return "RawSourceEntity{" +
                "rawId=" + rawId +
                ", fileLength=" + fileLength +
                ", name=" + name +
                '}';
    }
}
