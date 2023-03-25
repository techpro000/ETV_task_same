package com.etv.setting;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.etv.config.AppInfo;
import com.etv.entity.BeanEntity;
import com.etv.setting.adapter.SingleShowAdapter;
import com.etv.setting.framenew.SingleWorkFragment;
import com.etv.setting.parsener.SingleParsener;
import com.etv.util.MyLog;
import com.etv.util.SharedPerManager;
import com.etv.util.system.SystemManagerUtil;
import com.ys.etv.R;
import com.ys.model.dialog.MyToastView;
import com.ys.model.dialog.OridinryDialog;
import com.ys.model.listener.OridinryDialogClick;

import java.util.ArrayList;
import java.util.List;

public class SingleSettingActivity extends SettingBaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    String SCREEN_TAG_CURRENT = AppInfo.PROGRAM_POSITION_MAIN;

    public static final String SCREEN_TAG = "SCREEN_TAG";

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_single);
        initView();
    }

    GridView grid_layout;
    SingleShowAdapter adapter;
    List<BeanEntity> lists = new ArrayList<BeanEntity>();
    LinearLayout lin_exit;
    TextView tv_exit;

    private void initView() {
        AppInfo.startCheckTaskTag = false;
        Intent intent = getIntent();
        SCREEN_TAG_CURRENT = intent.getStringExtra(SCREEN_TAG);
        lists = SingleParsener.getLayoutList(SingleSettingActivity.this, SCREEN_TAG_CURRENT);
        grid_layout = (GridView) findViewById(R.id.grid_layout);
        boolean isHorVer = SystemManagerUtil.isScreenHorOrVer(SingleSettingActivity.this, SCREEN_TAG_CURRENT);
        adapter = new SingleShowAdapter(SingleSettingActivity.this, lists, SCREEN_TAG_CURRENT, isHorVer);
        grid_layout.setAdapter(adapter);
        grid_layout.setOnItemClickListener(this);
        lin_exit = (LinearLayout) findViewById(R.id.lin_exit);
        tv_exit = (TextView) findViewById(R.id.tv_exit);
        lin_exit.setOnClickListener(this);
        tv_exit.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        BeanEntity beanEntity = lists.get(position);
        int tagId = beanEntity.getTagId();
        showModifyDialog(tagId);
    }

    private void showModifyDialog(final int tagId) {
        OridinryDialog oridinAryDialog = new OridinryDialog(SingleSettingActivity.this);
        oridinAryDialog.setOnDialogClickListener(new OridinryDialogClick() {
            @Override
            public void sure() {
                if (SCREEN_TAG_CURRENT.contains(AppInfo.PROGRAM_POSITION_MAIN)) {
                    SharedPerManager.setSingleLayoutTag(tagId);
                } else if (SCREEN_TAG_CURRENT.contains(AppInfo.PROGRAM_POSITION_SECOND)) {
                    SharedPerManager.setSingleSecondLayoutTag(tagId);
                }
                MyToastView.getInstance().Toast(SingleSettingActivity.this, getLanguageFromResurce(R.string.set_success));
                SingleWorkFragment.autoUpdate = true;
                finish();
            }

            @Override
            public void noSure() {

            }
        });
        oridinAryDialog.show(getLanguageFromResurce(R.string.sure_modify_layout), getLanguageFromResurce(R.string.ok), getLanguageFromResurce(R.string.cancel));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lin_exit:
            case R.id.tv_exit:
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getScreenSize();
    }

    private void getScreenSize() {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int width = localDisplayMetrics.widthPixels;
        int height = localDisplayMetrics.heightPixels;
        MyLog.diff("start width=" + width + "  / height =" + height);
        SharedPerManager.setScreenWidth(width);
        SharedPerManager.setScreenHeight(height);
    }

}
