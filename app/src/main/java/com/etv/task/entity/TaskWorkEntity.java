package com.etv.task.entity;

import org.litepal.crud.LitePalSupport;

import java.util.List;

/***
 * 任务实体类
 */
public class TaskWorkEntity extends LitePalSupport {

    private String taskid;              //任务编号
    private String etName;              //任务名称
    private String etLevel;             //1替换 2追加 3插播 4同步 5：触发任务
    private String etTaskType;          // 1普通节目任务 2双屏异显任务 3:互动任务 4:插播消息

    private String startDate;            //开始的日期
    private String endDate;              //结束的日期
    private String startTime;           //开始的时分秒
    private String endTime;             //结束的时分秒

    private long sendTime;              //  节目的发送时间
    private String etMon;
    private String etTue;
    private String etWed;
    private String etThur;
    private String etFri;
    private String etSat;
    private String etSun;
    private int etIsLinkScreeen;  //是否双屏联动   1：联动  2：不联动
    private String streamIdOne;   //流媒体 1
    private String streamIdTwo;   //流媒体 2
    List<PmListEntity> pmListEntities;  //节目实体类


    public TaskWorkEntity() {
    }

    public String getStreamIdOne() {
        return streamIdOne;
    }

    public void setStreamIdOne(String streamIdOne) {
        this.streamIdOne = streamIdOne;
    }

    public String getStreamIdTwo() {
        return streamIdTwo;
    }

    public void setStreamIdTwo(String streamIdTwo) {
        this.streamIdTwo = streamIdTwo;
    }

    public int getEtIsLinkScreeen() {
        return etIsLinkScreeen;
    }

    public void setEtIsLinkScreeen(int etIsLinkScreeen) {
        this.etIsLinkScreeen = etIsLinkScreeen;
    }

    public String getEtLevel() {
        return etLevel;
    }

    public void setEtLevel(String etLevel) {
        this.etLevel = etLevel;
    }

    public String getEtTaskType() {
        return etTaskType;
    }

    public void setEtTaskType(String etTaskType) {
        this.etTaskType = etTaskType;
    }

    public List<PmListEntity> getPmListEntities() {
        return pmListEntities;
    }

    public void setPmListEntities(List<PmListEntity> pmListEntities) {
        this.pmListEntities = pmListEntities;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
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

    public String getEtMon() {
        return etMon;
    }

    public void setEtMon(String etMon) {
        this.etMon = etMon;
    }

    public String getEtTue() {
        return etTue;
    }

    public void setEtTue(String etTue) {
        this.etTue = etTue;
    }

    public String getEtWed() {
        return etWed;
    }

    public void setEtWed(String etWed) {
        this.etWed = etWed;
    }

    public String getEtThur() {
        return etThur;
    }

    public void setEtThur(String etThur) {
        this.etThur = etThur;
    }

    public String getEtFri() {
        return etFri;
    }

    public void setEtFri(String etFri) {
        this.etFri = etFri;
    }

    public String getEtSat() {
        return etSat;
    }

    public void setEtSat(String etSat) {
        this.etSat = etSat;
    }

    public String getEtSun() {
        return etSun;
    }

    public void setEtSun(String etSun) {
        this.etSun = etSun;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public String getTaskId() {
        return taskid;
    }

    public void setTaskId(String tadkid) {
        this.taskid = tadkid;
    }

    public String getEtName() {
        return etName;
    }

    public void setEtName(String etName) {
        this.etName = etName;
    }

    @Override
    public String toString() {
        return "TaskWorkEntity{" +
                "taskid='" + taskid + '\'' +
                ", etName='" + etName + '\'' +
                ", etLevel='" + etLevel + '\'' +
                ", etTaskType='" + etTaskType + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", sendTime=" + sendTime +
                ", etMon='" + etMon + '\'' +
                ", etTue='" + etTue + '\'' +
                ", etWed='" + etWed + '\'' +
                ", etThur='" + etThur + '\'' +
                ", etFri='" + etFri + '\'' +
                ", etSat='" + etSat + '\'' +
                ", etSun='" + etSun + '\'' +
                '}';
    }
}
