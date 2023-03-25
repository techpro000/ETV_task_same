package com.etv.adapter.recycle.listener;

import android.view.View;

import com.etv.adapter.recycle.BaseQuickAdapter;

public interface OnItemClickListener {
    void onItemClick(BaseQuickAdapter<?,?> baseAdapter, View view, int position);
}
