package com.etv.setting.entity;

/**
 * Created by jsjm on 2018/11/15.
 */

public class StoreEntity {

    String path;
    long totalSize;
    long lastSize;
    int type;   //1:内置SD卡   2：SD卡  3：USB


    public static final int TYPE_INNER = 1;
    public static final int TYPE_SD = 2;
    public static final int TYPE_USB = 3;

    public StoreEntity(String path, long totalSize, long lastSize, int type) {
        this.path = path;
        this.totalSize = totalSize;
        this.lastSize = lastSize;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public long getLastSize() {
        return lastSize;
    }

    public void setLastSize(long lastSize) {
        this.lastSize = lastSize;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
