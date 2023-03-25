package com.etv.entity;

public class CacheSizeEntity {

    float maxMemory;
    //当前分配的总内存
    float totalMemory;
    //剩余内存
    float freeMemory;

    public CacheSizeEntity(float maxMemory, float totalMemory, float freeMemory) {
        this.maxMemory = maxMemory;
        this.totalMemory = totalMemory;
        this.freeMemory = freeMemory;
    }

    public float getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(float maxMemory) {
        this.maxMemory = maxMemory;
    }

    public float getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(float totalMemory) {
        this.totalMemory = totalMemory;
    }

    public float getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(float freeMemory) {
        this.freeMemory = freeMemory;
    }

    @Override
    public String toString() {
        return "CacheSizeEntity{" +
                "maxMemory=" + maxMemory +
                ", totalMemory=" + totalMemory +
                ", freeMemory=" + freeMemory +
                '}';
    }
}
