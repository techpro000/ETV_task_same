package com.etv.task.model;

import com.etv.task.entity.SceneEntity;
import com.etv.task.entity.TaskDownEntity;
import com.etv.task.entity.TaskWorkEntity;

import java.util.List;

/**
 * 用来请求任务的
 */

public interface TaskMudel {

    /**
     * 获取同步任务得设备信息
     *
     * @param taskId
     */
    void getSameTaskDevInfoFromServer(String taskId, TaskSameScreenLinkListener linkListener);

    /**
     * 修改字幕信息
     *
     * @param txtId
     * @param content
     */
    void modifyTextInfoToWeb(String txtId, String content, TaskRequestListener listener);

    void playNextProgram(List<SceneEntity> listsSence, int currentPosition, int playTag, TaskRequestListener listener);

    /***
     * 和服务器请求数据
     * @param listener
     */
    void requestTaskInfo(TaskRequestListener listener, String printTag);

    /***
     * 从数据库中获取一个节目
     * 只有再播放界面请求才是过滤所有不合格的场景，
     * 其他的地方调用，都是仅过滤过期的
     */
    void getPlayTaskListFormDb(TaskGetDbListener listener, String printTag, int delTag);


    /**
     * 新接口
     * 获取更具任务列表，获取下载地址
     *
     * @param lists
     * @param taskGetDownListListener
     * @return
     */
    void getTaskDownListInfoFromDbByTaskIdRunnable(List<TaskWorkEntity> lists, TaskGetDownListListener taskGetDownListListener, String printTag);

    /**
     * 检查当前有没有下载资格
     *
     * @param listener
     */
    void checkDownLimit(TaskCheckLimitListener listener);

}
