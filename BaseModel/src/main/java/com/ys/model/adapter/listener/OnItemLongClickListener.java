package com.ys.model.adapter.listener;

import android.view.View;

import com.ys.model.adapter.BaseQuickAdapter;

public interface OnItemLongClickListener {
    boolean onItemLongClick(BaseQuickAdapter<?,?> adapter, View view, int position);
}
