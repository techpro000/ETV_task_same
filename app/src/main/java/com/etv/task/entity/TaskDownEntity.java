package com.etv.task.entity;

import java.io.Serializable;

/**
 * 用来封装下载地址和保存地址的实体类
 */

public class TaskDownEntity implements Serializable {

    String taskId;
    String downUrl;
    String savePath;
    String fileLength;
    boolean isDownOver;


    /***
     *
     * @param taskId
     * @param downUrl
     * @param savePath
     *     这里提交全路径
     * @param fileLength
     * @param isDownOver
     */
    public TaskDownEntity(String taskId, String downUrl, String savePath, String fileLength, boolean isDownOver) {
        this.taskId = taskId;
        this.downUrl = downUrl;
        this.savePath = savePath;
        this.fileLength = fileLength;
        this.isDownOver = isDownOver;
    }


    public boolean isDownOver() {
        return isDownOver;
    }

    public void setDownOver(boolean downOver) {
        isDownOver = downOver;
    }

    public String getFileLength() {
        return fileLength;
    }

    public void setFileLength(String fileLength) {
        this.fileLength = fileLength;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    @Override
    public String toString() {
        return "TaskDownEntity{" +
                "taskId='" + taskId + '\'' +
                ", downUrl='" + downUrl + '\'' +
                ", savePath='" + savePath + '\'' +
                ", fileLength='" + fileLength + '\'' +
                '}';
    }
}
