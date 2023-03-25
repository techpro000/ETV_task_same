package com.udpsync.bean;

public class CmdData {

    public int cmd;   //指令类型
    public int pos;    //进度
    public int fileType;  //文件类型 图片视频
    public long time;     //发送时间的
    public String path;  //文件路径
    public String secenId;  //场景DI
    public String playParam;  //播放时间




    @Override
    public String toString() {
        return "CmdData{" +
                "cmd=" + cmd +
                ", pos=" + pos +
                ", fileType=" + fileType +
                ", time=" + time +
                ", path='" + path + '\'' +
                ", secenId='" + secenId + '\'' +
                ", playParam='" + playParam + '\'' +
                '}';
    }
}
