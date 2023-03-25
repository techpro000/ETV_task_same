package com.etv.adapter.recycle.listener;

import android.view.View;

import com.etv.adapter.recycle.BaseQuickAdapter;

public interface OnItemChildLongClickListener {
    boolean onItemChildLongClick(BaseQuickAdapter<?,?> baseAdapter, View view, int position);
}
