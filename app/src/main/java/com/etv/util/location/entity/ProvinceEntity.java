package com.etv.util.location.entity;


import java.util.List;

public class ProvinceEntity {

    String name;
    List<CityEntity> cityList;

    public ProvinceEntity(String name, List<CityEntity> cityList) {
        this.name = name;
        this.cityList = cityList;
    }

    public ProvinceEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CityEntity> getCityList() {
        return cityList;
    }

    public void setCityList(List<CityEntity> cityList) {
        this.cityList = cityList;
    }

    @Override
    public String toString() {
        return "ProvinceEntity{" +
                "name='" + name + '\'' +
                '}';
    }
}
