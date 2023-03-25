package com.etv.setting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.etv.activity.ClearCacheActivity;
import com.etv.config.AppInfo;
import com.etv.setting.adapter.StoreAdapter;
import com.etv.setting.entity.StoreEntity;
import com.etv.util.APKUtil;
import com.etv.util.MyLog;
import com.etv.util.sdcard.MySDCard;
import com.ys.etv.R;

import java.util.ArrayList;
import java.util.List;

public class StorageActivity extends SettingBaseActivity implements View.OnClickListener {

    SDBroadcast sdBroadcast;
    private TextView tv_devices_setting;
    private LinearLayout lin_exit;
    private Button btn_file_manager;
    private Button btn_clear_sdcard;

    private class SDBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_MEDIA_EJECT) || action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                MyLog.e("SDCARD", "检测到U盘插拔==========");
                startGoSearchSdInfo();
            }
        }
    }

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.fragment_storage);
        initView();
        initReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppInfo.startCheckTaskTag = false;
        startGoSearchSdInfo();
    }

    private void startGoSearchSdInfo() {
        pro_deal.setVisibility(View.VISIBLE);
        getSdInfoData();
    }

    ListView lv_sd_info;
    MySDCard mySdcard;
    List<StoreEntity> sdLists = new ArrayList<StoreEntity>();
    StoreAdapter adapter;
    ProgressBar pro_deal;

    private void initView() {
        pro_deal = (ProgressBar) findViewById(R.id.pro_deal);
        mySdcard = new MySDCard(StorageActivity.this);
        tv_devices_setting = (TextView) findViewById(R.id.tv_devices_setting);
        lv_sd_info = (ListView) findViewById(R.id.lv_sd_info);
        adapter = new StoreAdapter(StorageActivity.this, sdLists);
        lv_sd_info.setAdapter(adapter);
        lin_exit = (LinearLayout) findViewById(R.id.lin_exit);
        lin_exit.setOnClickListener(this);
        btn_file_manager = (Button) findViewById(R.id.btn_file_manager);
        btn_file_manager.setOnClickListener(this);
        btn_clear_sdcard = (Button) findViewById(R.id.btn_clear_sdcard);
        btn_clear_sdcard.setOnClickListener(this);
    }

    private void getSdInfoData() {
        try {
            sdLists.clear();
            List<String> listPath = mySdcard.getAllExternalStorage();
            if (listPath.size() < 1 || listPath == null) {
                tv_devices_setting.setText(getLanguageFromResurce(R.string.storage) + "( 0 )");
                return;
            }
            String sdPath = MySDCard.getSDcardPath(StorageActivity.this);
            for (int i = 0; i < listPath.size(); i++) {
                int saveType = StoreEntity.TYPE_USB;
                String path = listPath.get(i).toString();
                if (path.contains("emulated") || path.contains("internal")) {
                    saveType = StoreEntity.TYPE_INNER;
                } else {
                    if (sdPath != null && sdPath.contains(path)) {
                        saveType = StoreEntity.TYPE_SD;
                    } else {
                        saveType = StoreEntity.TYPE_USB;
                    }
                }
                sdLists.add(new StoreEntity(path, 0, 0, saveType));
                MyLog.cdl("=======sd=====path" + path);
            }
            tv_devices_setting.setText(getLanguageFromResurce(R.string.storage) + "( " + sdLists.size() + " )");
            adapter.setList(sdLists);
            getFileSize(sdLists);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getFileSize(List<StoreEntity> sdLists) {
        try {
            List<StoreEntity> listShow = new ArrayList<>();
            pro_deal.setVisibility(View.GONE);
            String sdPath = MySDCard.getSDcardPath(StorageActivity.this);

            for (int i = 0; i < sdLists.size(); i++) {
                int saveType = StoreEntity.TYPE_USB;
                String path = sdLists.get(i).getPath();
                long totalSize = 0;
                if (path.contains("emulated") || path.contains("internal")) {
                    totalSize = MySDCard.getRealSizeOfNand();
                    saveType = StoreEntity.TYPE_INNER;
                } else {
                    if (sdPath != null && sdPath.contains(path)) {
                        saveType = StoreEntity.TYPE_SD;
                    } else {
                        saveType = StoreEntity.TYPE_USB;
                    }
                    totalSize = mySdcard.getTotalExternalMemorySize(path, 1024 * 1024 * 1024);
                }
                long lastUseSize = mySdcard.getAvailableExternalMemorySize(path, 1024 * 1024);
                listShow.add(new StoreEntity(path, totalSize, lastUseSize, saveType));
            }
            adapter.setList(listShow);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lin_exit:
                finish();
                break;
            case R.id.btn_file_manager:
                APKUtil.openFileManagerApk(StorageActivity.this);
                break;
            case R.id.btn_clear_sdcard:
                startActivity(new Intent(StorageActivity.this, ClearCacheActivity.class));
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sdBroadcast != null) {
            unregisterReceiver(sdBroadcast);
        }
    }

    private void initReceiver() {
        sdBroadcast = new SDBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addDataScheme("file");
        registerReceiver(sdBroadcast, filter);
    }

}

