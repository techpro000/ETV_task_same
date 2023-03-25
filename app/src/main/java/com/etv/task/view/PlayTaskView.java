package com.etv.task.view;

import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.etv.task.entity.CpListEntity;

import java.util.List;

public interface PlayTaskView {

    void findTaskNew();

    /***
     * 获取信息或者显示信息异常
     * @param errorInfo
     */
    void showViewError(String errorInfo);

    /**
     * 显示Toast view
     *
     * @param toast
     */
    void showToastView(String toast);

    /***
     * 获取跟布局
     * @return
     */
    AbsoluteLayout getAbsoluteLayout();


}
