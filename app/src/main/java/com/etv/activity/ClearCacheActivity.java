package com.etv.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.etv.config.AppInfo;
import com.etv.util.MyLog;
import com.etv.util.sdcard.FileFilter;
import com.etv.util.sdcard.MySDCard;
import com.ys.etv.R;

import java.util.List;

public class ClearCacheActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear);
        initView();
        MyLog.cdl("====开始清理");
    }

    private void initView() {
        AppInfo.startCheckTaskTag = false;
        handler.sendEmptyMessageDelayed(MESSAGE_CLEAR_CACHE, 5000);
        clearInnerPath();
        try {
            MySDCard mySDCard = new MySDCard(ClearCacheActivity.this);
            List<String> saveList = mySDCard.getAllExternalStorage();
            if (saveList == null || saveList.size() < 2) {  //只有一个存储设备
                MyLog.cdl("====目前只有内置SD卡");
                return;
            }
            String sdPath = MySDCard.getSDcardPath(ClearCacheActivity.this);            //外置SD卡
            MyLog.cdl("====sdPath=====" + sdPath);
            if (sdPath != null || sdPath.length() > 5) {
                clearOutSdPath();
            }
        } catch (Exception e) {
            MyLog.e("sdcard", "=======" + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppInfo.startCheckTaskTag = false;
    }


    private static final int MESSAGE_CLEAR_CACHE = 5645;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MESSAGE_CLEAR_CACHE) {
                handler.removeMessages(MESSAGE_CLEAR_CACHE);
                finish();
            }
        }
    };

    /***
     * 清理
     */
    private void clearOutSdPath() {
        MyLog.cdl("====清理外置SD卡=====");
        FileFilter.delFileFromPath(AppInfo.BASE_SD_PATH(), -1);
    }

    /***
     * 清理内存卡信息
     */
    private void clearInnerPath() {
        FileFilter.delFileFromPath(AppInfo.BASE_PATH_INNER, -1);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (handler != null) {
            handler.removeMessages(MESSAGE_CLEAR_CACHE);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeMessages(MESSAGE_CLEAR_CACHE);
        }
        MyLog.cdl("====开始清理--清理完成");
    }
}