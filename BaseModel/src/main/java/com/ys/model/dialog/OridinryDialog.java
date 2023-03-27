package com.ys.model.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ys.model.R;
import com.ys.model.config.DialogConfig;
import com.ys.model.listener.OridinryDialogClick;

/***
 * 通用dialog,一句话，两个按钮
 */
public class OridinryDialog {
    private Context context;
    private Dialog dialog;
    OridinryDialogClick dialogClick;
    public Button btn_sure;
    Button btn_no;
    public TextView dialog_title;
    public TextView view_text;

    public OridinryDialog(Context context) {
        this.context = context;
        dialog = new Dialog(context, R.style.MyDialog_Base);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialog_view = View.inflate(context, R.layout.update_dialog, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DialogConfig.SCREEN_WIDTH, DialogConfig.SCREEN_HEIGHT);
        dialog.setContentView(dialog_view, params);
        dialog.setCancelable(true); // true点击屏幕以外关闭dialog
        btn_sure = (Button) dialog_view.findViewById(R.id.btn_dialog_yes);
        btn_no = (Button) dialog_view.findViewById(R.id.btn_dialog_no);
        view_text = (TextView) dialog_view.findViewById(R.id.view_text);
        dialog_title = (TextView) dialog_view.findViewById(R.id.dialog_title);

        btn_sure.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (dialogClick != null) {
                    dialogClick.sure();
                }
                dissmiss();
            }
        });

        btn_no.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (dialogClick != null) {
                    dialogClick.noSure();
                }
                dissmiss();
            }
        });
    }

    public void show(String tips, String content) {
        try {
            view_text.setText(content);
            dialog_title.setText(tips);
            btn_sure.setText(context.getString(R.string.submit_base));
            btn_no.setText(context.getString(R.string.cancel_base));
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show(String content, String ok, String cacle) {
        try {
            view_text.setText(content);
            dialog_title.setText("Tips");
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

    public void setOnDialogClickListener(OridinryDialogClick dc) {
        dialogClick = dc;
    }

}
