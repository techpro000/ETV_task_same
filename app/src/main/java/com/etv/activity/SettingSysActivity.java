package com.etv.activity;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.etv.config.AppConfig;
import com.etv.config.AppInfo;
import com.etv.setting.SettingBaseActivity;
import com.etv.setting.framenew.AppSettingFragment;
import com.etv.setting.framenew.NetDownWorkFragment;
import com.etv.setting.framenew.NetWorkFragment;
import com.etv.setting.framenew.ServerConnectFragment;
import com.etv.setting.framenew.SingleWorkFragment;
import com.etv.util.MyLog;
import com.etv.util.NetWorkUtils;
import com.etv.util.SharedPerManager;
import com.etv.util.rxjava.AppStatuesListener;
import com.ys.etv.R;
import com.ys.model.dialog.MyToastView;

/**
 * 系统设置界面
 */
public class SettingSysActivity extends SettingBaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private BroadcastReceiver receiverSetting = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            MyLog.cdl("=========fragment==接受倒广播==" + action);
            if (action.equals(AppInfo.SOCKET_LINE_STATUS_CHANGE)) {
                updateTopTitleStatues();
                String errorDesc = intent.getStringExtra(AppInfo.SOCKET_LINE_STATUS_CHANGE);
                if (!errorDesc.equals(AppInfo.DEV_ONLINE_NOT_LOGIN)) {
                    showSettingToast(errorDesc);
                }
                if (CURRENT_POSITION != FRAGMENT_SERVER_CONNECT) {
                    return;
                }
                if (AppConfig.isOnline) {
                    showSettingToast(getLanguageFromResurce(R.string.server_conn_success));
                    if (isServerConClick) { //只有点击的才返回
                        startActivityMain();
                    }
                }
            }
        }
    };


    //用来判断是否是点击的。只有点击的才自动返回
    public static boolean isServerConClick = false;

    /**
     * 链接服务器成功
     */
    private void startActivityMain() {
        Intent intent = new Intent();
        intent.setClass(SettingSysActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showSettingToast(String desc) {
        MyToastView.getInstance().Toast(SettingSysActivity.this, desc);
    }

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_setting_system);
        isServerConClick = false;
        initView();
        initListener();
        initReceiver();
    }

    private void initListener() {
        AppStatuesListener.getInstance().NetChange.observe(SettingSysActivity.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                MyLog.message("=======onChanged==NetChange==系统设置系欸按====" + s);
                updateTopTitleStatues();
                if (CURRENT_POSITION == FRAGMENT_NET_WORK) {
                    MyLog.message("=======onChanged==刷新fragment界面状态====" + s);
                    netWorkFragment.updateNetView();
                }
            }
        });
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppInfo.SOCKET_LINE_STATUS_CHANGE);
        registerReceiver(receiverSetting, filter);
    }

    LinearLayout lin_server_conn, lin_net_work, lin_net_down, lin_single, lin_setting;
    Button tv_server_conn, tv_net_work, tv_net_down, tv_single, tv_setting;
    private FragmentManager fragmentManager;
    private TextView tv_net_status, tv_web_statues, tv_username;
    LinearLayout lin_exit;
    TextView tv_exit;

    private void initView() {
        AppInfo.startCheckTaskTag = false;  //停止任务自动检查
        fragmentManager = getFragmentManager();
        tv_net_status = (TextView) findViewById(R.id.tv_net_status);
        tv_web_statues = (TextView) findViewById(R.id.tv_web_statues);
        tv_username = (TextView) findViewById(R.id.tv_username);

        lin_server_conn = (LinearLayout) findViewById(R.id.lin_server_conn);
        lin_net_work = (LinearLayout) findViewById(R.id.lin_net_work);
        lin_net_down = (LinearLayout) findViewById(R.id.lin_net_down);
        lin_single = (LinearLayout) findViewById(R.id.lin_single);
        lin_setting = (LinearLayout) findViewById(R.id.lin_setting);

        tv_server_conn = (Button) findViewById(R.id.tv_server_conn);
        tv_net_work = (Button) findViewById(R.id.tv_net_work);
        tv_net_down = (Button) findViewById(R.id.tv_net_down);
        tv_single = (Button) findViewById(R.id.tv_single);
        tv_setting = (Button) findViewById(R.id.tv_setting);

        tv_server_conn.setOnClickListener(this);
        tv_net_work.setOnClickListener(this);
        tv_net_down.setOnClickListener(this);
        tv_single.setOnClickListener(this);
        tv_setting.setOnClickListener(this);

        tv_server_conn.setOnFocusChangeListener(this);
        tv_net_work.setOnFocusChangeListener(this);
        tv_net_down.setOnFocusChangeListener(this);
        tv_single.setOnFocusChangeListener(this);
        tv_setting.setOnFocusChangeListener(this);

        lin_server_conn.setVisibility(View.VISIBLE);
        lin_net_work.setVisibility(View.VISIBLE);
        lin_setting.setVisibility(View.VISIBLE);
        tv_exit = (TextView) findViewById(R.id.tv_exit);
        lin_exit = (LinearLayout) findViewById(R.id.lin_exit);
        tv_exit.setOnClickListener(this);
        lin_exit.setOnClickListener(this);
        setTabSelection(FRAGMENT_SERVER_CONNECT);
//        if (workModel == AppInfo.WORK_MODEL_NET) {  //网络模式设置成功
//            setTabSelection(FRAGMENT_SERVER_CONNECT);
////            setTabSelection(FRAGMENT_NET_WORK);
//        } else if (workModel == AppInfo.WORK_MODEL_NET_DOWN) { //网络导入设置成功
//            setTabSelection(FRAGMENT_NET_DOWN_WORK);
//        } else if (workModel == AppInfo.WORK_MODEL_SINGLE) {  //单机模式设置成功
//            setTabSelection(FRAGMENT_SINGLE_WORK);
//        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        TextView textView = (TextView) view;
        if (b) {
            lin_server_conn.setBackgroundColor(colorWhite);
            lin_setting.setBackgroundColor(colorWhite);
            lin_net_work.setBackgroundColor(colorWhite);
            lin_net_down.setBackgroundColor(colorWhite);
            lin_single.setBackgroundColor(colorWhite);
        } else {
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_server_conn:
                setTabSelection(FRAGMENT_SERVER_CONNECT);
                break;
            case R.id.tv_net_work:
                setTabSelection(FRAGMENT_NET_WORK);
                break;
            case R.id.tv_net_down:
                setTabSelection(FRAGMENT_NET_DOWN_WORK);
                break;
            case R.id.tv_single:
                setTabSelection(FRAGMENT_SINGLE_WORK);
                break;
            case R.id.tv_setting:
                setTabSelection(FRAGMENT_APP_SETTING);
                break;
            case R.id.lin_exit:
            case R.id.tv_exit:
                startActivityMain();
                break;
        }
    }

    public static final int FRAGMENT_SERVER_CONNECT = 0;
    public static final int FRAGMENT_NET_WORK = 1;
    public static final int FRAGMENT_NET_DOWN_WORK = 2;
    public static final int FRAGMENT_SINGLE_WORK = 3;
    public static final int FRAGMENT_APP_SETTING = 4;

    ServerConnectFragment serverConnectFragment;
    NetWorkFragment netWorkFragment;
    NetDownWorkFragment netDownWorkFragment;
    SingleWorkFragment singleWorkFragment;
    AppSettingFragment appSettingFragment;
    int CURRENT_POSITION = 0;
    int colorChooice = 0xff00CCFF;   //选中蓝色
    int colorWhite = 0xffffffff;  //white

    @SuppressLint("ResourceAsColor")
    public void setTabSelection(int tabSelection) {
        lin_server_conn.setBackgroundColor(colorWhite);
        lin_setting.setBackgroundColor(colorWhite);
        lin_net_work.setBackgroundColor(colorWhite);
        lin_net_down.setBackgroundColor(colorWhite);
        lin_single.setBackgroundColor(colorWhite);

        CURRENT_POSITION = tabSelection;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (tabSelection) {
            case FRAGMENT_SERVER_CONNECT:
                lin_server_conn.setBackgroundColor(colorChooice);
                if (serverConnectFragment == null) {
                    serverConnectFragment = new ServerConnectFragment();
                }
                transaction.replace(R.id.content_layout, serverConnectFragment);
                break;
            case FRAGMENT_NET_WORK:
                lin_net_work.setBackgroundColor(colorChooice);
                if (netWorkFragment == null) {
                    netWorkFragment = new NetWorkFragment();
                }
                transaction.replace(R.id.content_layout, netWorkFragment);
                break;
            case FRAGMENT_NET_DOWN_WORK:
                lin_net_down.setBackgroundColor(colorChooice);
                if (netDownWorkFragment == null) {
                    netDownWorkFragment = new NetDownWorkFragment();
                }
                transaction.replace(R.id.content_layout, netDownWorkFragment);
                break;
            case FRAGMENT_SINGLE_WORK:
                lin_single.setBackgroundColor(colorChooice);
                if (singleWorkFragment == null) {
                    singleWorkFragment = new SingleWorkFragment();
                }
                transaction.replace(R.id.content_layout, singleWorkFragment);
                break;
            case FRAGMENT_APP_SETTING:
                lin_setting.setBackgroundColor(colorChooice);
                if (appSettingFragment == null) {
                    appSettingFragment = new AppSettingFragment();
                }
                transaction.replace(R.id.content_layout, appSettingFragment);
                break;
        }
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateFragmentView(CURRENT_POSITION);
        updateTopTitleStatues();
    }

    /**
     * 更新顶部菜单
     */
    private void updateTopTitleStatues() {
        boolean isNetConnect = NetWorkUtils.isNetworkConnected(SettingSysActivity.this);
        if (isNetConnect) {
            tv_net_status.setTextColor(getResources().getColor(R.color.grey));
        } else {
            tv_net_status.setTextColor(getResources().getColor(R.color.red));
        }
        tv_net_status.setText(isNetConnect ? getLanguageFromResurce(R.string.connect_ed) : getLanguageFromResurce(R.string.un_connect));
        boolean isLineWeb = AppConfig.isOnline;
        if (isLineWeb) {
            tv_web_statues.setTextColor(getResources().getColor(R.color.grey));
        } else {
            tv_web_statues.setTextColor(getResources().getColor(R.color.red));
        }
        tv_web_statues.setText(isLineWeb ? getLanguageFromResurce(R.string.connect_ed) : getLanguageFromResurce(R.string.un_connect));
        String username = SharedPerManager.getUserName();
        tv_username.setTextColor(getResources().getColor(R.color.grey));
        if (username == null || username.length() < 2) {
            username = "No User";
            tv_username.setTextColor(getResources().getColor(R.color.red));
        }
        tv_username.setText(username);
    }

    private void updateFragmentView(int tabSelection) {
        switch (tabSelection) {
            case FRAGMENT_NET_WORK:  //刷新界面
                netWorkFragment.onResumeView();
                break;
            case FRAGMENT_NET_DOWN_WORK:
                break;
            case FRAGMENT_SINGLE_WORK:
                singleWorkFragment.onResumeView();
                break;
            case FRAGMENT_APP_SETTING:

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiverSetting != null) {
            unregisterReceiver(receiverSetting);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivityMain();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
