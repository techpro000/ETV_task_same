package com.etv.task.model;

import com.etv.task.entity.TaskWorkEntity;

import java.util.List;

public interface TaskGetDbListener {

    /**
     * 从数据库中获取数据
     */
    void getTaskFromDb(List<TaskWorkEntity> list);

    /***
     * 获取触发节目
     */
    void getTaskTigerFromDb(TaskWorkEntity taskWorkEntity);

}
