package com.etv.activity.sdcheck;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.etv.activity.BaseActivity;
import com.etv.activity.MainActivity;
import com.etv.adapter.SdCheckAdapter;
import com.etv.config.AppInfo;
import com.etv.entity.SdCheckEntity;
import com.etv.entity.StorageInfo;
import com.etv.task.db.DBTaskUtil;
import com.etv.util.FileUtil;
import com.etv.util.MyLog;
import com.etv.util.SharedPerManager;
import com.etv.util.sdcard.FileFilter;
import com.etv.util.sdcard.MySDCard;
import com.etv.util.system.CpuModel;
import com.ys.model.dialog.MyToastView;
import com.ys.etv.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/***
 * 用来检测SD卡插入动作
 */
public class SdCheckActivity extends BaseActivity implements SdCheckView {

    MySDCard mySDCard;
    private ListView lv_sd_info;
    SdCheckAdapter adapter;
    List<SdCheckEntity> list = new ArrayList<>();
    private static final int FINISH_TIME_DISTANCE = 5000;
    public static final String TAG_CHECK_URL = "TAG_CHECK_URL";

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
                MyLog.e("SDCARD", "检测到U盘插拔==========");
                if (sdCheckParsener != null) {
                    sdCheckParsener.stopZipFile();
                }
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_sd_check);
        initView();
        getDataInfo();
        initReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppInfo.startCheckTaskTag = false;
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addDataScheme("file");
        registerReceiver(receiver, filter);
    }

    SdCheckParsener sdCheckParsener;
    private LinearLayout lin_progress;
    private TextView tv_progress;
    private ProgressBar progress_write;
    String checkUrl;

    private void initView() {
        Intent intent = getIntent();
        checkUrl = intent.getStringExtra(TAG_CHECK_URL);
        mySDCard = new MySDCard(SdCheckActivity.this);
        lv_sd_info = (ListView) findViewById(R.id.lv_sd_info);
        adapter = new SdCheckAdapter(SdCheckActivity.this, list);
        lv_sd_info.setAdapter(adapter);
        lin_progress = (LinearLayout) findViewById(R.id.lin_progress);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        progress_write = (ProgressBar) findViewById(R.id.progress_write);
        addInfoToList(getLanguageFromResurce(R.string.start_ckeckout));
        sdCheckParsener = new SdCheckParsener(SdCheckActivity.this, this);
    }

    private void getDataInfo() {
        if (!CpuModel.getMobileType().startsWith(CpuModel.CPU_MODEL_3568_11)) {
            List<String> list = mySDCard.getAllExternalStorage();  //获取所有的磁盘路径
            if (list.size() < 2 || list == null) {
                setThreeClose(getString(R.string.no_sdmemory));
                return;
            }
        }
        StorageInfo storageInfoType = FileFilter.jujleFileIsExict(SdCheckActivity.this, checkUrl);
        int exDevType = storageInfoType.getSdType();
        if (exDevType == StorageInfo.TYPE_USB) {
            addInfoToList(getString(R.string.check_usb));
        } else if (exDevType == StorageInfo.TYPE_SD) {
            addInfoToList(getString(R.string.check_sd));
        }
        String filePath = storageInfoType.getPath();
        if (filePath == null || filePath.length() < 2) {
            setThreeClose(getString(R.string.no_filematch));
            return;
        }
        String desc = getLanguageFromResurceWithPosition(R.string.check_info, filePath);
        addInfoToList(desc);
        StorageInfo storageInfo = FileUtil.getStorageFileAction(filePath);
        if (storageInfo == null) {
            MyLog.sdckeck("=======action====storageInfo==null=");
            addInfoToList(getLanguageFromResurce(R.string.get_actionfailed));
            return;
        }
        MyLog.sdckeck("=======action==" + storageInfo.toString());
        int action = storageInfo.getAction();
        MyLog.sdckeck("=======action=====" + action);
        if (action == StorageInfo.ACTION_MODIFY_IP) {  //修改IP
            try {
                addInfoToList("\nModify working mode to network distribution\n", true);
                addInfoToList(getLanguageFromResurce(R.string.modify_action_ip));
                //因为替换了Apk所以需要删除任务
                String taskPath = AppInfo.BASE_TASK_URL();
                FileUtil.deleteDirOrFilePath(taskPath, "U盘修改IP==StorageInfo.ACTION_MODIFY_IP");
                DBTaskUtil.clearAllDbInfo("");
                sdCheckParsener.modifySharedIpAddress(filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (action == StorageInfo.ACTION_INSTALL_ETV_APK) {  //更新APK
            lin_progress.setVisibility(View.VISIBLE);
            addInfoToList(getLanguageFromResurce(R.string.modify_action_apk));
            sdCheckParsener.installApk(filePath);
        } else if (action == StorageInfo.ACTION_VOICE_MEDIA) {  //触沃-语音替换
            addInfoToList(getLanguageFromResurce(R.string.action_modify_welcome));
            lin_progress.setVisibility(View.VISIBLE);
            sdCheckParsener.modifyWelcomeMediaFile(filePath);
        }
    }

    @Override
    public void setThreeClose(String desc) {
        MyLog.cdl("===desc=" + desc);
        addInfoToList(desc + getLanguageFromResurce(R.string.close_three_min), true);
        handler.sendEmptyMessageDelayed(FINISH_MY_SELF, FINISH_TIME_DISTANCE);
    }

    private static final int FINISH_MY_SELF = 89;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case FINISH_MY_SELF:
                    startToMainActivity();
                    break;
            }
        }
    };

    private void startToMainActivity() {
        MainActivity.IS_ORDER_REQUEST_TASK = true;
        startActivity(new Intent(SdCheckActivity.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sdCheckParsener != null) {
            sdCheckParsener.stopZipFile();
        }
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        if (handler != null) {
            handler.removeMessages(FINISH_MY_SELF);
        }
    }

    @Override
    public void addInfoToList(String info) {
        addInfoToList(info, false);
    }

    public void addInfoToList(String info, boolean isRed) {
        MyLog.cdl("===info=" + info, true);
        list.add(new SdCheckEntity(info, list.size() + 1 + "", isRed));
        adapter.setList(list);
    }

    @Override
    public void writeFileProgress(int progress) {
        progress_write.setProgress(progress);
        tv_progress.setText(progress + " %");
    }

    @Override
    public void showToastView(String toast) {
        MyToastView.getInstance().Toast(SdCheckActivity.this, toast);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (sdCheckParsener != null) {
                sdCheckParsener.stopZipFile();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
