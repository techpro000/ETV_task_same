//package com.etv.util.tts;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.speech.tts.TextToSpeech;
//import android.speech.tts.UtteranceProgressListener;
//
//import com.etv.util.MyLog;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class TtsManager {
//
//    Context context;
//
//    public TtsManager(Context context) {
//        this.context = context;
//        init();
//    }
//
//    private TextToSpeech mSpeech;
//    private boolean mIsInited;
//
//    public void init() {
//        mSpeech = new TextToSpeech(context, status -> {
//            if (status == TextToSpeech.SUCCESS) {
//                mIsInited = true;
//                mSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
//                    @Override
//                    public void onStart(String utteranceId) {
//                        MyLog.tts("============onStart=======");
//                    }
//
//                    @Override
//                    public void onDone(String utteranceId) {
//                        MyLog.tts("============onDone=======");
//                        startToNextPosition();
//                    }
//
//                    @Override
//                    public void onError(String utteranceId) {
//                        MyLog.tts("============onError=======" + utteranceId);
//                    }
//                });
//            }
//        });
//    }
//
//    private List<String> stringList;
//
//    public void addSpeechMessageToList(String messageDesc) {
//        if (stringList == null) {
//            stringList = new ArrayList<String>();
//        }
//        stringList.add(messageDesc);
//        if (isSpeaking()) {
//            return;
//        }
//        speakText(stringList.get(0));
//    }
//
//    private void startToNextPosition() {
//        if (stringList == null || stringList.size() < 1) {
//            return;
//        }
//        stringList.remove(0);
//        if (stringList == null || stringList.size() < 1) {
//            return;
//        }
//        speakText(stringList.get(0));
//    }
//
//    @SuppressLint("NewApi")
//    public boolean speakText(String text) {
//        if (!mIsInited) {
//            MyLog.tts("语音合成失败，未初始化成功");
//            init();
//            return false;
//        }
//        MyLog.tts("======startToSpeak=====speak=" + text, true);
//        if (text == null || text.length() < 1) {
//            startToNextPosition();
//            return false;
//        }
//        if (mSpeech != null) {
//            int result = mSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "");
//            return result == TextToSpeech.SUCCESS;
//        }
//        return false;
//    }
//
//    public void stop() {
//        if (stringList != null) {
//            stringList.clear();
//        }
//        if (mSpeech != null && mSpeech.isSpeaking()) {
//            mSpeech.stop();
//        }
//    }
//
//    public boolean isSpeaking() {
//        if (mSpeech == null)
//            return false;
//        return mSpeech.isSpeaking();
//    }
//
//    public void destory() {
//        if (mSpeech != null) {
//            mSpeech.stop();
//            mSpeech.shutdown();
//        }
//    }
//}
