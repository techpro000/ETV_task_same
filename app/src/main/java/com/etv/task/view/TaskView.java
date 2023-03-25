package com.etv.task.view;


import com.etv.task.entity.TaskWorkEntity;

import java.util.List;

public interface TaskView {

    void finishMyShelf(String toast);

    /**
     * 返回需要下载播放的任务
     *
     * @param lists
     */
    void backTaskList(List<TaskWorkEntity> lists, String tag);

}
