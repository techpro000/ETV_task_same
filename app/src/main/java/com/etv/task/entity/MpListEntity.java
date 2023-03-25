package com.etv.task.entity;

import org.litepal.crud.LitePalSupport;

/**
 * 资源列表
 */

public class MpListEntity extends LitePalSupport {
    /**
     * playParam : 5
     * mid : 10
     * size:114543
     * url : upload/srcImgPath\test2.png
     */
    private String taskId;
    private String pmType;         //节目类型
    private String mid;            //素材编号
    private String cpId;           //控件ID
    private String playParam;   //播放时长
    private String cartoon;      //切换动画特效
    private String url;          //下载地址
    private String size;         //文件大小
    private String volume;       //音量大小
    private String type;         //文件得类型
    private String parentCoId;   //关联控件id  -1是普通得控件  有数值得是关联得ID

    public MpListEntity(String taskId, String mid, String cpId, String url, String playParam,
                        String cartoon, String size, String volume, String pmType, String parentCoId, String type) {
        this.taskId = taskId;
        this.playParam = playParam;
        this.mid = mid;
        this.cartoon = cartoon;
        this.cpId = cpId;
        this.url = url;
        this.size = size;
        this.volume = volume;
        this.pmType = pmType;
        this.parentCoId = parentCoId;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParentCoId() {
        return parentCoId;
    }

    public void setParentCoId(String parentCoId) {
        this.parentCoId = parentCoId;
    }

    public String getPmType() {
        return pmType;
    }

    public void setPmType(String pmType) {
        this.pmType = pmType;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getCartoon() {
        return cartoon;
    }

    public void setCartoon(String cartoon) {
        this.cartoon = cartoon;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCpId() {
        return cpId;
    }

    public void setCpId(String cpId) {
        this.cpId = cpId;
    }

    public String getPlayParam() {
        return playParam;
    }

    public void setPlayParam(String playParam) {
        this.playParam = playParam;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "MpListEntity{" +
                "taskId=" + taskId +
                "mid=" + mid +
                ", cpId=" + cpId +
                ", playParam='" + playParam + '\'' +
                ", cartoon='" + cartoon + '\'' +
                ", url='" + url + '\'' +
                ", size='" + size + '\'' +
                ", volume='" + volume + '\'' +
                '}';
    }
}
