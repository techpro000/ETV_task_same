package com.etv.entity;

import org.litepal.crud.LitePalSupport;

/**
 * 背景图片实体类
 */
public class BggImageEntity extends LitePalSupport {

    String filestyle;   //0:bgg  1:logo
    String fileType;
    long fileSize;
    String imagePath;
    String savePath;
    String imageName;

    public static final String STYPE_BGG_IMAGE = "0";
    public static final String STYPE_LOGO_IMAGE = "1";


    public BggImageEntity(String fileType, long fileSize, String imagePath, String savePath, String imageName, String filestyle) {
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.imagePath = imagePath;
        this.savePath = savePath;
        this.imageName = imageName;
        this.filestyle = filestyle;
    }

    public String getFileStype() {
        return filestyle;
    }

    public void setFileStype(String filestyle) {
        this.filestyle = filestyle;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "BggImageEntity{" +
                "fileType='" + fileType + '\'' +
                ", fileSize=" + fileSize +
                ", imagePath='" + imagePath + '\'' +
                ", savePath='" + savePath + '\'' +
                ", filestyle='" + filestyle + '\'' +
                '}';
    }
}
