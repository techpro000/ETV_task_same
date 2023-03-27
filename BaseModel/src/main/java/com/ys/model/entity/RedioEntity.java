package com.ys.model.entity;

public class RedioEntity {

    int position;
    String radioText;

    public RedioEntity(String radioText) {
        this.radioText = radioText;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getRadioText() {
        return radioText;
    }

    public void setRadioText(String radioText) {
        this.radioText = radioText;
    }
}
