package com.etv.setting.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.etv.config.AppInfo;
import com.etv.entity.AppInfomation;
import com.etv.http.util.FileWriteRunnable;
import com.etv.listener.WriteSdListener;
import com.etv.service.EtvService;
import com.etv.setting.SettingBaseActivity;
import com.etv.util.APKUtil;
import com.etv.util.PackgeUtil;
import com.etv.util.SharedPerManager;
import com.ys.model.dialog.MyToastView;
import com.ys.model.dialog.OridinryDialog;
import com.ys.model.listener.OridinryDialogClick;

import com.ys.etv.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class AppManagerActivity extends SettingBaseActivity implements AppShowAdapter.AdapterAppManagerClickListener, View.OnClickListener {

    private BroadcastReceiver receiverPackage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e("action", "======APK监听===" + action);
            getAppListInfo();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        initView();
        initReplaceReceiver();
    }

    ListView lv_app_content;
    List<AppInfomation> lists = new ArrayList<AppInfomation>();
    AppShowAdapter adapter;
    ProgressBar pro_write;
    LinearLayout lin_exit;
    TextView tv_exit;

    private void initView() {
        AppInfo.startCheckTaskTag = false;
        lin_exit = (LinearLayout) findViewById(R.id.lin_exit);
        tv_exit = (TextView) findViewById(R.id.tv_exit);
        lin_exit.setOnClickListener(this);
        tv_exit.setOnClickListener(this);

        pro_write = (ProgressBar) findViewById(R.id.pro_write);
        lv_app_content = (ListView) findViewById(R.id.lv_app_content);
        adapter = new AppShowAdapter(AppManagerActivity.this, listsShow);
        lv_app_content.setAdapter(adapter);
        adapter.setAdapterClickLisenter(this);
    }

    @Override
    public void clickSure(Object object, View view) {
        final AppInfomation appInfomation = (AppInfomation) object;
        if (appInfomation == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.btn_open_apk:
                startApp(appInfomation);
                break;
            case R.id.btn_uninstall_apk:
                unInstallApkInfo(appInfomation);
                break;
            case R.id.btn_back_apk:
                OridinryDialog oridinryDialog = new OridinryDialog(AppManagerActivity.this);
                oridinryDialog.setOnDialogClickListener(new OridinryDialogClick() {
                    @Override
                    public void sure() {
                        backApkToSd(appInfomation);
                    }

                    @Override
                    public void noSure() {

                    }
                });
                oridinryDialog.show("是否备份 < " + appInfomation.getName() + " >软件? ", "确定", "取消");

                break;
        }
    }

    /**
     * 备份APK到本地
     *
     * @param appInfomation
     */
    private void backApkToSd(AppInfomation appInfomation) {
        try {
            String apkPath = appInfomation.getInstallPath();
            String name = appInfomation.getName() + ".apk";
            String saveFilePath = "/sdcard/" + name;
            File file = new File(saveFilePath);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            writeFileToSd(apkPath, saveFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeFileToSd(String apkPath, String saveFilePath) {
        try {
            FileWriteRunnable runnable = new FileWriteRunnable(AppManagerActivity.this, apkPath, saveFilePath, new WriteSdListener() {

                @Override
                public void writeProgress(int progress) {
                    pro_write.setProgress(progress);

                    Log.e("write", "==========文件拷贝进度===" + progress);
                }

                @Override
                public void writeSuccess(String savePath) {
                    pro_write.setProgress(0);
                    showToastView("备份完成");
                    Log.e("write", "==========writeSuccess===" + savePath);
                }

                @Override
                public void writrFailed(String errorrDesc) {
                    pro_write.setProgress(0);
                    showToastView("备份失败: " + errorrDesc);
                    Log.e("write", "==========writrFailed===" + errorrDesc);
                }
            });
            EtvService.getInstance().executor(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unInstallApkInfo(final AppInfomation appInfomation) {
        String packageName = appInfomation.getPackageName();
        if (packageName.contains(SharedPerManager.getPackageNameBySp())) { //不允许卸载掉自己
            MyToastView.getInstance().Toast(AppManagerActivity.this, "不允许卸载信发程序！");
            return;
        }
        OridinryDialog oridinryDialog = new OridinryDialog(AppManagerActivity.this);
        oridinryDialog.setOnDialogClickListener(new OridinryDialogClick() {
            @Override
            public void sure() {
                try {
                    String packageName = appInfomation.getPackageName();
                    Intent intent = new Intent(Intent.ACTION_DELETE);
                    intent.setData(Uri.parse("package:" + packageName));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void noSure() {

            }
        });
        oridinryDialog.show("是否卸载 < " + appInfomation.getName() + " >软件? ", "确定", "取消");
    }

    private void startApp(AppInfomation appInfomation) {
        if (appInfomation == null) {
            showToastView("数据==null");
            return;
        }
        String packageName = appInfomation.getPackageName();
        APKUtil.startApp(AppManagerActivity.this, packageName);
    }


    List<AppInfomation> listsShow = new ArrayList<AppInfomation>();

    public void updateView(int tag) {
        if (lists == null || lists.size() < 1) {
            return;
        }
        listsShow.clear();
        for (int i = 0; i < lists.size(); i++) {
            AppInfomation appInfomation = lists.get(i);
            int appTag = appInfomation.getAppTag();
            if (appTag == tag) {
                listsShow.add(appInfomation);
            } else if (tag == AppInfomation.APP_TAG_ALL) {
                listsShow.add(appInfomation);
            }
        }
        adapter.setList(listsShow);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAppListInfo();
    }

    private void getAppListInfo() {
        PackgeUtil.getPackage(AppManagerActivity.this, new PackgeUtil.PackageListener() {
            @Override
            public void getSuccess(ArrayList<AppInfomation> appList) {
                lists = appList;
                updateView(AppInfomation.APP_TAG_INSTALL);
            }

            @Override
            public void getFail(String error) {

            }
        });
    }

    private void initReplaceReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);  //替换APK
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);  //安装
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);  //卸载
        filter.addDataScheme("package");
        registerReceiver(receiverPackage, filter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lin_exit:
            case R.id.tv_exit:
                finish();
                break;
        }
    }
}
