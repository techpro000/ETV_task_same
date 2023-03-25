package com.udpsync.observe;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

public class Owner implements LifecycleOwner {

    private final LifecycleRegistry registry;

    private Owner() {
        registry = new LifecycleRegistry(this);
        registry.setCurrentState(Lifecycle.State.RESUMED);
    }

    public static Owner create() {
        return new Owner();
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return registry;
    }
}
