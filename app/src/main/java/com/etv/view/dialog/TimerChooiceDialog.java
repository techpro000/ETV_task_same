package com.etv.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etv.adapter.TextAdapter;
import com.etv.util.SharedPerManager;
import com.ys.etv.R;

import java.util.ArrayList;
import java.util.List;

public class TimerChooiceDialog implements OnClickListener, AdapterView.OnItemClickListener {
    public static final int TAG_CLOSE_EQUIP = 1;
    public static final int TAG_OPEN_EQUIP = 0;
    Button btn_cacel;
    Button btn_hour;
    Button btn_min;
    Button btn_ok;
    private Context context;
    private Dialog dialog;
    TimerChooiceListener listener;
    GridView lv_time;
    TextAdapter adapter;
    List<String> listShow = new ArrayList<>();
    //0 hour  1:min
    int showTag = 0;
    TextView tv_chooide_desc;

    public TimerChooiceDialog(Context context) {
        this.context = context;
        dialog = new Dialog(context, R.style.MyDialog);
        dialog.requestWindowFeature(1);
        View view = View.inflate(context, R.layout.dialog_timer_show, null);
        LayoutParams localLayoutParams = new LayoutParams(SharedPerManager.getScreenWidth(), SharedPerManager.getScreenHeight());
        dialog.setContentView(view, localLayoutParams);
        dialog.setCancelable(false);
        btn_hour = ((Button) view.findViewById(R.id.btn_hour));
        btn_min = ((Button) view.findViewById(R.id.btn_min));
        btn_ok = ((Button) view.findViewById(R.id.btn_ok));
        btn_cacel = ((Button) view.findViewById(R.id.btn_cacel));
        lv_time = (GridView) view.findViewById(R.id.lv_time);
        tv_chooide_desc = (TextView) view.findViewById(R.id.tv_chooide_desc);
        btn_hour.setOnClickListener(this);
        btn_min.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        btn_cacel.setOnClickListener(this);
        adapter = new TextAdapter(context, listShow);
        lv_time.setAdapter(adapter);
        lv_time.setOnItemClickListener(this);
        RelativeLayout rela_bgg = (RelativeLayout) view.findViewById(R.id.rela_bgg);
        rela_bgg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dissmiss();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        lv_time.setVisibility(View.INVISIBLE);
        tv_chooide_desc.setVisibility(View.INVISIBLE);
        if (showTag == 0) {  //小时
            btn_hour.setText(listShow.get(i));
            btn_hour.requestFocus();
        } else if (showTag == 1) {  //分钟
            btn_min.requestFocus();
            btn_min.setText(listShow.get(i));
        }
    }


    public void dissmiss() {
        if ((this.dialog != null) && (this.dialog.isShowing())) {
            dialog.dismiss();
        }
    }

    public void onClick(View paramView) {
        switch (paramView.getId()) {
            case R.id.btn_hour:
                showHourNum();
                break;
            case R.id.btn_min:
                lv_time.setVisibility(View.VISIBLE);
                tv_chooide_desc.setVisibility(View.VISIBLE);
                tv_chooide_desc.setText("请选择分钟 !!");
                lv_time.setNumColumns(10);
                listShow = getMins();
                adapter.setList(listShow);
                break;
            case R.id.btn_ok:
                int backHour = Integer.parseInt(btn_hour.getText().toString());
                int backMin = Integer.parseInt(btn_min.getText().toString());
                if (listener != null) {
                    listener.chooiceTimerNum(backHour, backMin);
                }
                dissmiss();
                return;
            case R.id.btn_cacel:
                dissmiss();
        }
    }

    private void showHourNum() {
        lv_time.setVisibility(View.VISIBLE);
        tv_chooide_desc.setVisibility(View.VISIBLE);
        tv_chooide_desc.setText("请选择小时 !!");
        lv_time.setNumColumns(4);
        listShow = getHours();
        adapter.setList(listShow);
    }

    public void show(int paramInt2, int paramInt3) {
        try {
            if (paramInt2 < 10) {
                btn_hour.setText("0" + paramInt2 + "");
            } else {
                btn_hour.setText(paramInt2 + "");
            }
            if (paramInt3 < 10) {
                btn_min.setText("0" + paramInt3 + "");
            } else {
                btn_min.setText(paramInt3 + "");
            }
            showHourNum();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnTimerChooiceListener(TimerChooiceListener paramTimerChooiceListener) {
        listener = paramTimerChooiceListener;
    }

    public interface TimerChooiceListener {
        void chooiceTimerNum(int paramInt1, int paramInt2);
    }

    public List<String> getHours() {
        listShow.clear();
        List<String> hours = new ArrayList<>();
        showTag = 0;
        for (int i = 0; i < 24; i++) {
            if (i < 10) {
                hours.add("0" + i);
            } else {
                hours.add(i + "");
            }

        }
        return hours;
    }

    public List<String> getMins() {
        listShow.clear();
        showTag = 1;
        List<String> mins = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            if (i < 10) {
                mins.add("0" + i);
            } else {
                mins.add(i + "");
            }
        }
        return mins;
    }

}
