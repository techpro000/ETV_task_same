package com.etv.util;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class ViewSizeChange {

    public static void setMainLogoPosition(LinearLayout linearLayout) {
        int width = SharedPerManager.getScreenWidth();
        int height = SharedPerManager.getScreenHeight();
        if (width - height > 0) { //横屏
            setScreenHroView(linearLayout);
        } else {
            setScreenVerView(linearLayout);
        }
    }

    private static void setScreenVerView(LinearLayout linearLayout) {
        int height = SharedPerManager.getScreenHeight();
        height = height / 2;
        int viewHeight = 270;
        MyLog.cdl("=====view的高度111===" + viewHeight);
        RelativeLayout.LayoutParams localLayoutParams = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
        localLayoutParams.topMargin = height - viewHeight * 3 / 2;
        linearLayout.setLayoutParams(localLayoutParams);
    }

    private static void setScreenHroView(LinearLayout linearLayout) {
        int height = SharedPerManager.getScreenHeight();
        height = height / 2;
        int viewHeight = 150;
        MyLog.cdl("=====view的高度000===" + viewHeight);
        RelativeLayout.LayoutParams localLayoutParams = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
        localLayoutParams.topMargin = height - 250;
        linearLayout.setLayoutParams(localLayoutParams);
    }

    public static void setRecycleExcelView(LinearLayout view, int size, int viewWidth) {
        int widthShow = viewWidth / size;
        RelativeLayout.LayoutParams localLayoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        localLayoutParams.width = widthShow;
        localLayoutParams.height = 50;
        view.setLayoutParams(localLayoutParams);
    }

    public static void setLogoPosition(ImageView iv_logo_show) {
        int height = SharedPerManager.getScreenHeight();
        int hei_half = height / 2;
        int distance = hei_half / 3;
        int top_mar = hei_half - distance;
        RelativeLayout.LayoutParams localLayoutParams = (RelativeLayout.LayoutParams) iv_logo_show.getLayoutParams();
        localLayoutParams.width = 150;
        localLayoutParams.height = 150;
        localLayoutParams.topMargin = top_mar;
        iv_logo_show.setLayoutParams(localLayoutParams);
    }


}
