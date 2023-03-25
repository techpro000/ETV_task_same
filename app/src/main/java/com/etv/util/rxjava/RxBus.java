package com.etv.util.rxjava;


import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class RxBus {

    private static volatile RxBus mInstance;
    private final Subject bus;

    private RxBus() {
        bus = PublishSubject.create().toSerialized();
    }

    /**
     * 单例模式RxBus
     *
     * @return Rxbus对象
     */
    public static RxBus getInstance() {
        RxBus rxBus2 = mInstance;
        if (mInstance == null) {
            synchronized (RxBus.class) {
                rxBus2 = mInstance;
                if (mInstance == null) {
                    rxBus2 = new RxBus();
                    mInstance = rxBus2;
                }
            }
        }
        return rxBus2;
    }


    /**
     * 发送消息
     */
    public void post(Object object) {
        bus.onNext(object);
    }

    /**
     * 接收消息
     */
    public <T> Observable<T> toObserverable(Class<T> eventType) {
        return bus.ofType(eventType);
    }

}