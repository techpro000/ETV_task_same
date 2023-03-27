package com.ys.model.adapter.listener;

import android.view.View;

import com.ys.model.adapter.BaseQuickAdapter;

public interface OnItemChildLongClickListener {
    boolean onItemChildLongClick(BaseQuickAdapter<?,?> baseAdapter, View view, int position);
}
