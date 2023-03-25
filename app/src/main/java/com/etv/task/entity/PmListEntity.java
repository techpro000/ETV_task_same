package com.etv.task.entity;

import org.litepal.crud.LitePalSupport;

import java.util.List;

/**
 * 节目实体类
 */
public class PmListEntity extends LitePalSupport {

    private String proid;             //节目ID
    private String taskid;            //绑定的任务ID
    private String pmName;            //节目名称
    private String pmType;            //节目类型 1普通节目 2触摸互动节目  3:插播消息
    private String displayPos;       //用来区分现在在哪一个屏幕
    private int pmResolutionType;       // 屏幕类型  1：分辨率  2：自适应  3:4k-自适应
    private int pmFixedScreen;             // 1横屏 2 竖屏

    private List<SceneEntity> sceneEntityList;//场景集合

    public int getPmResolutionType() {
        return pmResolutionType;
    }

    public void setPmResolutionType(int pmResolutionType) {
        this.pmResolutionType = pmResolutionType;
    }

    public int getPmFixedScreen() {
        return pmFixedScreen;
    }

    public void setPmFixedScreen(int pmFixedScreen) {
        this.pmFixedScreen = pmFixedScreen;
    }

    public String getDisplayPos() {
        return displayPos;
    }

    public void setDisplayPos(String displayPos) {
        this.displayPos = displayPos;
    }

    public String getPmType() {
        return pmType;
    }

    public void setPmType(String pmType) {
        this.pmType = pmType;
    }


    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getProId() {
        return proid;
    }

    public void setProId(String proid) {
        this.proid = proid;
    }

    public String getPmName() {
        return pmName;
    }

    public void setPmName(String pmName) {
        this.pmName = pmName;
    }

    public List<SceneEntity> getSceneEntityList() {
        return sceneEntityList;
    }

    public void setSceneEntityList(List<SceneEntity> sceneEntityList) {
        this.sceneEntityList = sceneEntityList;
    }

    @Override
    public String toString() {
        return "PmListEntity{" +
                "proid='" + proid + '\'' +
                ", taskid='" + taskid + '\'' +
                ", pmName='" + pmName + '\'' +
                ", pmType='" + pmType + '\'' +
                ", displayPos='" + displayPos + '\'' +
                '}';
    }
}
