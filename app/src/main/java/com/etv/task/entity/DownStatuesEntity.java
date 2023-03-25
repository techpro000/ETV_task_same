package com.etv.task.entity;

public class DownStatuesEntity {
    boolean isShow;
    String showDesc;

    public DownStatuesEntity(boolean isShow, String showDesc) {
        this.isShow = isShow;
        this.showDesc = showDesc;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public String getShowDesc() {
        return showDesc;
    }

    public void setShowDesc(String showDesc) {
        this.showDesc = showDesc;
    }
}
