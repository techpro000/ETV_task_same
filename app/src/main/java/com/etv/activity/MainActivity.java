package com.etv.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.etv.activity.pansener.MainParsener;
import com.etv.activity.view.MainView;
import com.etv.config.AppConfig;
import com.etv.config.AppInfo;
import com.etv.db.DbBggImageUtil;
import com.etv.service.TaskWorkService;
import com.etv.service.TcpService;
import com.etv.service.TcpSocketService;
import com.etv.task.activity.PlayerTaskActivity;
import com.etv.task.activity.TaskActivity;
import com.etv.task.entity.TaskWorkEntity;
import com.etv.task.model.TaskGetDbListener;
import com.etv.task.model.TaskModelUtil;
import com.etv.task.model.TaskModelmpl;
import com.etv.util.Biantai;
import com.etv.util.MyLog;
import com.etv.util.NetWorkUtils;
import com.etv.util.SharedPerManager;
import com.etv.util.SharedPerUtil;
import com.etv.util.ViewSizeChange;
import com.etv.util.guardian.GuardianUtil;
import com.etv.util.image.glide.GlideImageUtil;
import com.etv.util.rxjava.AppStatuesListener;
import com.etv.view.dialog.SettingMenuDialog;
import com.ys.etv.R;

import java.io.File;
import java.util.List;

/***
 * 程序主界面，
 * 这一行是为了测试代码
 * 功能在parsener里面
 * GuardInterface.aidl是用来监听守护进程的功能。
 * 守护进程默认3分钟吊起本程序，时间修改可以在终端设置里面，点击标题进入修改弹窗，单位秒
 * log标签 ：cdl
 */
public class MainActivity extends TaskActivity implements
    View.OnClickListener, MainView {

    MainParsener mainParsener;
    public static boolean IS_ORDER_REQUEST_TASK = false; //根据这个参数去请求任务
    public static boolean isMainForst = false;

    private BroadcastReceiver receiverMain = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            MyLog.e("main", "====main界面广播====" + action);
            if (action.equals(Intent.ACTION_TIME_TICK)) {
                //时间网络变化广播
                if (mainParsener == null) {
                    return;
                }
                mainParsener.updateTimeNetLineView();
                mainParsener.startTimerToPlay("时间变化,去检测一次");       //5秒后自动播放
            } else if (action.equals(AppInfo.SOCKET_LINE_STATUS_CHANGE)) {
                if (mainParsener == null) {
                    return;
                }
                mainParsener.updateTimeNetLineView();
                mainParsener.startTimerToPlay("服务器连接成功了，去检查一次");       //5秒后自动播放
            }
        }
    };

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_main_new);
        AppInfo.isAppRun = true;
        initView();
        initListener();
        initReceiver();
    }


    //:/ #
