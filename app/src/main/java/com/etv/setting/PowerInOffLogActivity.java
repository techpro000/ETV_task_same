package com.etv.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.etv.config.AppInfo;
import com.etv.db.PowerOnOffLogDb;
import com.etv.entity.PoOnOffLogEntity;
import com.etv.setting.adapter.PowerSaveAdapter;
import com.etv.util.FileUtil;
import com.ys.model.dialog.MyToastView;
import com.ys.etv.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/***
 * 定时开关机日志界面
 */
public class PowerInOffLogActivity extends SettingBaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_hiddle_view);
        initView();
    }

    ListView list_info;
    List<PoOnOffLogEntity> list_log = new ArrayList<>();
    PowerSaveAdapter adapter;
    private Button btn_load_out;
    private Button btn_clear;
    LinearLayout lin_exit;
    TextView tv_exit;

    private void initView() {
        AppInfo.startCheckTaskTag = false;
        list_info = (ListView) findViewById(R.id.list_hiddle);
        adapter = new PowerSaveAdapter(this, list_log);
        list_info.setAdapter(adapter);
        btn_clear = (Button) findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(this);
        btn_load_out = (Button) findViewById(R.id.btn_load_out);
        btn_load_out.setOnClickListener(this);

        lin_exit = (LinearLayout) findViewById(R.id.lin_exit);
        lin_exit.setOnClickListener(this);
        tv_exit = (TextView) findViewById(R.id.tv_exit);
        tv_exit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lin_exit:
            case R.id.tv_exit:
                finish();
                break;
            case R.id.btn_clear:
                PowerOnOffLogDb.clearAllData();
                getDataInfo();
                break;
            case R.id.btn_load_out:
                writeTxtSd();
                break;
        }
    }

    private void writeTxtSd() {
        try {
            if (list_log == null || list_log.size() < 1) {
                MyToastView.getInstance().Toast(PowerInOffLogActivity.this, getLanguageFromResurce(R.string.no_data));
                return;
            }
            String fileName = "/sdcard/powerOnOFF.txt";
            File file = new File(fileName);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            for (int i = 0; i < list_log.size(); i++) {
                PoOnOffLogEntity poOnOffInfo = list_log.get(i);
                FileUtil.writeMessageInfoToTxt(fileName, poOnOffInfo.toString());
            }
            MyToastView.getInstance().Toast(PowerInOffLogActivity.this, getLanguageFromResurce(R.string.load_success) + "Path:" + fileName);
        } catch (IOException e) {
            MyToastView.getInstance().Toast(PowerInOffLogActivity.this, getLanguageFromResurce(R.string.load_failed));
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataInfo();
    }

    private void getDataInfo() {
        list_log.clear();
        adapter.setList(list_log);
        list_log = PowerOnOffLogDb.getPowerInfoList();
        if (list_log == null || list_log.size() < 1) {
            MyToastView.getInstance().Toast(PowerInOffLogActivity.this, getLanguageFromResurce(R.string.no_data));
            return;
        }
        adapter.setList(list_log);
    }
}
