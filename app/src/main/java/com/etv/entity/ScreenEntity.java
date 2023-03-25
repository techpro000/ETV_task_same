package com.etv.entity;

import android.view.Display;

/**
 * 屏幕参数
 */
public class ScreenEntity {

    String screenType; //屏幕属性 主辅
    int screenWidth;
    int screenHeight;
    Display display;

    public ScreenEntity(String screenType, int screenWidth, int screenHeight, Display display) {
        this.screenType = screenType;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.display = display;
    }

    public Display getDisplay() {
        return display;
    }

    public void setDisplay(Display display) {
        this.display = display;
    }

    public String getScreenType() {
        return screenType;
    }

    public void setScreenType(String screenType) {
        this.screenType = screenType;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    @Override
    public String toString() {
        return "ScreenEntity{" +
                "screenType='" + screenType + '\'' +
                ", screenWidth=" + screenWidth +
                ", screenHeight=" + screenHeight +
                '}';
    }
}
