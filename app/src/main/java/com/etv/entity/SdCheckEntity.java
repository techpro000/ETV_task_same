package com.etv.entity;


/**
 * 通用List grid view的简单展示entity
 */

public class SdCheckEntity {

    String title;
    String number;
    boolean isRed;

    public SdCheckEntity(String title, String number, boolean isRed) {
        this.title = title;
        this.number = number;
        this.isRed = isRed;
    }

    public boolean isRed() {
        return isRed;
    }

    public void setRed(boolean red) {
        isRed = red;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
