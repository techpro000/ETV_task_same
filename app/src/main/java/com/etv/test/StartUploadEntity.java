package com.etv.test;

/***
 * 开始同步主屏得任务信息
 */
public class StartUploadEntity {

    int fileType;
    String playParam;
    String filePath;
    String sencenId;  //场景ID

    public StartUploadEntity(int fileType, String playParam, String filePath, String sencenId) {
        this.fileType = fileType;
        this.playParam = playParam;
        this.filePath = filePath;
        this.sencenId = sencenId;
    }

    public String getSencenId() {
        return sencenId;
    }

    public void setSencenId(String sencenId) {
        this.sencenId = sencenId;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public String getPlayParam() {
        return playParam;
    }

    public void setPlayParam(String playParam) {
        this.playParam = playParam;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "StartUploadEntity{" +
                "fileType=" + fileType +
                ", playParam='" + playParam + '\'' +
                ", filePath='" + filePath + '\'' +
                ", sencenId='" + sencenId + '\'' +
                '}';
    }
}
