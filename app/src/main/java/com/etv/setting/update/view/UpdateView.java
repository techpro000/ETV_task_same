package com.etv.setting.update.view;

import com.etv.setting.update.entity.UpdateInfo;

import java.util.List;

public interface UpdateView {

    /***
     * 获取请求数据success,b把数据返回界面显示
     */
    void updateMainView(List<UpdateInfo> listCache);

    /***
     * 操作完毕
     * @param desc
     * 用来查看是哪里的tag
     */
    void updateOver(String desc);
}
