package com.etv.entity;

public class SameScreen {

    public Data clientSameScreen;

    public static class Data {
        // 同屏任务分组id
        public int sgId;
        // 是不是主屏幕 1是 2不是
        public int isMaster;
        public int rowNum;
        public int columnNum;
        // 排版位置序号;
        public int serialNum;
    }
}
