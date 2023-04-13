package com.etv.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.etv.config.AppConfig;
import com.etv.config.AppInfo;
import com.etv.service.EtvService;
import com.etv.service.TaskWorkService;
import com.etv.service.TcpService;
import com.etv.service.TcpSocketService;
import com.etv.util.MyLog;
import com.etv.util.SharedPerManager;
import com.etv.util.SharedPerUtil;
import com.etv.util.guardian.GuardianUtil;
import com.etv.util.system.CpuModel;
import com.etv.util.system.SystemManagerUtil;
import com.etv.view.dialog.SettingMenuDialog;
import com.ys.etv.R;
import com.ys.model.dialog.EditTextDialog;
import com.ys.model.dialog.ErrorToastView;
import com.ys.model.dialog.MyToastView;
import com.ys.model.listener.EditTextDialogListener;
import com.ys.model.util.ActivityCollector;


public class BaseActivity extends AppCompatActivity {

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        init();
    }

    //进入播放界面，取消显示悬浮窗
    public void dismissPopWindow() {
        sendBroadcast(new Intent(TaskWorkService.DISSMISS_DOWN_POOP_WINDOW));
    }

    public String getLanguageFromResurce(int resourceId) {
        String desc = getResources().getString(resourceId);
        return desc;
    }

    public String getLanguageFromResurceWithPosition(int resourceId, String desc) {
        String stringStart = getResources().getString(resourceId);
        String startResult = String.format(stringStart, desc);
        return startResult;
    }

    private void init() {
        SystemManagerUtil.openCloseChuangpinLeaderBar(BaseActivity.this, false);
        ActivityCollector.addActivity(this);
        GuardianUtil.sendBroadToGuardianConpany(BaseActivity.this);
    }

    protected void onResume() {
        super.onResume();
        startAppService();
    }

    /***
     * start App Service
     */
    private void startAppService() {
        //  EtvService 不管什么状况，都要起来
        startService(new Intent(BaseActivity.this, EtvService.class));
        if (SharedPerUtil.SOCKEY_TYPE() == AppConfig.SOCKEY_TYPE_WEBSOCKET) {
            startService(new Intent(BaseActivity.this, TcpService.class));
        } else {
            startService(new Intent(BaseActivity.this, TcpSocketService.class));
        }
        startService(new Intent(BaseActivity.this, TaskWorkService.class));
    }

    public void showToastView(String desc) {
        MyToastView.getInstance().Toast(BaseActivity.this, desc);
    }

//    public void showToastCenterView(String desc) {
//        Toast toast = Toast.makeText(getApplication(), desc, Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        toast.show();
//    }


    SettingMenuDialog settingMenuDialog;

    public void showBaseSettingDialogNew() {
        boolean mainForst = MainActivity.isMainForst;
        if (settingMenuDialog == null) {
            settingMenuDialog = new SettingMenuDialog(BaseActivity.this);
        }
        settingMenuDialog.setOnDialogClickListener(new SettingMenuDialog.SettingMenuClickListener() {
            @Override
            public void clickWorkModel() {      //显示工作模式的弹窗
                startActivity(new Intent(BaseActivity.this, SettingSysActivity.class));
                if (!mainForst) { //不在前台，直接退出
                    finish();
                }
            }

            @Override
            public void exitApp() {
                if (mainForst) { //主界面才需要密码，播放界面不需要
                    showExitBaseDialog();
                } else {
                    exitAppInfo();
                }
            }
        });
        if (mainForst) {
            settingMenuDialog.show(getString(R.string.exit_app));
        } else {
            settingMenuDialog.show(getString(R.string.exit_play));
        }
    }

    public void showExitBaseDialog() {
        String exitCode = SharedPerManager.getExitpassword();
        if (exitCode == null || exitCode.length() < 1) {
            exitAppInfo();
            return;
        }
        //有退出密码
        EditTextDialog editTextDialog = new EditTextDialog(BaseActivity.this);
        editTextDialog.setOnDialogClickListener(new EditTextDialogListener() {
            @Override
            public void commit(String content) {
                String exitCodeDialog = SharedPerManager.getExitpassword();
                if (content.trim().contains(exitCodeDialog) || content.trim().contains("000")) {
                    exitAppInfo();
                } else {
                    MyToastView.getInstance().Toast(BaseActivity.this, getString(R.string.password_error));
                }
            }

            @Override
            public void clickHiddleView() {

            }
        });
        editTextDialog.show(getString(R.string.exit_password), "", getString(R.string.submit));
    }

    public void exitAppInfo() {
        //干掉PopWindow
        sendBroadcast(new Intent(TaskWorkService.DESTORY_DOWN_POOP_WINDOW));
        //设置异常退出得问题
        SharedPerManager.setExitDefault(true);
        try {
            //没有退出密码
            boolean mainForst = ActivityCollector.isForeground(BaseActivity.this, MainActivity.class.getName());
            if (mainForst) {
                //在前台，直接退出
                killAppMySelf();
            } else {
                //不再前台。需要返回到前台
                Intent intentMain = new Intent(BaseActivity.this, MainActivity.class);
                startActivity(intentMain);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void killAppMySelf() {
        AppInfo.startCheckTaskTag = false;  //退出程序，设置为false
        //开启底部导航栏
        SystemManagerUtil.openCloseChuangpinLeaderBar(BaseActivity.this, true);
        //发广播给守护进程，待会要起来
        Intent intent = new Intent(AppInfo.START_PROJECTOR_GUARDIAN_TIMER);
        sendBroadcast(intent);
        Log.e("projector", "发广播给守护进程，定时起来");
        //停止所有得程序服务
        if (SharedPerUtil.SOCKEY_TYPE() == AppConfig.SOCKEY_TYPE_WEBSOCKET) {
            stopService(new Intent(BaseActivity.this, TcpService.class));
        } else {
            stopService(new Intent(BaseActivity.this, TcpSocketService.class));
        }
        stopService(new Intent(BaseActivity.this, EtvService.class));
        stopService(new Intent(BaseActivity.this, TaskWorkService.class));
        // moveTaskToBack(true);
        MyLog.cdl("主界面退出,发广播给守护进程", true);
        //关闭所有的程序界面
        ActivityCollector.finishAll();
        //杀死当前进程
        android.os.Process.killProcess(android.os.Process.myPid());
        //结束掉自己
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (settingMenuDialog != null) {
            settingMenuDialog.dissmiss();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    public void sendMyBroadcastWithLongExtra(String action, String key, long value) {
        try {
            Intent intent = new Intent();
            intent.setAction(action);
            intent.putExtra(key, value);
            sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
