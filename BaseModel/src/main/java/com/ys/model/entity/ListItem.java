package com.ys.model.entity;

public class ListItem {
    public int flag;
    public String name;

    public ListItem() {
    }

    public ListItem(String name) {
        this.name = name;
    }

    public ListItem(int flag, String name) {
        this.flag = flag;
        this.name = name;
    }
}
