package com.etv.task.entity;

/**
 * 单机模式控件的坐标封装
 */
public class ViewPosition {

    public static final int VIEW_LAYOUT_HRO_VIEW = 1;
    public static final int VIEW_LAYOUT_IMAGE_HRO = VIEW_LAYOUT_HRO_VIEW + 1;  //横屏图片
    public static final int VIEW_LAYOUT_VIDEO_HRO = VIEW_LAYOUT_IMAGE_HRO + 1;  //横屏视频
    public static final int VIEW_LAYOUT_WPS_HRO = VIEW_LAYOUT_VIDEO_HRO + 1;  //横屏文档
    public static final int VIEW_LAYOUT_2 = VIEW_LAYOUT_WPS_HRO + 1;
    public static final int VIEW_LAYOUT_3 = VIEW_LAYOUT_2 + 1;
    public static final int VIEW_LAYOUT_4 = VIEW_LAYOUT_3 + 1;
    public static final int VIEW_LAYOUT_5 = VIEW_LAYOUT_4 + 1;
    public static final int VIEW_LAYOUT_6 = VIEW_LAYOUT_5 + 1;
    public static final int VIEW_LAYOUT_7 = VIEW_LAYOUT_6 + 1;
    public static final int VIEW_LAYOUT_8 = VIEW_LAYOUT_7 + 1;
    public static final int VIEW_LAYOUT_9 = VIEW_LAYOUT_8 + 1;
    public static final int VIEW_LAYOUT_10 = VIEW_LAYOUT_9 + 1;
    public static final int VIEW_LAYOUT_11 = VIEW_LAYOUT_10 + 1;
    public static final int VIEW_LAYOUT_12 = VIEW_LAYOUT_11 + 1;
    public static final int VIEW_LAYOUT_13 = VIEW_LAYOUT_12 + 1;
    public static final int VIEW_LAYOUT_14 = VIEW_LAYOUT_13 + 1;

    public static final int VIEW_LAYOUT_VER_VIEW = 50;
    public static final int VIEW_LAYOUT_IMAGE_VER = VIEW_LAYOUT_VER_VIEW + 1;  //竖屏图片
    public static final int VIEW_LAYOUT_VIDEO_VER = VIEW_LAYOUT_IMAGE_VER + 1;  //竖屏视频
    public static final int VIEW_LAYOUT_WPS_VER = VIEW_LAYOUT_VIDEO_VER + 1;  //竖屏文档
    public static final int VIEW_LAYOUT_54 = VIEW_LAYOUT_WPS_VER + 1;
    public static final int VIEW_LAYOUT_55 = VIEW_LAYOUT_54 + 1;
    public static final int VIEW_LAYOUT_56 = VIEW_LAYOUT_55 + 1;
    public static final int VIEW_LAYOUT_57 = VIEW_LAYOUT_56 + 1;
    public static final int VIEW_LAYOUT_58 = VIEW_LAYOUT_57 + 1;
    public static final int VIEW_LAYOUT_59 = VIEW_LAYOUT_58 + 1;
    public static final int VIEW_LAYOUT_60 = VIEW_LAYOUT_59 + 1;
    public static final int VIEW_LAYOUT_61 = VIEW_LAYOUT_60 + 1;
    public static final int VIEW_LAYOUT_62 = VIEW_LAYOUT_61 + 1;
    public static final int VIEW_LAYOUT_63 = VIEW_LAYOUT_62 + 1;
    public static final int VIEW_LAYOUT_64 = VIEW_LAYOUT_63 + 1;
    public static final int VIEW_LAYOUT_65 = VIEW_LAYOUT_64 + 1;  //上下 1：2 比例显示
    public static final int VIEW_LAYOUT_66 = VIEW_LAYOUT_65 + 1;  //上中下  1：1：1 比例显示

    String viewType;
    int leftPosition;
    int topPosition;
    int width;
    int height;
    int showPosition;

    public ViewPosition(String viewType, int leftPosition, int topPosition, int width, int height, int showPosition) {
        this.viewType = viewType;
        this.leftPosition = leftPosition;
        this.topPosition = topPosition;
        this.width = width;
        this.height = height;
        this.showPosition = showPosition;
    }

    public int getShowPosition() {
        return showPosition;
    }

    public void setShowPosition(int showPosition) {
        this.showPosition = showPosition;
    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    public int getLeftPosition() {
        return leftPosition;
    }

    public void setLeftPosition(int leftPosition) {
        this.leftPosition = leftPosition;
    }

    public int getTopPosition() {
        return topPosition;
    }

    public void setTopPosition(int topPosition) {
        this.topPosition = topPosition;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