//
//        :/ # setprop media.omx.display_mode 0
//             setprop media.omx.display_mode 0
//            :/ # setprop sys.meida.omx.vr 1
//                 setprop sys.meida.omx.vr 1
//            :/ # getprop media.omx.display_mode
//                 getprop media.omx.display_mode  0
//        :/ # getprop sys.meida.omx.vr
//             getprop sys.meida.omx.vr  1
//        :/ #


    @Override
    public void showDownStatuesView(boolean isShow, String desc) {
        rela_down_tag.setVisibility(isShow ? View.VISIBLE : View.GONE);
        tv_down_desc.setText(desc);
    }

    TextView tv_time;
    ImageView iv_net_state, iv_line_state, iv_work_model;
    View view_cleck;
    ImageView iv_main_bgg;
    LinearLayout lin_qf_logo;
    TextView tv_mac_main;
    ImageView iv_logo_show;  //主界面logo显示

    RelativeLayout rela_down_tag;
    TextView tv_down_desc;

    private void initView() {
        tv_down_desc = (TextView) findViewById(R.id.tv_down_desc);
        rela_down_tag = (RelativeLayout) findViewById(R.id.rela_down_tag);
        AppInfo.startCheckTaskTag = true;
        tv_mac_main = (TextView) findViewById(R.id.tv_mac_main);
        lin_qf_logo = (LinearLayout) findViewById(R.id.lin_qf_logo);
        iv_logo_show = (ImageView) findViewById(R.id.iv_logo_show);
        ViewSizeChange.setLogoPosition(iv_logo_show);
        iv_main_bgg = (ImageView) findViewById(R.id.iv_main_bgg);
        iv_main_bgg.setOnClickListener(this);
        view_cleck = (View) findViewById(R.id.view_cleck);
        view_cleck.setOnClickListener(this);
        iv_net_state = (ImageView) findViewById(R.id.iv_net_state);
        iv_line_state = (ImageView) findViewById(R.id.iv_line_state);
        iv_work_model = (ImageView) findViewById(R.id.iv_work_model);
        tv_time = (TextView) findViewById(R.id.tv_time);

        mainParsener = new MainParsener(MainActivity.this, this);
        view_cleck.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showSettingDialog();
                return true;
            }
        });
        updateBggImageView("程序启动，加载一次");
        initEventBus();
    }


    private void initListener() {
        //网络状态监听
        AppStatuesListener.getInstance().NetChange.observe(MainActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                MyLog.message("=======onChanged==NetChange======" + s);
                if (mainParsener != null) {
                    mainParsener.updateTimeNetLineView();
                }
            }
        });
    }


    /**
     * 更新背景图
     * 以及Logo
     */
    @Override
    public void updateBggImageView(String tag) {
        MyLog.cdl("=========updateBggImageView======" + tag);
        String imagePath = DbBggImageUtil.getBggImagePath(MainActivity.this);
        int defaultImage = DbBggImageUtil.getDefaultBggImage();
        lin_qf_logo.setVisibility(View.GONE);
        MyLog.cdl("===================加载背景图==========" + imagePath);
        GlideImageUtil.loadImageDefaultId(MainActivity.this, imagePath, iv_main_bgg, defaultImage);
    }

    private void loadQfLogoView(String imagePath) {
        if (imagePath == null || imagePath.length() < 5) { //文件不存在
            lin_qf_logo.setVisibility(View.VISIBLE);
            ViewSizeChange.setMainLogoPosition(lin_qf_logo);
            return;
        }
        File fileQf = new File(imagePath);
        if (fileQf.exists()) {
            lin_qf_logo.setVisibility(View.GONE);
        } else {
            lin_qf_logo.setVisibility(View.VISIBLE);
            ViewSizeChange.setMainLogoPosition(lin_qf_logo);
        }
    }

    private void initReceiver() {
        IntentFilter fileter = new IntentFilter();
        fileter.addAction(Intent.ACTION_TIME_TICK);
        fileter.addAction(AppInfo.SOCKET_LINE_STATUS_CHANGE);
        registerReceiver(receiverMain, fileter);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //这里点击只是为了测试，正式版本需要注释掉
            case R.id.view_cleck:
                break;
            case R.id.iv_main_bgg:
                if (Biantai.isTwoClick()) {
                    return;
                }
                startToCheckTaskToActivity("点击背景图");
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //一键报警功能需要回归守护时间
        if (SharedPerManager.getGpioAction()) {
            GuardianUtil.setGuardianProjectTime(MainActivity.this, "120");
        }
        isMainForst = true;
        if (Biantai.isMainOnResume()) {
            return;
        }
        AppInfo.startCheckTaskTag = true;
        try {
            if (mainParsener != null) {
                mainParsener.startTimerToPlay("onResume");       //5秒后自动播放
                mainParsener.updateDevInfoToWeb();     //更新设备状态给服务器
                mainParsener.updateTimeNetLineView();   //刷新界面时间网络信息
            }
            if (!IS_ORDER_REQUEST_TASK) {
                return;
            }
            requestTaskFromWeb("OnResume");
            IS_ORDER_REQUEST_TASK = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public ImageView getIvlineState() {
        return iv_line_state;
    }

    @Override
    public ImageView getIvWorkModel() {
        return iv_work_model;
    }

    @Override
    public TextView getTimeView() {
        return tv_time;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (receiverMain != null) {
                unregisterReceiver(receiverMain);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("etv", "=========keyCode======" + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showSettingDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    SettingMenuDialog settingMenuDialog;

    private void showSettingDialog() {
        settingMenuDialog = new SettingMenuDialog(MainActivity.this);
        settingMenuDialog.setOnDialogClickListener(new SettingMenuDialog.SettingMenuClickListener() {
            @Override
            public void clickWorkModel() {      //显示工作模式的弹窗
                startActivity(new Intent(MainActivity.this, SettingSysActivity.class));
            }

            @Override
            public void exitApp() {
                showExitBaseDialog();
            }
        });
        settingMenuDialog.show(getLanguageFromResurce(R.string.exit_app));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (settingMenuDialog != null) {
            settingMenuDialog.dissmiss();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        isMainForst = false;
        if (mainParsener != null) {
            mainParsener.onStopParsener();
        }
        if (handler != null) {
            handler.removeMessages(MESSAGE_UPDATE_TIME);
        }
    }

    //====================================================================================================

    /**
     * 这里是入口
     */
    @Override
    public void startToCheckTaskToActivity(String printTag) {
        requestTaskFromWeb(printTag);
    }

    public void requestTaskFromWeb(String printTag) {
        if (!NetWorkUtils.isNetworkConnected(MainActivity.this)) {
            TaskWorkService.setCurrentTaskType(TaskWorkService.TASK_TYPE_DEFAULT, "主界面调用该方法");
            startToPlayActivity();
            return;
        }
        MyLog.task("========requestTaskFromWeb======" + printTag);
        Intent intentTask = new Intent();
        intentTask.setAction(TaskWorkService.GET_TASK_FROM_WEB_TAG);
        intentTask.putExtra(TaskWorkService.GET_TASK_FROM_WEB_TAG, printTag);
        sendBroadcast(intentTask);
        IS_ORDER_REQUEST_TASK = false;
    }

    @Override
    public ImageView getIvWifiState() {
        return iv_net_state;
    }

    @Override
    public void checkSdStateFinish() {
        cacelTaskRequest("checkSdStateFinish");
    }

    @Override
    public void stopOrderToPlay() {
        cacelTaskRequest("stopOrderToPlay");
    }

    /**
     * 获取的任务==null
     */
    @Override
    public void getTaskInfoNull() {
        MyLog.cdl("=======获取的节目==null====MainActivity");
        cacelTaskRequest("getTaskInfoNull");
    }

    public void cacelTaskRequest(String tag) {
        MyLog.task("=======主界面获取的任务==null======" + tag);
        onResume();
    }

    TaskModelmpl taskModelmpl;

    /**
     * 监测网络下发模式
     * 为什么要监测一次任务呢，
     * 测试说，这里会一闪一闪得，这里就加了一个判断
     */
    private void startToPlayActivity() {
        if (taskModelmpl == null) {
            taskModelmpl = new TaskModelmpl();
        }
        taskModelmpl.getPlayTaskListFormDb(new TaskGetDbListener() {
            @Override
            public void getTaskFromDb(List<TaskWorkEntity> list) {
                if (list == null || list.size() < 1) {
                    MyLog.playTask("======没有获取到需要播放的任务，中断检查");
                    mainParsener.startTimerToPlay("网络导入模式,定时去检查");
                    return;
                }
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, PlayerTaskActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void getTaskTigerFromDb(TaskWorkEntity taskWorkEntity) {

            }
        }, "====主界面主动检查任务====", TaskModelUtil.DEL_LASTDATE_AND_AFTER_NOW);
    }

    //=========稳定的代码==========================================================
    private void initEventBus() {
        AppStatuesListener.getInstance().timeChangeEvent.observe(MainActivity.this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                handler.sendEmptyMessageDelayed(MESSAGE_UPDATE_TIME, 1500);
                MyLog.cdl("==========解析设备=====界面====通知系统成功===", true);
                AppInfo.updateLocalTime = true;
            }
        });

        AppStatuesListener.getInstance().UpdateMainBggEvent.observe(MainActivity.this, new Observer<String>() {
            @Override
            public void onChanged(String desc) {
                updateBggImageView(desc);
            }
        });
    }

    private static final int MESSAGE_UPDATE_TIME = 456;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handler.removeMessages(msg.what);
            switch (msg.what) {
                case MESSAGE_UPDATE_TIME:
                    if (mainParsener != null) {
                        mainParsener.updateTimeNetLineView();
                    }
                    break;
            }
        }
    };

}
