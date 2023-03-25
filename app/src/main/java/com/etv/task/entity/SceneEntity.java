package com.etv.task.entity;

import org.litepal.crud.LitePalSupport;

import java.util.List;

/**
 * 场景实体类
 */
public class SceneEntity extends LitePalSupport {

    String senceid;               //场景 ID
    String programid;             //节目ID
    String taskid;                //任务ID
    String pmType;                //节目类型  普通，互动,双屏任务
    String etLevel;               //1替换 2追加 3插播 4同步
    String displayPos;             //展示的位置
    List<CpListEntity> listCp;
    String scBackImg;              //背景图地址
    String scBackimgSize;          //背景图文件大小
    String scTime;                 //场景设置的播放时间
    int etIsLinkScreeen;           //是否双屏联动   1：联动  2：不联动
    long saveTime;

    public SceneEntity(String senceid, String programid, String taskid, String scBackImg, String scBackimgSize,
                       String pmType, String displayPos, String etLevel, String scTime,
                       int etIsLinkScreeen, long saveTime) {
        this.taskid = taskid;
        this.senceid = senceid;
        this.programid = programid;
        this.scBackImg = scBackImg;
        this.scBackimgSize = scBackimgSize;
        this.pmType = pmType;
        this.displayPos = displayPos;
        this.etLevel = etLevel;
        this.scTime = scTime;
        this.etIsLinkScreeen = etIsLinkScreeen;
        this.saveTime = saveTime;
    }

    public long getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(long saveTime) {
        this.saveTime = saveTime;
    }

    public int getEtIsLinkScreeen() {
        return etIsLinkScreeen;
    }

    public void setEtIsLinkScreeen(int etIsLinkScreeen) {
        this.etIsLinkScreeen = etIsLinkScreeen;
    }

    public String getScTime() {
        return scTime;
    }

    public void setScTime(String scTime) {
        this.scTime = scTime;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getPmType() {
        return pmType;
    }

    public String getEtLevel() {
        return etLevel;
    }

    public void setEtLevel(String etLevel) {
        this.etLevel = etLevel;
    }

    public void setPmType(String pmType) {
        this.pmType = pmType;
    }

    public String getDisplayPos() {
        return displayPos;
    }

    public void setDisplayPos(String displayPos) {
        this.displayPos = displayPos;
    }

    public String getScBackimgSize() {
        return scBackimgSize;
    }

    public void setScBackimgSize(String scBackimgSize) {
        this.scBackimgSize = scBackimgSize;
    }

    public String getSenceId() {
        return senceid;
    }

    public void setSenceId(String senceid) {
        this.senceid = senceid;
    }

    public String getProgramId() {
        return programid;
    }

    public void setProgramId(String programid) {
        this.programid = programid;
    }

    public String getScBackImg() {
        return scBackImg;
    }

    public void setScBackImg(String scBackImg) {
        this.scBackImg = scBackImg;
    }

    public List<CpListEntity> getListCp() {
        return listCp;
    }

    public void setListCp(List<CpListEntity> listCp) {
        this.listCp = listCp;
    }

    @Override
    public String toString() {
        return "SceneEntity{" +
                "senceid='" + senceid + '\'' +
                ", programid='" + programid + '\'' +
                ", taskid='" + taskid + '\'' +
                ", pmType='" + pmType + '\'' +
                ", etLevel='" + etLevel + '\'' +
                ", displayPos='" + displayPos + '\'' +
                ", listCp=" + listCp +
                ", scBackImg='" + scBackImg + '\'' +
                ", scBackimgSize='" + scBackimgSize + '\'' +
                ", scTime='" + scTime + '\'' +
                ", etIsLinkScreeen=" + etIsLinkScreeen +
                ", saveTime=" + saveTime +
                '}';
    }
}

