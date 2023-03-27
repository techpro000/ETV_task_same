package com.ys.model.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ys.model.R;
import com.ys.model.listener.WriteFileListener;

public class WriteFilesDialog {

    private Context context;
    private Dialog dialog;
    Button btn_no;
    TextView tv_write_num, tv_file_progress, tv_title;
    ProgressBar pro_current_total, pro_file_progress;
    TextView tv_copy_path, tv_past_path;

    public WriteFilesDialog(Context context) {
        this.context = context;
        dialog = new Dialog(context, R.style.MyDialog_Base);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialog_view = View.inflate(context, R.layout.dialog_write_foler, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(700, 700);
        dialog.setContentView(dialog_view, params);
        dialog.setCancelable(true); // true点击屏幕以外关闭dialog

        tv_copy_path = (TextView) dialog_view.findViewById(R.id.tv_copy_path);
        tv_past_path = (TextView) dialog_view.findViewById(R.id.tv_past_path);

        tv_title = (TextView) dialog_view.findViewById(R.id.tv_title);
        tv_write_num = (TextView) dialog_view.findViewById(R.id.tv_write_num);
        tv_file_progress = (TextView) dialog_view.findViewById(R.id.tv_file_progress);
        btn_no = (Button) dialog_view.findViewById(R.id.btn_dialog_no);
        pro_current_total = (ProgressBar) dialog_view.findViewById(R.id.pro_current_total);
        pro_file_progress = (ProgressBar) dialog_view.findViewById(R.id.pro_file_progress);
        btn_no.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (listener != null) {
                    listener.closeWriteFile();
                }
                dissmiss();
            }
        });
    }

    WriteFileListener listener;

    public void setWriteFileChangeLitener(WriteFileListener listener) {
        this.listener = listener;
    }


    public void show(String copyPath, String pastePath) {
        try {
            tv_copy_path.setText("Copy : " + copyPath);
            tv_past_path.setText("Paste: " + pastePath);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void udateTitleStatues(String title) {
        if (tv_title == null) {
            return;
        }
        tv_title.setText(title);
    }

    public void updateWriteFileProgress(int currentNum, int total, int progress) {
        int totalProgress = currentNum * 100 / total;
        tv_write_num.setText(currentNum + " / " + total);
        pro_current_total.setProgress(totalProgress);
        pro_file_progress.setProgress(progress);
        tv_file_progress.setText(progress + " %");
    }


    public void dissmiss() {
        try {
            if (dialog == null) {
                return;
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
