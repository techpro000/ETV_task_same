package com.ys.model.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.ys.model.R;

import java.util.Timer;
import java.util.TimerTask;

public class WaitDialogUtil {

    Dialog waitDialog;
    Context context;
    public static final String TAG = WaitDialogUtil.class.getName();
    private final TextView mTv;

    public WaitDialogUtil(Context context) {
        this.context = context;
        waitDialog = new Dialog(context, R.style.MyDialog_Base);
        View recdialog = View.inflate(context, R.layout.dialog_wait, null);
        mTv = (TextView) recdialog.findViewById(R.id.tv_dialog_wait);
        waitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        waitDialog.setContentView(recdialog);
        waitDialog.setCanceledOnTouchOutside(false);
        waitDialog.setCancelable(true);
        waitDialog.getWindow().setGravity(Gravity.CENTER);
    }

    public void show(String text_dialog) {
        try {
            dismiss();
            mTv.setText(text_dialog);
            waitDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismiss() {
        if (waitDialog == null) {
            return;
        }
        try {
            cacelTimer();
            if (waitDialog != null && waitDialog.isShowing()) {
                waitDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isShowing() {
        if (waitDialog != null && waitDialog.isShowing()) {
            return true;
        }
        return false;
    }

    public void show(String text_dialog, int time) {
        if (waitDialog == null) {
            return;
        }
        try {
            dismiss();
            mTv.setText(text_dialog);
            waitDialog.show();
            if (time > 1000) {
                startTimer(time);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startTimer(long duration) {
        cacelTimer();
        time = new Timer(true);
        task = new MyTask();
        time.schedule(task, duration);
    }

    public void cacelTimer() {
        if (time != null) {
            time.cancel();
        }
        if (task != null) {
            task.cancel();
        }
    }

    public Window getWindow() {
        return waitDialog.getWindow();
    }

    Timer time;
    MyTask task;

    class MyTask extends TimerTask {

        @Override
        public void run() {
            handler.sendEmptyMessage(MESSAGE_CACEL_TIMEER_DIALOG);
        }
    }

    private static final int MESSAGE_CACEL_TIMEER_DIALOG = 56214;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handler.removeMessages(msg.what);
            switch (msg.what) {
                case MESSAGE_CACEL_TIMEER_DIALOG:
                    dismiss();
                    break;
            }
        }
    };

}
