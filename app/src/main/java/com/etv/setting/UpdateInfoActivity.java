package com.etv.setting;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etv.activity.BaseActivity;
import com.etv.config.AppInfo;
import com.ys.etv.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class UpdateInfoActivity extends SettingBaseActivity {

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_update_info);
        initView();
    }

    TextView tv_update_desc;

    private void initView() {
        AppInfo.startCheckTaskTag = false;
        tv_update_desc = (TextView) findViewById(R.id.tv_update_desc);
        String updateInfo = getInfoFromAsset();
        tv_update_desc.setText(updateInfo);
    }

    /***
     * 从asset中获取字符串
     * @return
     */
    private String getInfoFromAsset() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open("updateInfo.txt")));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append("\n" + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
