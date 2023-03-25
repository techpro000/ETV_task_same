package com.etv.activity;

import android.os.Bundle;

import com.ys.etv.R;

public class TestActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initView();
    }

//    VLCSurfaceView surfaceView_vlc;

    private void initView() {
//        surfaceView_vlc = (VLCSurfaceView) findViewById(R.id.surfaceView_vlc);
//        surfaceView_vlc.startPlayUrl("rtsp://192.168.13.6/test");
//      surfaceView_vlc.startPlayUrl("rtsp://192.168.4.3/test");
    }

}