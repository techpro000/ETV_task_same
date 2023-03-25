package com.etv.adapter.recycle.listener;

import android.view.View;

import com.etv.adapter.recycle.BaseQuickAdapter;

public interface OnItemChildClickListener {
    void onItemChildClick(BaseQuickAdapter<?,?> baseAdapter, View view, int position);
}
