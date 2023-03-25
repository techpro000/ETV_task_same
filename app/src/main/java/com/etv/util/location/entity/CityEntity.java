package com.etv.util.location.entity;


import java.util.ArrayList;

public class CityEntity {

    String name;
    ArrayList<AreaEntity> areaList;

    public CityEntity(String name, ArrayList<AreaEntity> areaList) {
        this.name = name;
        this.areaList = areaList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<AreaEntity> getAreaList() {
        return areaList;
    }

    public void setAreaList(ArrayList<AreaEntity> areaList) {
        this.areaList = areaList;
    }

    @Override
    public String toString() {
        return "CityEntity{" +
                "name='" + name + '\'' +
                '}';
    }
}
