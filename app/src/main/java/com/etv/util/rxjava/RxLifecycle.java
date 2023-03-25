package com.etv.util.rxjava;


import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/***
 * 自定义生命周期
 * 放置内存泄漏
 * @param <T>
 */
public class RxLifecycle<T> implements LifecycleObserver, ObservableTransformer<T, T> {

    public static <T> RxLifecycle<T> bindRxLifecycle(LifecycleOwner lifecycleOwner) {
        RxLifecycle<T> lifecycle = new RxLifecycle<>();
        lifecycleOwner.getLifecycle().addObserver(lifecycle);
        return lifecycle;
    }

    final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy() {
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream.doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(@NonNull Disposable disposable) throws Exception {
                compositeDisposable.add(disposable);
            }
        });
    }
}
