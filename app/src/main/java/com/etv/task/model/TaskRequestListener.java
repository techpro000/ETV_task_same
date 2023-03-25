package com.etv.task.model;

import com.etv.task.entity.SceneEntity;
import com.etv.task.entity.TaskWorkEntity;

import java.util.List;

/***
 * 用来监听任务请求状态
 */
public interface TaskRequestListener {

    /**
     * 修改字幕信息
     *
     * @param isSuccess
     * @param desc
     */
    void modifyTxtInfoStatues(boolean isSuccess, String desc);

    /***
     * 播放完成，切换下一个
     * @param isBack
     * @param sceneEntities
     * tag==-1 得时候，表示是触发得任务，这里结束播放任务
     * @param tag
     */
    void playNextProgram(boolean isBack, List<SceneEntity> sceneEntities, int tag);

//    void requestStatus(boolean isSuccess, String json, String errorDesc, String printTag);

    void finishMySelf(String errorDesc);

    /***
     * 解析JSON完毕
     */
    void parserJsonOver(String tag);
}
