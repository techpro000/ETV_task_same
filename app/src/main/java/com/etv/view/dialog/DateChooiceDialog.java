package com.etv.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ys.etv.R;
import com.etv.util.SharedPerManager;


public class DateChooiceDialog implements OnClickListener {
    public static final int TAG_CLOSE_EQUIP = 1;
    public static final int TAG_OPEN_EQUIP = 0;
    private final RelativeLayout rela_bgg;
    Button btn_cacel;
    Button btn_hour_add;
    Button btn_min_add;
    Button btn_ok;
    Button btn_reduce_hour;
    Button btn_reduce_min;
    Button btn_day_add;
    Button btn_reduce_day;
    private Context context;
    int currentHour;
    int currentMin;
    int day;
    private Dialog dialog;
    TimerChooiceListener listener;
    int tag;
    TextView tv_hour;
    TextView tv_min;
    TextView tv_day;

    public DateChooiceDialog(Context context) {
        this.context = context;
        dialog = new Dialog(context, R.style.MyDialog);
        dialog.requestWindowFeature(1);
        View view = View.inflate(context, R.layout.dialog_date_show, null);
        LayoutParams localLayoutParams = new LayoutParams(SharedPerManager.getScreenWidth(), SharedPerManager.getScreenHeight());
        dialog.setContentView(view, localLayoutParams);
        dialog.setCancelable(false);
        tv_hour = ((TextView) view.findViewById(R.id.tv_hour));
        tv_min = ((TextView) view.findViewById(R.id.tv_min));
        tv_day = ((TextView) view.findViewById(R.id.tv_day));
        btn_hour_add = ((Button) view.findViewById(R.id.btn_hour_add));
        btn_min_add = ((Button) view.findViewById(R.id.btn_min_add));
        btn_day_add = ((Button) view.findViewById(R.id.btn_day_add));
        btn_reduce_day = ((Button) view.findViewById(R.id.btn_reduce_day));
        btn_reduce_hour = ((Button) view.findViewById(R.id.btn_reduce_hour));
        btn_reduce_min = ((Button) view.findViewById(R.id.btn_reduce_min));
        btn_ok = ((Button) view.findViewById(R.id.btn_ok));
        btn_cacel = ((Button) view.findViewById(R.id.btn_cacel));
        btn_hour_add.setOnClickListener(this);
        btn_min_add.setOnClickListener(this);
        btn_reduce_hour.setOnClickListener(this);
        btn_reduce_min.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        btn_cacel.setOnClickListener(this);
        btn_day_add.setOnClickListener(this);
        btn_reduce_day.setOnClickListener(this);
        rela_bgg = (RelativeLayout) view.findViewById(R.id.rela_bgg);
        rela_bgg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dissmiss();
            }
        });
    }

    private void updateShareOpenTime(int paramInt1, int paramInt2) {
//        if (this.tag == 0)  {
//            SharedPerManager.setOpenEquipHour(paramInt1);
//            SharedPerManager.setOpenEquipMin(paramInt2);
//        }
//        while (this.tag != 1) {
//            return;
//        }
//        SharedPerManager.setCloseEquipHour(paramInt1);
//        SharedPerManager.setCloseEqiopMin(paramInt2);
    }

    private void updateTimer(int paramInt1, int paramInt2, int day) {
        tv_hour.setText(paramInt1 + "");
        tv_min.setText(paramInt2 + "");
        tv_day.setText(day + "");
    }

    public void dissmiss() {
        if ((this.dialog != null) && (this.dialog.isShowing())) {
            dialog.dismiss();
        }
    }

    public void onClick(View paramView) {
        int near;
        switch (paramView.getId()) {
            case R.id.btn_hour_add:
                currentHour += 1;
                if (!isleapyear(currentHour)) {
                    if (tv_min.getText().toString().equals("2")) {
                        if (tv_day.getText().toString().equals("29")) {
                            day = 28;
                            tv_day.setText(day + "");
                        }
                    }
                }
                tv_hour.setText(this.currentHour + "");
                return;
            case R.id.btn_min_add:
                currentMin += 1;
                if (this.currentMin > 12) {
                    currentMin = 1;
                }
                tv_min.setText(this.currentMin + "");
                if (tv_min.getText().toString().equals("4") || tv_min.getText().toString().equals("6") ||
                        tv_min.getText().toString().equals("9") || tv_min.getText().toString().equals("11")) {
                    if (this.day > 30) {
                        day = 30;
                        tv_day.setText(day + "");
                    }
                } else if (tv_min.getText().toString().equals("2")) {
                    near = Integer.parseInt(tv_hour.getText().toString().trim());
                    if (isleapyear(near)) {
                        if (this.day > 29) {
                            day = 29;
                            tv_day.setText(day + "");
                        }
                    } else {
                        if (this.day > 28) {
                            day = 28;
                            tv_day.setText(day + "");
                        }
                    }
                }
                return;
            case R.id.btn_day_add:
                day += 1;
                Log.i("DateChooiceDialog", tv_min.getText().toString());
                if (tv_min.getText().toString().equals("1") || tv_min.getText().toString().equals("3") ||
                        tv_min.getText().toString().equals("5") || tv_min.getText().toString().equals("7") ||
                        tv_min.getText().toString().equals("8") || tv_min.getText().toString().equals("10") ||
                        tv_min.getText().toString().equals("12")) {
                    if (this.day > 31) {
                        day = 1;
                    }
                } else if (tv_min.getText().toString().equals("4") || tv_min.getText().toString().equals("6") ||
                        tv_min.getText().toString().equals("9") || tv_min.getText().toString().equals("11")) {
                    if (this.day > 30) {
                        day = 1;
                    }
                } else if (tv_min.getText().toString().equals("2")) {
                    near = Integer.parseInt(tv_hour.getText().toString().trim());
                    if (isleapyear(near)) {
                        if (this.day > 29) {
                            day = 1;
                        }
                    } else {
                        if (this.day > 28) {
                            day = 1;
                        }
                    }
                }
                tv_day.setText(this.day + "");
                return;
            case R.id.btn_reduce_hour:
                currentHour -= 1;
                if (!isleapyear(currentHour)) {
                    if (tv_min.getText().toString().equals("2")) {
                        if (tv_day.getText().toString().equals("29")) {
                            day = 28;
                            tv_day.setText(day + "");
                        }
                    }
                }
                if (this.currentHour < 0) {
                    currentHour = 0;
                }
                tv_hour.setText(this.currentHour + "");
                return;
            case R.id.btn_reduce_min:
                currentMin -= 1;
                if (this.currentMin < 1) {
                    currentMin = 12;
                }
                tv_min.setText(this.currentMin + "");
                if (tv_min.getText().toString().equals("4") || tv_min.getText().toString().equals("6") ||
                        tv_min.getText().toString().equals("9") || tv_min.getText().toString().equals("11")) {
                    if (this.day > 30) {
                        day = 30;
                        tv_day.setText(day + "");
                    }
                } else if (tv_min.getText().toString().equals("2")) {
                    near = Integer.parseInt(tv_hour.getText().toString().trim());
                    if (isleapyear(near)) {
                        if (this.day > 29) {
                            day = 29;
                            tv_day.setText(day + "");
                        }
                    } else {
                        if (this.day > 28) {
                            day = 28;
                            tv_day.setText(day + "");
                        }
                    }
                }
                return;
            case R.id.btn_reduce_day:
                day -= 1;
                if (this.day < 1) {
                    if (tv_min.getText().toString().equals("1") || tv_min.getText().toString().equals("3") ||
                            tv_min.getText().toString().equals("5") || tv_min.getText().toString().equals("7") ||
                            tv_min.getText().toString().equals("8") || tv_min.getText().toString().equals("10") ||
                            tv_min.getText().toString().equals("12")) {
                        day = 31;
                    } else if (tv_min.getText().toString().equals("4") || tv_min.getText().toString().equals("6") ||
                            tv_min.getText().toString().equals("9") || tv_min.getText().toString().equals("11")) {
                        day = 30;
                    } else if (tv_min.getText().toString().equals("2")) {
                        near = Integer.parseInt(tv_hour.getText().toString().trim());
                        if (isleapyear(near)) {
                            day = 29;
                        } else {
                            day = 28;
                        }
                    }
                }
                tv_day.setText(this.day + "");
                return;
            case R.id.btn_ok:
                updateShareOpenTime(this.currentHour, currentMin);
                if (this.listener != null) {
                    listener.chooiceTimerNum(this.currentHour, currentMin, day);
                }
                dissmiss();
                return;
            case R.id.btn_cacel:
                dissmiss();
        }
    }

    public void setOnTimerChooiceListener(TimerChooiceListener paramTimerChooiceListener) {
        listener = paramTimerChooiceListener;
    }

    public void show(int paramInt2, int paramInt3, int day) {
        currentHour = paramInt2;
        currentMin = paramInt3;
        this.day = day;
        Log.e("haha", "=================获取的年月日==" + currentHour + "  /" + currentMin + "  /" + this.day);
        updateTimer(this.currentHour, currentMin, this.day);
        dialog.show();
    }

    public interface TimerChooiceListener {
        void chooiceTimerNum(int paramInt1, int paramInt2, int day);
    }

    private boolean isleapyear(int year) {
        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            return true;
        } else {
            return false;
        }
    }
}
