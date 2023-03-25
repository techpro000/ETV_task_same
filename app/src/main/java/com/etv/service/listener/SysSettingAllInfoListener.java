package com.etv.service.listener;

import com.etv.entity.BggImageEntity;
import com.etv.entity.PowerOnOffEntity;

import java.util.List;

public interface SysSettingAllInfoListener {

    void backPowerOnOffTimeInfo(boolean isSuccess, PowerOnOffEntity powerOnOffEntity, String errorDesc);

    /***
     * 返沪i背景图操作
     * @param lists
     * @param errorDesc
     */
    void backSuccessBggImageInfo(List<BggImageEntity> lists, String errorDesc);
}
