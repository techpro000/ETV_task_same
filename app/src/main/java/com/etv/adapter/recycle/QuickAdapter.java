package com.etv.adapter.recycle;

import java.util.List;

public abstract class QuickAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {

    public QuickAdapter() {
        super();
    }

    public QuickAdapter(int layoutRes) {
        super(layoutRes);
    }

    public QuickAdapter(int layoutRes, List<T> list) {
        super(layoutRes, list);
    }
}
