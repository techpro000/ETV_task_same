package com.etv.entity;

public class WorkChooiceEntity {

    int imageId;
    String desc;
    boolean isChooice;
    int workModel;

    public WorkChooiceEntity(int imageId, String desc, boolean isChooice, int workModel) {
        this.imageId = imageId;
        this.desc = desc;
        this.isChooice = isChooice;
        this.workModel = workModel;
    }

    public int getWorkModel() {
        return workModel;
    }

    public void setWorkModel(int workModel) {
        this.workModel = workModel;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isChooice() {
        return isChooice;
    }

    public void setChooice(boolean chooice) {
        isChooice = chooice;
    }

    @Override
    public String toString() {
        return "WorkChooiceEntity{" +
                "imageId=" + imageId +
                ", desc='" + desc + '\'' +
                ", isChooice=" + isChooice +
                '}';
    }
}
