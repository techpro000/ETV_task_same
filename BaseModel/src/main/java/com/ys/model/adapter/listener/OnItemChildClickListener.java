package com.ys.model.adapter.listener;

import android.view.View;

import com.ys.model.adapter.BaseQuickAdapter;

public interface OnItemChildClickListener {
    void onItemChildClick(BaseQuickAdapter<?,?> baseAdapter, View view, int position);
}
