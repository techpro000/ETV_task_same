package com.etv.entity;


/**
 * 通用List grid view的简单展示entity
 */

public class HiddleBeanEntity {


    int beanId;
    String title;
    int imageId;
    String btnText;

    public HiddleBeanEntity(int beanId, String title, int imageId, String btnText) {
        this.beanId = beanId;
        this.title = title;
        this.imageId = imageId;
        this.btnText = btnText;
    }

    public int getBeanId() {
        return beanId;
    }

    public void setBeanId(int beanId) {
        this.beanId = beanId;
    }

    public String getBtnText() {
        return btnText;
    }

    public void setBtnText(String btnText) {
        this.btnText = btnText;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }


}
