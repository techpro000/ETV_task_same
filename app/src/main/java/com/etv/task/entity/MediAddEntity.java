package com.etv.task.entity;


import java.io.Serializable;

public class MediAddEntity implements Serializable {

    String url;  //播放地址
    String midId;   //素材ID
    String cartoon;  //动画切换特效
    String playParam; //播放间隔时间
    String volNum;     //播放音量
    int fileType;      // 文件类型
    String pmType;  // 节目类型
    long fileSize;

    public MediAddEntity() {
    }

    public MediAddEntity(String url, String midId, String cartoon, String playParam, String volNum, int fileType, String pmType, long fileSize) {
        this.url = url;
        this.midId = midId;
        this.cartoon = cartoon;
        this.playParam = playParam;
        this.volNum = volNum;
        this.fileType = fileType;
        this.pmType = pmType;
        this.fileSize = fileSize;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getPmType() {
        return pmType;
    }

    public void setPmType(String pmType) {
        this.pmType = pmType;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public String getVolNum() {
        return volNum;
    }

    public void setVolNum(String volNum) {
        this.volNum = volNum;
    }

    public String getCartoon() {
        return cartoon;
    }

    public void setCartoon(String cartoon) {
        this.cartoon = cartoon;
    }

    public String getPlayParam() {
        return playParam;
    }

    public void setPlayParam(String playParam) {
        this.playParam = playParam;
    }

    public String getUrl() {
        return url;
    }

//    @Override
//    public String getImageUrl() {
//        return getUrl();
//    }
//
//    @Override
//    public long getLoopTime() {
//        if (playParam == null || playParam.length() < 1) {
//            return 10 * 1000;
//        }
//        long timeBack = 10;
//        try {
//            timeBack = Integer.parseInt(playParam);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return timeBack * 1000;
//    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMidId() {
        return midId;
    }

    public void setMidId(String midId) {
        this.midId = midId;
    }

    @Override
    public String toString() {
        return "MediAddEntity{" +
                "url='" + url + '\'' +
                ", midId=" + midId +
                ", cartoon='" + cartoon + '\'' +
                ", playParam='" + playParam + '\'' +
                ", volNum='" + volNum + '\'' +
                '}';
    }
}
