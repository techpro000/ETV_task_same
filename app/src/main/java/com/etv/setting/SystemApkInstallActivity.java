package com.etv.setting;

import android.os.Bundle;
import android.os.Handler;

import com.etv.activity.BaseActivity;
import com.etv.util.APKUtil;
import com.ys.etv.R;

import java.io.File;

public class SystemApkInstallActivity extends BaseActivity {

    public static final String FILE_UPDATE_PATH = "FILE_UPDATE_PATH";

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_apk_install);
        getDateFromIntent();
    }

    private void getDateFromIntent() {
        String filePath = getIntent().getStringExtra(FILE_UPDATE_PATH);
        File file = new File(filePath);
        if (!file.exists()) {
            showToastView("File does not exist");
            finishMyShelf();
            return;
        }
        APKUtil apkUtil = new APKUtil(SystemApkInstallActivity.this);
        apkUtil.installApk(filePath);
        finishMyShelf();
    }

    private void finishMyShelf() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }
}
