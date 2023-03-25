package com.etv.task.entity;

import com.etv.view.layout.Generator;

public class CacheMemory {

    Generator generator;  //播放类View
    String coType;  //控件类型
    boolean isRelation;  //是否是关联控件

    public CacheMemory(Generator generator, String coType, boolean isRelation) {
        this.generator = generator;
        this.coType = coType;
        this.isRelation = isRelation;
    }

    public boolean isRelation() {
        return isRelation;
    }

    public void setRelation(boolean relation) {
        isRelation = relation;
    }

    public Generator getGenerator() {
        return generator;
    }

    public void setGenerator(Generator generator) {
        this.generator = generator;
    }

    public String getCoType() {
        return coType;
    }

    public void setCoType(String coType) {
        this.coType = coType;
    }
}
