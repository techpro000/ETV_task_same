package com.etv.task.entity;

/***
 * 用来同步任务信息得
 */
public class SameTaskEntity {
    String taskId;
    String scId;
    int currentPlayPosition;    //当前播放得个数位置

    public SameTaskEntity(String taskId, String scId, int currentPlayPosition) {
        this.scId = scId;
        this.taskId = taskId;
        this.currentPlayPosition = currentPlayPosition;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getScId() {
        return scId;
    }

    public void setScId(String scId) {
        this.scId = scId;
    }

    public int getCurrentPlayPosition() {
        return currentPlayPosition;
    }

    public void setCurrentPlayPosition(int currentPlayPosition) {
        this.currentPlayPosition = currentPlayPosition;
    }

    @Override
    public String toString() {
        return "SameTaskEntity{" +
                "taskId='" + taskId + '\'' +
                ", scId='" + scId + '\'' +
                ", currentPlayPosition='" + currentPlayPosition + '\'' +
                '}';
    }
}
