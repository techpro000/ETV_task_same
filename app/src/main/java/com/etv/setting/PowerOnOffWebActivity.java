package com.etv.setting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.etv.activity.BaseActivity;
import com.etv.config.AppInfo;
import com.etv.setting.adapter.PowerOnOffAdapter;
import com.etv.task.db.DbTimerUtil;
import com.etv.task.entity.TimerDbEntity;
import com.etv.util.MyLog;
import com.etv.util.SharedPerManager;
import com.etv.util.rxjava.AppStatuesListener;
import com.etv.util.rxjava.RxBus;
import com.etv.util.rxjava.event.RxEvent;
import com.etv.util.system.PowerOnOffUtil;
import com.ys.model.dialog.MyToastView;
import com.ys.model.dialog.WaitDialogUtil;
import com.ys.etv.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.functions.Consumer;

/***
 * 网络版本的定时开关机
 */
public class PowerOnOffWebActivity extends SettingBaseActivity implements View.OnClickListener {

    ListView lv_power;
    TextView tv_show_time;
    PowerOnOffAdapter adapter;
    LinearLayout iv_no_data;
    List<TimerDbEntity> list = new ArrayList<TimerDbEntity>();
    private Handler handler = new Handler();
    PowerOnOffUtil powerOnOffUtil;

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.fragment_poweronoff);
        initView();
        initRxBus();
    }

    private void initRxBus() {
        AppStatuesListener.getInstance().PowerOnOffEvent.observe(PowerOnOffWebActivity.this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                MyLog.powerOnOff("=========界面刷新定时开关机======");
                getWebDbData();
            }
        });
    }

    TextView tv_mon, tv_tue, tv_wed, tv_thu, tv_fri, tv_sat, tv_sun;
    WaitDialogUtil waitDialogUtil;
    LinearLayout lin_exit;
    TextView tv_exit;
    private Button btn_power_log;

    private void initView() {
        AppInfo.startCheckTaskTag = false;
        waitDialogUtil = new WaitDialogUtil(PowerOnOffWebActivity.this);
        powerOnOffUtil = new PowerOnOffUtil(PowerOnOffWebActivity.this);
        tv_mon = (TextView) findViewById(R.id.tv_mon);
        tv_tue = (TextView) findViewById(R.id.tv_tue);
        tv_wed = (TextView) findViewById(R.id.tv_wed);
        tv_thu = (TextView) findViewById(R.id.tv_thu);
        tv_fri = (TextView) findViewById(R.id.tv_fri);
        tv_sat = (TextView) findViewById(R.id.tv_sat);
        tv_sun = (TextView) findViewById(R.id.tv_sun);

        btn_power_log = (Button) findViewById(R.id.btn_power_log);
        btn_power_log.setOnClickListener(this);
        lin_exit = (LinearLayout) findViewById(R.id.lin_exit);
        tv_exit = (TextView) findViewById(R.id.tv_exit);
        lin_exit.setOnClickListener(this);
        tv_exit.setOnClickListener(this);


        tv_show_time = (TextView) findViewById(R.id.tv_show_time);
        iv_no_data = (LinearLayout) findViewById(R.id.iv_no_data);
        lv_power = (ListView) findViewById(R.id.lv_power);
        adapter = new PowerOnOffAdapter(PowerOnOffWebActivity.this, list);
        lv_power.setAdapter(adapter);
    }

    /***
     * 获取数据库保存的定时开关时间
     */
    public void getWebDbData() {
        list.clear();
        adapter.setList(list);

        waitDialogUtil.show(getLanguageFromResurce(R.string.refreshing));
        list = DbTimerUtil.queryTimerList();
        if (list == null || list.size() < 1) {
            waitDialogUtil.dismiss();
            iv_no_data.setVisibility(View.VISIBLE);
            powerOnOffUtil.clearPowerOnOffTime();
            getCurrentDate();
            MyToastView.getInstance().Toast(PowerOnOffWebActivity.this, getLanguageFromResurce(R.string.no_poweronoff));
            return;
        }
        iv_no_data.setVisibility(View.GONE);
        adapter.setList(list);
        getCurrentDate();
        powerOnOffUtil.changePowerOnOffByWorkModel("网络定时开关机设置");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                waitDialogUtil.dismiss();
                getCurrentDate();
            }
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWebDbData();
    }

    private void getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int currentDayWeek = calendar.get(Calendar.DAY_OF_WEEK);  //今天的工作星期
        switch (currentDayWeek) {
            case 1: //7
                tv_sun.setTextColor(getResources().getColor(R.color.white));
                tv_sun.setBackgroundResource(R.drawable.terminal_setting);
                break;
            case 2: //1
                tv_mon.setBackgroundResource(R.drawable.terminal_setting);
                tv_mon.setTextColor(getResources().getColor(R.color.white));
                break;
            case 3: //2
                tv_tue.setBackgroundResource(R.drawable.terminal_setting);
                tv_tue.setTextColor(getResources().getColor(R.color.white));
                break;
            case 4: //3
                tv_wed.setBackgroundResource(R.drawable.terminal_setting);
                tv_wed.setTextColor(getResources().getColor(R.color.white));
                break;
            case 5: //4
                tv_thu.setBackgroundResource(R.drawable.terminal_setting);
                tv_thu.setTextColor(getResources().getColor(R.color.white));
                break;
            case 6: //5
                tv_fri.setBackgroundResource(R.drawable.terminal_setting);
                tv_fri.setTextColor(getResources().getColor(R.color.white));
                break;
            case 7: //6
                tv_sat.setBackgroundResource(R.drawable.terminal_setting);
                tv_sat.setTextColor(getResources().getColor(R.color.white));
                break;
        }
        MyLog.e("cdl", "========currentDayWeek=====" + currentDayWeek);
        String onTime = PowerOnOffUtil.getPowerOnTime(PowerOnOffWebActivity.this);
        String offTime = PowerOnOffUtil.getPowerOffTime(PowerOnOffWebActivity.this);
        if (onTime.length() < 2 || offTime.length() < 2) {
            String openDesc = getLanguageFromResurce(R.string.open_time);
            String closeDesc = getLanguageFromResurce(R.string.close_time);
            tv_show_time.setText(openDesc + ": " + offTime + "\n" + closeDesc + ": " + onTime);
        } else {
            String year = onTime.substring(0, 4);
            String month = onTime.substring(4, 6);
            String day = onTime.substring(6, 8);
            String hour = onTime.substring(8, 10);
            String min = onTime.substring(10, 12);

            String offyear = offTime.substring(0, 4);
            String offmonth = offTime.substring(4, 6);
            String offday = offTime.substring(6, 8);
            String offhour = offTime.substring(8, 10);
            String offmin = offTime.substring(10, 12);
            String onTimeShow = year + "/" + month + "/" + day + "  " + hour + ":" + min + ":00";
            String offTimeShow = offyear + "/" + offmonth + "/" + offday + "  " + offhour + ":" + offmin + ":00";
            tv_show_time.setText(getLanguageFromResurce(R.string.close_time) + ": " + offTimeShow + "\n"
                    + getLanguageFromResurce(R.string.open_time) + ": " + onTimeShow);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_power_log:
                startActivity(new Intent(PowerOnOffWebActivity.this, PowerInOffLogActivity.class));
                break;
            case R.id.lin_exit:
            case R.id.tv_exit:
                finish();
                break;
        }
    }
}
