package com.etv.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ys.etv.R;


/***
 * 文件发送显示进度dialog
 */
public class FileSendDialog {

    private Context context;
    private Dialog dialog;
    FileSendDialogListener dialogClick;
    public TextView dialog_title;
    ProgressBar progressBar;
    Button btn_cacle_file_send;
    TextView tv_send_state;
    TextView tv_send_progress;

    public FileSendDialog(Context context, int width, int height) {
        this.context = context;
        dialog = new Dialog(context, R.style.MyDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialog_view = View.inflate(context, R.layout.dialog_send_file, null);
        dialog.setOnKeyListener(keylistener); // 返回键不会退出程序
        dialog.setCancelable(false); // true点击屏幕以外关闭dialog
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        dialog.setContentView(dialog_view, params);
        dialog_title = (TextView) dialog_view.findViewById(R.id.tv_title_file_send);
        progressBar = (ProgressBar) dialog_view.findViewById(R.id.progressBar_file_send);
        btn_cacle_file_send = (Button) dialog_view.findViewById(R.id.btn_cacle_file_send);
        tv_send_state = (TextView) dialog_view.findViewById(R.id.tv_send_state);
        tv_send_progress = (TextView) dialog_view.findViewById(R.id.tv_send_progress);
        btn_cacle_file_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogClick.clickCacle();
                dissmiss();
            }
        });
        RelativeLayout rela_bgg = (RelativeLayout) dialog_view.findViewById(R.id.rela_bgg);
        rela_bgg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dissmiss();
            }
        });
    }

    public void showDialog(String sendFileName, String btn_cacle) {
        dissmiss();
        dialog_title.setText(sendFileName);
        btn_cacle_file_send.setText(btn_cacle);
        progressBar.setProgress(0);
        dialog.show();
    }

    public void updateSendProgress(String state, int progressNum) {
        if (dialog != null) {
            tv_send_state.setText(state);
            tv_send_progress.setText(progressNum + "%");
            progressBar.setProgress(progressNum);
        }
    }


    public void dissmiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    DialogInterface.OnKeyListener keylistener = new DialogInterface.OnKeyListener() {
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                return false;
            } else {
                return true;
            }
        }
    };

    public void setOnCacleSendFileListener(FileSendDialogListener dc) {
        dialogClick = dc;
    }

    public interface FileSendDialogListener {
        void clickCacle();
    }


}
