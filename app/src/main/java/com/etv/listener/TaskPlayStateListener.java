package com.etv.listener;

import com.etv.task.entity.CpListEntity;

import java.util.List;

/**
 * 任务播放状态回调
 */
public interface TaskPlayStateListener {

    int TAG_PLAY_TIME_REDUCE = -1;  //倒计时
    int TAG_PLAY_VIDEO = 0;  //视频
    int TAG_PLAY_VIDEO_IMAGE = 1;  //混播
    int TAG_PLAY_AUDIO = 2;  //音频
    int TAG_PLAY_PICTURE = 3; //图片
    int TAG_PLAY_WPS = 4;    //文本
    int TAG_PLAY_IMAGE_MIX = 5;    //混播里面得图片


    public static String getPlayTag(int tag) {
        String backInfo = "";
        switch (tag) {
            case TAG_PLAY_IMAGE_MIX:
                backInfo = "混播图片";
                break;
            case TAG_PLAY_TIME_REDUCE:
                backInfo = "倒计时";
                break;
            case TAG_PLAY_VIDEO:
                backInfo = "视频";
                break;
            case TAG_PLAY_VIDEO_IMAGE:
                backInfo = "混播";
                break;
            case TAG_PLAY_AUDIO:
                backInfo = "音频";
                break;
            case TAG_PLAY_PICTURE:
                backInfo = "图片";
                break;
            case TAG_PLAY_WPS:
                backInfo = "文本";
                break;

        }
        return backInfo;
    }

    /**
     * 播放完毕回调
     *
     * @param playTag 回调得控件
     *                1：表示全部播放我那笔
     *                -1：表示只有一个播放完毕
     */
    void playComplete(int playTag);

    /**
     * 用来标记混播同步的回调接口
     *
     * @param etLevel             播放模式
     * @param taskId              任务ID
     * @param currentPlayPosition 当前播放的位置
     * @param playTag             播放标记
     */
    void playCompletePosition(String etLevel, String taskId, int currentPlayPosition, int playTag);

    /**
     * 播放异常，重启一次
     *
     * @param errorDesc
     */
    void reStartPlayProgram(String errorDesc);
}
