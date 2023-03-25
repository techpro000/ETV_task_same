package com.etv.listener;

public interface VideoPlayListener {

    void initOver();

    /**
     * @param tag 正常情况都是正数
     *            特俗形况是负数，主要是正对负数得情况
     */
    void playCompletion(String tag);

    /***
     * splash界面专项接口\
     * 播放时长
     */
    void playCompletionSplash(int position, int playTimeCurrent);

    void playError(String errorDesc);

    /**
     * 播放出错，停止播放，显示nodata
     *
     * @param errorDesc
     */
    void playErrorToStop(String errorDesc);

    /**
     * 播放出错了，这里重启y一次播放一次
     *
     * @param errorDesc
     */
    void reStartPlayProgram(String errorDesc);


}
