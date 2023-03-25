package com.etv.task.model;

import com.etv.task.entity.TaskDownEntity;
import com.etv.task.entity.TaskWorkEntity;

import java.util.List;

public interface TaskGetDownListListener {

    /**
     * 从数据库中获取数据
     */
    void getTaskDownFileListFromDb(List<TaskDownEntity> list);

}
