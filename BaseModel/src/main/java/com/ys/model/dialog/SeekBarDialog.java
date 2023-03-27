package com.ys.model.dialog;

import android.content.Context;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ys.model.R;
import com.ys.model.config.DialogConfig;
import com.ys.model.listener.SeekBarBackListener;

/***
 * 通用dialog,一句话，两个按钮
 */
public class SeekBarDialog {
    private Context context;
    private android.app.Dialog dialog;
    SeekBarBackListener dialogClick;
    public Button btn_sure;
    Button btn_no;
    public android.widget.TextView dialog_title;
    android.widget.SeekBar seekBar_progress;
    RelativeLayout rela_click_close;
    android.widget.TextView tv_progress;

    public SeekBarDialog(Context context) {
        this.context = context;
        dialog = new android.app.Dialog(context, R.style.MyDialog_Base);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        android.view.View dialog_view = android.view.View.inflate(context, R.layout.seekbar_dialog, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DialogConfig.SCREEN_WIDTH, DialogConfig.SCREEN_HEIGHT);
        dialog.setContentView(dialog_view, params);
        dialog.setCancelable(true); // true点击屏幕以外关闭dialog

        rela_click_close = (RelativeLayout) dialog_view.findViewById(R.id.rela_click_close);
        rela_click_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                dissmiss();
            }
        });
        btn_sure = (Button) dialog_view.findViewById(R.id.btn_dialog_yes);
        btn_no = (Button) dialog_view.findViewById(R.id.btn_dialog_no);
        tv_progress = (android.widget.TextView) dialog_view.findViewById(R.id.tv_progress);
        dialog_title = (android.widget.TextView) dialog_view.findViewById(R.id.dialog_title);
        seekBar_progress = (android.widget.SeekBar) dialog_view.findViewById(R.id.seekBar_progress);
        btn_sure.setOnClickListener(new OnClickListener() {
            public void onClick(android.view.View v) {
                dissmiss();
            }
        });

        btn_no.setOnClickListener(new OnClickListener() {
            public void onClick(android.view.View v) {
                dissmiss();
            }
        });

        seekBar_progress.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean b) {
                tv_progress.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(android.widget.SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(android.widget.SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (dialogClick != null) {
                    dialogClick.backNumInfo(progress);
                }
            }
        });
    }

    /***
     * 通用 100 得进度
     * @param title
     * @param progress
     */
    public void show(String title, int progress) {
        try {
            tv_progress.setText(progress + "");
            seekBar_progress.setProgress(progress);
            dialog_title.setText(title);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /***
     * 高度定制化得seekBar
     * @param title
     * @param progress
     * @param totalProgress
     */
    public void show(String title, int progress, int totalProgress) {
        try {
            seekBar_progress.setMax(totalProgress);
            tv_progress.setText(progress + "");
            seekBar_progress.setProgress(progress);
            dialog_title.setText(title);
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

    public void setOnDialogClickListener(SeekBarBackListener dc) {
        dialogClick = dc;
    }

}
