package com.etv.adapter.recycle;

import android.util.SparseIntArray;

import androidx.annotation.LayoutRes;

public abstract class MultiItemType<T> {
    private final SparseIntArray layouts = new SparseIntArray();

    public int getLayoutId(int type) {
        return layouts.get(type);
    }

    public void addItemType(int type, @LayoutRes int layoutRes) {
        layouts.put(type, layoutRes);
    }

    public abstract int getItemType(T item, int position);
}
