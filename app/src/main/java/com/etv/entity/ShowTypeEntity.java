package com.etv.entity;

public class ShowTypeEntity {

    int position;
    String descType;

    public ShowTypeEntity(int position, String descType) {
        this.position = position;
        this.descType = descType;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getDescType() {
        return descType;
    }

    public void setDescType(String descType) {
        this.descType = descType;
    }

    @Override
    public String toString() {
        return "ShowTypeEntity{" +
                "position=" + position +
                ", descType='" + descType + '\'' +
                '}';
    }
}
