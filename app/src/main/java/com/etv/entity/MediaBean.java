package com.etv.entity;

public class MediaBean {
    String Name;
    String path;
    int fileType;

//    public static final int MEDIA_IMAGE = 0;
//    public static final int MEDIA_VIDEO = 2;


    public MediaBean(String name, String path) {
        Name = name;
        this.path = path;
    }

    public MediaBean(String name, String path, int fileType) {
        Name = name;
        this.path = path;
        this.fileType = fileType;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
