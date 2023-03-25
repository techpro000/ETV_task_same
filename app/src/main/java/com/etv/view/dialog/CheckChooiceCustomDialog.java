package com.etv.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etv.util.SharedPerManager;
import com.ys.etv.R;

public class CheckChooiceCustomDialog {

    public Button btn_modify;
    private RadioGroup radio_screen;
    private Context context;
    private Dialog dialog;
    private RelativeLayout rela_bgg;
    private TextView tv_dialog_title;
    RadioButton rb_main, rb_second;

    public CheckChooiceCustomDialog(Context context) {
        this.context = context;
        dialog = new Dialog(context, R.style.MyDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialog_view = View.inflate(context, R.layout.dialog_check_custom_chooice, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(SharedPerManager.getScreenWidth(), SharedPerManager.getScreenHeight());
        initView(dialog_view);
        dialog.setContentView(dialog_view, params);
        dialog.setCancelable(true); // true点击屏幕以外关闭dialog
    }

    int tag = 0;

    private void initView(View view) {
        tv_dialog_title = (TextView) view.findViewById(R.id.tv_dialog_title);
        btn_modify = (Button) view.findViewById(R.id.btn_modify);
        radio_screen = (RadioGroup) view.findViewById(R.id.radio_screen);
        rb_main = (RadioButton) view.findViewById(R.id.rb_main);
        rb_second = (RadioButton) view.findViewById(R.id.rb_second);

        radio_screen.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb_main) {
                    tag = 0;
                } else if (i == R.id.rb_second) {
                    tag = 1;
                }
            }
        });
        btn_modify.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dissmiss();
                if (listener == null) {
                    return;
                }
                listener.checkPosition(tag);
            }
        });
        rela_bgg = (RelativeLayout) view.findViewById(R.id.rela_bgg);
        rela_bgg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dissmiss();
            }
        });
    }

    public void showDialog(String title, String check1, String check2, String submit, int tag) {
        try {
            dissmiss();
            dialog.show();
            tv_dialog_title.setText(title);
            rb_main.setText(check1);
            rb_second.setText(check2);
            btn_modify.setText(submit);
            this.tag = tag;
            if (tag == 0) {
                radio_screen.check(R.id.rb_main);
            } else if (tag == 1) {
                radio_screen.check(R.id.rb_second);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dissmiss() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    CheckCustomListener listener;

    public void setCheckCustomClickListener(CheckCustomListener listener) {
        this.listener = listener;
    }


    public interface CheckCustomListener {
        void checkPosition(int position);
    }

}
