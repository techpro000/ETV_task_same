package com.etv.task.entity;


/***
 * 用来计算节目坐标点的
 */
public class PositionEntity {

    int leftPosition = 0;
    int topPosition = 0;
    int width = 0;
    int height = 0;

    public PositionEntity(int leftPosition, int topPosition, int width, int height) {
        this.leftPosition = leftPosition;
        this.topPosition = topPosition;
        this.width = width;
        this.height = height;
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

    @Override
    public String toString() {
        return "PositionEntity{" +
                "leftPosition=" + leftPosition +
                ", topPosition=" + topPosition +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
