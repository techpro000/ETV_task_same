package com.etv.adapter.recycle.listener;

import android.view.View;

import com.etv.adapter.recycle.BaseQuickAdapter;

public interface OnItemLongClickListener {
    boolean onItemLongClick(BaseQuickAdapter<?,?> adapter, View view, int position);
}
