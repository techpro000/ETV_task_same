package com.etv.util;

import com.etv.listener.TimeChangeListener;
import com.etv.view.layout.Generator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class TimerDealUtil {

    public static TimerDealUtil instance;
    TimeChangeListener timeChangeListener;
    Disposable mDisposable;
    List<Generator> list = new ArrayList<>();
    public static boolean isTimerRun = true;  //用来执行 暂停 和恢复的

    public static TimerDealUtil getInstance() {
        if (instance == null) {
            synchronized (TimerDealUtil.class) {
                if (instance == null) {
                    instance = new TimerDealUtil();
                }
            }
        }
        return instance;
    }

    long classIdBack;

    public void setTimeChangeListener(long classId, TimeChangeListener timeChangeListener) {
        this.classIdBack = classId;
        this.timeChangeListener = timeChangeListener;
    }

    public void startToTimerMinRxJava() {
        Observable.interval(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        mDisposable = disposable;
                    }

                    @Override
                    public void onNext(@NonNull Long number) {
                        notifyAllGeneratorInfo();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void notifyAllGeneratorInfo() {
        if (!isTimerRun) {
            log("==========TimerDealUtil========刷新时间====000=");
            return;
        }
        if (timeChangeListener != null && classIdBack > 0) {
            //这里是给 界面调用的
            timeChangeListener.timeChangeMin(classIdBack);
        }
        if (list == null || list.size() < 1) {
            log("==========TimerDealUtil========刷新时间====111=");
            return;
        }
        log("==========TimerDealUtil========刷新时间===222==");
        try {
            for (Generator generator : list) {
                if (generator != null) {
                    generator.timeChangeToUpdateView();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void log(String s) {
//        MyLog.cdl(s);
    }

    /***
     * 用来管理播放，暂停的
     * @param isTimer
     * true   恢复
     * false  暂停
     */
    public void pauseOrResumeTimerInfo(boolean isTimer) {
        isTimerRun = isTimer;
    }

    public void addGeneratorToList(Generator generator) {
        log("==========TimerDealUtil=====addGeneratorToList========" + generator.getClass().getName());
        if (list == null) {
            list = new ArrayList<Generator>();
        }
        pauseOrResumeTimerInfo(true);
        list.add(generator);
    }

    public void removeGeneratorToList(Generator generator) {
        pauseOrResumeTimerInfo(true);
        log("==========TimerDealUtil======removeGeneratorToList=======" + generator.getClass().getName());
        if (list != null) {
            list.remove(generator);
        }
    }

    public void onDestroyTimer() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        removeAllGenerator();
    }

    public void removeAllGenerator() {
        if (list == null || list.size() < 1) {
            return;
        }
        list.clear();
    }
}
