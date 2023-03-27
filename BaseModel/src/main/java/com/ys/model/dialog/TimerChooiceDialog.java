package com.ys.model.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import com.ys.model.R;
import com.ys.model.config.DialogConfig;

public class TimerChooiceDialog implements OnClickListener {
    Button btn_cacel;
    Button btn_ok;
    private Context context;
    private Dialog dialog;
    TimerChooiceListener listener;
    TimePicker mTimepicker;

    public TimerChooiceDialog(Context context) {
        this.context = context;
        dialog = new Dialog(context, R.style.MyDialog_Base);
        dialog.requestWindowFeature(1);
        View view = View.inflate(context, R.layout.dialog_timer_chooice, null);
        LayoutParams localLayoutParams = new LayoutParams(DialogConfig.SCREEN_WIDTH, DialogConfig.SCREEN_HEIGHT);
        dialog.setContentView(view, localLayoutParams);
        dialog.setCancelable(false);
        btn_ok = ((Button) view.findViewById(R.id.btn_ok));
        btn_cacel = ((Button) view.findViewById(R.id.btn_cacel));
        btn_ok.setOnClickListener(this);
        btn_cacel.setOnClickListener(this);
        RelativeLayout rela_bgg = (RelativeLayout) view.findViewById(R.id.rela_bgg);
        rela_bgg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dissmiss();
            }
        });
        mTimepicker = (TimePicker) view.findViewById(R.id.timepicker);
        mTimepicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);  //设置点击事件不弹键盘
        mTimepicker.setIs24HourView(true);   //设置时间显示为24小时
        mTimepicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {  //获取当前选择的时间
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                hourBack = hourOfDay;
                minBack = minute;
            }
        });
    }

    public void dissmiss() {
        if ((this.dialog != null) && (this.dialog.isShowing())) {
            dialog.dismiss();
        }
    }

    public void onClick(View paramView) {
        if (paramView.getId() == R.id.btn_ok) {
            if (listener != null) {
                listener.chooiceTimerNum(hourBack, minBack);
            }
            dissmiss();
        } else if (paramView.getId() == R.id.btn_cacel) {
            dissmiss();
        }
    }

    int hourBack = 8;
    int minBack = 30;

    @SuppressLint("NewApi")
    public void show(int hour, int min) {
        try {
            dissmiss();
            hourBack = hour;
            minBack = min;
            mTimepicker.setHour(hour);  //设置当前小时
            mTimepicker.setMinute(min); //设置当前分（0-59）
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnTimerChooiceListener(TimerChooiceListener paramTimerChooiceListener) {
        listener = paramTimerChooiceListener;
    }

    public interface TimerChooiceListener {
        void chooiceTimerNum(int hour, int min);
    }

}
