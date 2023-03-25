package com.etv.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.etv.listener.ThreeClickListener;
import com.etv.util.SharedPerManager;
import com.ys.etv.R;


/**
 * task 点击按钮
 */
public class TaskClickDialog {
    private Context context;
    private Dialog dialog;
    ThreeClickListener dialogClick;
    public Button btn_sure;
    //    TextView tv_pro_num;
    Button btn_no;
    Button btn_first;

    public TaskClickDialog(Context context) {
        this.context = context;
        dialog = new Dialog(context, R.style.MyDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialog_view = View.inflate(context, R.layout.task_dialog, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(SharedPerManager.getScreenWidth(), SharedPerManager.getScreenHeight());
        dialog.setContentView(dialog_view, params);
        dialog.setCancelable(true); // true点击屏幕以外关闭dialog

//        tv_pro_num = (TextView) dialog_view.findViewById(R.id.tv_pro_num);
        btn_first = (Button) dialog_view.findViewById(R.id.btn_first);
        btn_sure = (Button) dialog_view.findViewById(R.id.btn_dialog_yes);
        btn_no = (Button) dialog_view.findViewById(R.id.btn_dialog_no);

        btn_first.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogClick != null) {
                    dialogClick.clickFirst();
                }
                dissmiss();
            }
        });

        btn_sure.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (dialogClick != null) {
                    dialogClick.clickSecond();
                }
                dissmiss();
            }
        });

        btn_no.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (dialogClick != null) {
                    dialogClick.clickThird();
                }
                dissmiss();
            }
        });
        RelativeLayout rela_bgg = (RelativeLayout) dialog_view.findViewById(R.id.rela_bgg);
        rela_bgg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dissmiss();
            }
        });
    }

    public void show(String first, String ok, String cacle) {
        try {
//            tv_pro_num.setText(proNum);
            btn_first.setText(first);
            btn_sure.setText(ok);
            btn_no.setText(cacle);
            dialog.show();
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

    public void setOnDialogClickListener(ThreeClickListener dc) {
        dialogClick = dc;
    }


}
