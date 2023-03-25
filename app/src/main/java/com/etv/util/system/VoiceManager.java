package com.etv.util.system;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;

import com.etv.util.MyLog;

/**
 * 音量控制管理器
 */
public class VoiceManager {

    Context context;
    public static VoiceManager Instance;

    public static VoiceManager getInstance(Context context) {
        if (Instance == null) {
            synchronized (VoiceManager.class) {
                Instance = new VoiceManager(context);
            }
        }
        return Instance;
    }

    AudioManager mAudioManager = null;

    public VoiceManager(Context context) {
        this.context = context;
        mAudioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
    }


    public int getCurrentVoiceNum() {
        int mediacurrent = 7;
        int maxVoice = 15;
        try {
            mediacurrent = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            maxVoice = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyLog.cdl("====voice===获取媒体得音量====" + mediacurrent + " / " + maxVoice);
        return mediacurrent;
    }

    /**
     * 按照比例来修改音量
     *
     * @param progress
     */
    public void repairDevVoice(String progress) {
        try {
            int progressNum = Integer.parseInt(progress);
            if (progressNum == 0) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                return;
            }
            int voiceNum = (progressNum * 15) / 100;
            MyLog.cdl("========voice===repairDevVoice=====" + voiceNum);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, voiceNum, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * @param currentMediaNum
     * 1-15之间测值
     */
    public void setMediaVoiceNum(int currentMediaNum) {
        MyLog.cdl("========voice====setMediaVoiceNum====" + +currentMediaNum);
        try {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentMediaNum, 0);  //设置媒体音量为 0
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}