package com.udpsync.observe;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.udpsync.bean.CmdData;


public class CmdObserver {

    private final MutableLiveData<CmdData> liveData = new MutableLiveData<>();

    private CmdObserver() {

    }

    public static CmdObserver get() {
        return Holder.OBSERVER;
    }

    public MutableLiveData<CmdData> getLiveData() {
        return liveData;
    }

    public void setValue(CmdData data) {
        liveData.setValue(data);
    }

    public void postValue(CmdData data) {
        liveData.postValue(data);
    }

    public void addObserver(Observer<CmdData> observer) {
        liveData.observe(Owner.create(), observer);
    }

    public void removeObserver(Observer<CmdData> observer) {
        liveData.removeObserver(observer);
    }

    private static class Holder {
        private static final CmdObserver OBSERVER = new CmdObserver();
    }
}
