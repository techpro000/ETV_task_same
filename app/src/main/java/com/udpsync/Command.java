package com.udpsync;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONValidator;
import com.etv.test.StartUploadEntity;
import com.udpsync.bean.CmdData;

public class Command {

    /***
     * 开始播放
     * @param path
     * @return
     */
    public static String buildPlayCmd(int fileType, String playParam, String path, String secenId, int playPos) {
        return JsonBuilder.newBuilder()
                .add("cmd", CmdAct.CMD_PLAY)
                .add("fileType", fileType)
                .add("playParam", playParam)
                .add("secenId", secenId)
                .add("path", path)
                .add("pos", playPos)
                .toString();
    }

    /**
     * 播放进度
     *
     * @param path
     * @param progress
     * @return
     */
    public static String buildProgressCmd(String path, int progress, String sencenId) {
        return JsonBuilder.newBuilder()
                .add("cmd", CmdAct.CMD_PROGRESS)
                .add("path", path)
                .add("time", System.currentTimeMillis())
                .add("pos", progress)
                .add("secenId", sencenId)
                .toString();
    }


    /***
     * 同步进度的
     * @param path
     * @param progress
     * @return
     */
    public static String buildSeekProgressCmd(String path, int progress, String sencenId) {
        return JsonBuilder.newBuilder()
                .add("cmd", CmdAct.CMD_SEEK_PROGRESS)
                .add("path", path)
                .add("time", System.currentTimeMillis())
                .add("pos", progress)
                .add("secenId", sencenId)
                .toString();
    }

    public static CmdData getData(String dataStr) {
        try {
            return JSON.parseObject(dataStr, CmdData.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
