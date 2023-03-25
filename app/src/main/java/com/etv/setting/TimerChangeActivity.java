package com.etv.setting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import com.etv.config.AppInfo;
import com.etv.setting.db.DbTimerLocalUtil;
import com.etv.setting.db.TimeLocalEntity;
import com.etv.util.MyLog;
import com.ys.model.dialog.MyToastView;
import com.ys.model.dialog.WaitDialogUtil;
import com.etv.view.dialog.TimerChooiceDialog;
import com.ys.etv.R;

import com.ys.model.dialog.OridinryDialog;
import com.ys.model.listener.OridinryDialogClick;

public class TimerChangeActivity extends SettingBaseActivity implements View.OnClickListener {
    LinearLayout lin_exit;
    public static final String ORDER_STRING = "ORDER_STRING";  //修改
    public static final int ORDER_ADD = 1;
    public static final int ORDER_MODIFY = 2;  //修改
    public static final String TIMER_ID = "TIMER_ID";  //ID
    private OridinryDialog oridinryDialog;

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_time_change);
        initView();
        getDate();
    }

    Button btn_time_off;
    Button btn_time_on;
    Button btn_del;
    Button btn_submit;
    Button btn_cacel;
    CheckBox ck_mon;
    CheckBox ck_tue;
    CheckBox ck_wed;
    CheckBox ck_thu;
    CheckBox ck_fri;
    CheckBox ck_sta;
    CheckBox ck_sun;
    CheckBox ck_all;
    TimerChooiceDialog timerChooiceDialog;
    WaitDialogUtil waitDialogUtil;

    private void initView() {
        AppInfo.startCheckTaskTag = false;
        waitDialogUtil = new WaitDialogUtil(TimerChangeActivity.this);
        timerChooiceDialog = new TimerChooiceDialog(TimerChangeActivity.this);
        lin_exit = (LinearLayout) findViewById(R.id.lin_exit);
        lin_exit.setOnClickListener(this);
        btn_del = (Button) findViewById(R.id.btn_del);
        btn_del.setOnClickListener(this);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_cacel = (Button) findViewById(R.id.btn_cacel);
        btn_submit.setOnClickListener(this);
        btn_cacel.setOnClickListener(this);
        btn_time_on = (Button) findViewById(R.id.btn_time_on);
        btn_time_off = (Button) findViewById(R.id.btn_time_off);
        btn_time_on.setOnClickListener(this);
        btn_time_off.setOnClickListener(this);
        ck_mon = (CheckBox) findViewById(R.id.ck_mon);
        ck_tue = (CheckBox) findViewById(R.id.ck_tue);
        ck_wed = (CheckBox) findViewById(R.id.ck_wed);
        ck_thu = (CheckBox) findViewById(R.id.ck_thu);
        ck_fri = (CheckBox) findViewById(R.id.ck_fri);
        ck_sta = (CheckBox) findViewById(R.id.ck_sta);
        ck_sun = (CheckBox) findViewById(R.id.ck_sun);
        ck_all = (CheckBox) findViewById(R.id.ck_all);
        ck_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ck_mon.setChecked(b);
                ck_tue.setChecked(b);
                ck_wed.setChecked(b);
                ck_thu.setChecked(b);
                ck_fri.setChecked(b);
                ck_sta.setChecked(b);
                ck_sun.setChecked(b);
            }
        });
    }

    long timeId = -1;
    int order = ORDER_ADD;
    int onHour = 6;
    int onMin = 30;
    int offHour = 18;
    int offMin = 30;

    private void getDate() {
        Intent intent = getIntent();
        order = intent.getIntExtra(ORDER_STRING, ORDER_ADD);
        if (order == ORDER_ADD) {  //增加
            //不做任何操作
            btn_del.setVisibility(View.GONE);
            btn_submit.setText(getLanguageFromResurce(R.string.add));
        } else if (order == ORDER_MODIFY) {  //修改
            btn_submit.setText(getLanguageFromResurce(R.string.modify));
            btn_del.setVisibility(View.VISIBLE);
            timeId = intent.getLongExtra(TIMER_ID, -1);
            if (timeId < 0) {
                showToastView(getLanguageFromResurce(R.string.get_power_error));
                return;
            }
            TimeLocalEntity entity = DbTimerLocalUtil.getTimeById(timeId);
            updateView(entity);
        }
    }

    private void updateView(TimeLocalEntity entity) {
        String onTime = entity.getTtOnTime();
        String offTime = entity.getTtOffTime();
        onHour = Integer.parseInt(onTime.substring(0, onTime.indexOf(":")));
        onMin = Integer.parseInt(onTime.substring(onTime.indexOf(":") + 1, onTime.length()));
        offHour = Integer.parseInt(offTime.substring(0, offTime.indexOf(":")));
        offMin = Integer.parseInt(offTime.substring(offTime.indexOf(":") + 1, offTime.length()));
        boolean ttMon = Boolean.parseBoolean(entity.getTtMon());
        boolean ttTue = Boolean.parseBoolean(entity.getTtTue());
        boolean ttWed = Boolean.parseBoolean(entity.getTtWed());
        boolean ttThu = Boolean.parseBoolean(entity.getTtThu());
        boolean ttFri = Boolean.parseBoolean(entity.getTtFri());
        boolean ttSat = Boolean.parseBoolean(entity.getTtSat());
        boolean ttSun = Boolean.parseBoolean(entity.getTtSun());
        ck_mon.setChecked(ttMon);
        ck_tue.setChecked(ttTue);
        ck_wed.setChecked(ttWed);
        ck_thu.setChecked(ttThu);
        ck_fri.setChecked(ttFri);
        ck_sta.setChecked(ttSat);
        ck_sun.setChecked(ttSun);
        btn_time_on.setText(onTime);
        btn_time_off.setText(offTime);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_del:
                showDelTimerDialog();
                break;
            case R.id.btn_time_off:
                showTimeOffDialog();
                break;
            case R.id.btn_time_on:
                MyLog.powerOnOff("================点击了开机时间图标");
                showTimeOnDialog();
                break;
            case R.id.btn_submit:
                if (isCheckNull()) {
                    showToastView("请勾选周期");
                    return;
                }
                if (order == ORDER_ADD) {
                    addTimeEntityToDb();
                } else {
                    modifyTimerDb();
                }
                break;
            case R.id.btn_cacel:
            case R.id.lin_exit:
                finish();
                break;
        }
    }

    private void showDelTimerDialog() {
        if (oridinryDialog == null) {
            oridinryDialog = new OridinryDialog(TimerChangeActivity.this);
        }
        oridinryDialog.show(getLanguageFromResurce(R.string.if_del_power), getLanguageFromResurce(R.string.delete), getLanguageFromResurce(R.string.cancel));
        oridinryDialog.setOnDialogClickListener(new OridinryDialogClick() {
            @Override
            public void sure() {  //删除
                boolean isDel = DbTimerLocalUtil.delTimerById(timeId);
                showToastView(getLanguageFromResurce(R.string.del_power_tips) + (isDel ? getLanguageFromResurce(R.string.success) : getLanguageFromResurce(R.string.failed)));
                finish();
            }

            @Override
            public void noSure() {  //取消

            }
        });
    }

    private void modifyTimerDb() {
        //数据修改会影响到定时开关机，这里清除所有数据，重新添加
        TimeLocalEntity timeDbEntity = new TimeLocalEntity();
        timeDbEntity.setId(timeId);
        timeDbEntity.setTtOnTime(btn_time_on.getText().toString().trim());
        timeDbEntity.setTtOffTime(btn_time_off.getText().toString().trim());
        timeDbEntity.setTtMon(ck_mon.isChecked() ? "true" : "false");
        timeDbEntity.setTtTue(ck_tue.isChecked() ? "true" : "false");
        timeDbEntity.setTtWed(ck_wed.isChecked() ? "true" : "false");
        timeDbEntity.setTtThu(ck_thu.isChecked() ? "true" : "false");
        timeDbEntity.setTtFri(ck_fri.isChecked() ? "true" : "false");
        timeDbEntity.setTtSat(ck_sta.isChecked() ? "true" : "false");
        timeDbEntity.setTtSun(ck_sun.isChecked() ? "true" : "false");
        boolean isModify = DbTimerLocalUtil.modifyTimeById(timeDbEntity);
        if (isModify) {
            showToastView(getLanguageFromResurce(R.string.modifu_success));
            finish();
        } else {
            showToastView(getLanguageFromResurce(R.string.modifu_failed));
        }
    }

    /***
     * 添加数据到书库
     */
    private void addTimeEntityToDb() {
        waitDialogUtil.show(getLanguageFromResurce(R.string.deal));
        TimeLocalEntity timeDbEntity = new TimeLocalEntity();
        timeDbEntity.setTtOnTime(btn_time_on.getText().toString().trim());
        timeDbEntity.setTtOffTime(btn_time_off.getText().toString().trim());
        timeDbEntity.setTtMon(ck_mon.isChecked() ? "true" : "false");
        timeDbEntity.setTtTue(ck_tue.isChecked() ? "true" : "false");
        timeDbEntity.setTtWed(ck_wed.isChecked() ? "true" : "false");
        timeDbEntity.setTtThu(ck_thu.isChecked() ? "true" : "false");
        timeDbEntity.setTtFri(ck_fri.isChecked() ? "true" : "false");
        timeDbEntity.setTtSat(ck_sta.isChecked() ? "true" : "false");
        timeDbEntity.setTtSun(ck_sun.isChecked() ? "true" : "false");
        boolean isSave = DbTimerLocalUtil.addTimerDb(timeDbEntity);
        MyLog.powerOnOff("====保存本地数据库==" + isSave);
        if (isSave) {
            showToastView(getLanguageFromResurce(R.string.get_power_success));
            finish();
        } else {
            showToastView(getLanguageFromResurce(R.string.get_power_failed));
        }
    }

    public void showToastView(String toast) {
        MyToastView.getInstance().Toast(TimerChangeActivity.this, toast);
    }

    private void showTimeOnDialog() {
        if (timerChooiceDialog == null) {
            timerChooiceDialog = new TimerChooiceDialog(TimerChangeActivity.this);
        }
        timerChooiceDialog.show(onHour, onMin);
        timerChooiceDialog.setOnTimerChooiceListener(new TimerChooiceDialog.TimerChooiceListener() {
            @Override
            public void chooiceTimerNum(int paramInt1, int paramInt2) {
                onHour = paramInt1;
                onMin = paramInt2;
                btn_time_on.setText(date(paramInt1) + ":" + date(paramInt2));
            }
        });
    }

    private void showTimeOffDialog() {
        if (timerChooiceDialog == null) {
            timerChooiceDialog = new TimerChooiceDialog(TimerChangeActivity.this);
        }
        timerChooiceDialog.show(offHour, offMin);
        timerChooiceDialog.setOnTimerChooiceListener(new TimerChooiceDialog.TimerChooiceListener() {
            @Override
            public void chooiceTimerNum(int paramInt1, int paramInt2) {
                offHour = paramInt1;
                offMin = paramInt2;
                btn_time_off.setText(date(paramInt1) + ":" + date(paramInt2));
            }
        });
    }

    public static String date(int data) {
        String hour = String.valueOf(data);
        if (hour.length() == 1) {
            hour = "0" + hour;
        }
        return hour;
    }


    /**
     * 检查是否
     *
     * @return
     */
    public boolean isCheckNull() {
        boolean check_ck_mon = ck_mon.isChecked();
        boolean check_ck_tue = ck_tue.isChecked();
        boolean check_ck_wed = ck_wed.isChecked();
        boolean check_ck_thu = ck_thu.isChecked();
        boolean check1_ck_fri = ck_fri.isChecked();
        boolean check_ck_sta = ck_sta.isChecked();
        boolean check_ck_sun = ck_sun.isChecked();
        if (!check_ck_mon &&
                !check_ck_tue &&
                !check_ck_wed &&
                !check_ck_thu &&
                !check1_ck_fri &&
                !check_ck_sta &&
                !check_ck_sun) {
            return true;
        }
        return false;
    }
}
