package com.etv.setting.entity;

public class RowCow {
    public int row;
    public int cow;
    public int position;

    public boolean check;

    public RowCow(int row, int cow, int position) {
        this(row, cow, position, false);
    }

    public RowCow(int row, int cow, int position, boolean check) {
        this.row = row;
        this.cow = cow;
        this.position = position;
        this.check = check;
    }

    public String getPositionStr() {
        return "" + (position + 1);
    }
}
