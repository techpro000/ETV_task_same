package com.etv.activity.view;

import android.widget.ImageView;
import android.widget.TextView;

public interface MainView {

    //更新桌面背景图片
    void updateBggImageView(String tag);

    ImageView getIvlineState();

    ImageView getIvWorkModel();

    TextView getTimeView();

    void startToCheckTaskToActivity(String tag);

    ImageView getIvWifiState();
}
