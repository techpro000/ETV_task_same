package com.etv.entity;

import org.litepal.crud.LitePalSupport;

public class StatisticsEntity extends LitePalSupport {

    String mtid;
    String addtype;
    int pmtime;
    int count;
    long createtime;

    public StatisticsEntity(String mtid, String addtype, int pmtime, int count, long createtime) {
        this.mtid = mtid;
        this.addtype = addtype;
        this.pmtime = pmtime;
        this.count = count;
        this.createtime = createtime;
    }

    @Override
    public String toString() {
        return "StatisticsEntity{" +
                "mtid='" + mtid + '\'' +
                ", addtype='" + addtype + '\'' +
                ", pmtime=" + pmtime +
                ", count=" + count + ", createtime=" + createtime +
                '}';
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public String getMtid() {
        return mtid;
    }

    public void setMtid(String mtid) {
        this.mtid = mtid;
    }

    public String getAddtype() {
        return addtype;
    }

    public void setAddtype(String addtype) {
        this.addtype = addtype;
    }

    public int getPmtime() {
        return pmtime;
    }

    public void setPmtime(int pmtime) {
        this.pmtime = pmtime;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
