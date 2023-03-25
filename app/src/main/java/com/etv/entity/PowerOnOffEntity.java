package com.etv.entity;

import java.util.List;

/**
 * 定时开关机保存信息实体类
 */

public class PowerOnOffEntity {

    private List<TimedTaskListEntity> TimedTaskList;

    public void setTimedTaskList(List<TimedTaskListEntity> TimedTaskList) {
        this.TimedTaskList = TimedTaskList;
    }

    public List<TimedTaskListEntity> getTimedTaskList() {
        return TimedTaskList;
    }

}
