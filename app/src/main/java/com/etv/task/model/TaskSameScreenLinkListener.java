package com.etv.task.model;

import com.etv.entity.DeviceTaskSameInfoEntity;

import java.util.List;

public interface TaskSameScreenLinkListener {

    void backDevListInfo(boolean isSuccess, List<DeviceTaskSameInfoEntity> lists, String errorDesc);

}
