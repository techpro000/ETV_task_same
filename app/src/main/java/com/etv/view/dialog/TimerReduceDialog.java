package com.etv.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.etv.util.SharedPerManager;
import com.ys.etv.R;

import java.util.Timer;
import java.util.TimerTask;


/***
 * 通用dialog,一句话，两个按钮
 */
public class TimerReduceDialog implements OnClickListener {
    private Context context;
    private Dialog dialog;
    TimerReduceListener dialogClick;


    public TimerReduceDialog(Context context) {
        this.context = context;
        dialog = new Dialog(context, R.style.MyDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setOnKeyListener(keylistener);
        dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
        View dialog_view = View.inflate(context, R.layout.activity_time_reduce, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(SharedPerManager.getScreenWidth(), SharedPerManager.getScreenHeight());
        dialog.setContentView(dialog_view, params);
        dialog.setCancelable(true); // true点击屏幕以外关闭dialog
        initDialog(dialog_view);
    }


    DialogInterface.OnKeyListener keylistener = new DialogInterface.OnKeyListener() {

        @Override
        public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                return true;
            }
            return false;
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_shut_down:
                if (dialogClick != null) {
                    dialogClick.sure();
                }
                dissmiss();
                break;
            case R.id.btn_cacel_shut:
                if (dialogClick != null) {
                    dialogClick.noSure();
                }
                dissmiss();
                break;
        }
    }

    TextView tv_reduce;
    Button btn_shut_down;
    Button btn_cacel_shut;
    private static final int DEFAULT_TIME = 40;
    int redusNum = DEFAULT_TIME;

    private void initDialog(View viewPop) {
        tv_reduce = (TextView) viewPop.findViewById(R.id.tv_reduce);
        btn_shut_down = (Button) viewPop.findViewById(R.id.btn_shut_down);
        btn_cacel_shut = (Button) viewPop.findViewById(R.id.btn_cacel_shut);
        btn_shut_down.setOnClickListener(this);
        btn_cacel_shut.setOnClickListener(this);
    }

    public void show() {
        try {
            dissmiss();
            dialog.show();
            startTimer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dissmiss() {
        try {
            cacelTimer();
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnDialogClickListener(TimerReduceListener dc) {
        dialogClick = dc;
    }

    public boolean isShow() {
        if (dialog == null) {
            return false;
        }
        return dialog.isShowing();
    }


    public interface TimerReduceListener {
        void sure();

        void noSure();
    }


    private static final int MESSAGE_REDUCE_TIME = 907;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MESSAGE_REDUCE_TIME) {
                redusNum--;
                if (redusNum < 0) {
                    if (dialogClick != null) {
                        dialogClick.sure();
                    }
                    dissmiss();
                    return;
                }
                if (redusNum < 10) {
                    tv_reduce.setText("0" + redusNum);
                } else {
                    tv_reduce.setText(redusNum + "");
                }
            }
        }
    };

    private Timer timer;
    private MyTask task;

    private class MyTask extends TimerTask {

        @Override
        public void run() {
            handler.sendEmptyMessage(MESSAGE_REDUCE_TIME);
        }
    }

    public void startTimer() {
        redusNum = DEFAULT_TIME;
        cacelTimer();
        timer = new Timer(true);
        task = new MyTask();
        timer.schedule(task, 0, 1000);
    }

    private void cacelTimer() {
        if (handler != null) {
            handler.removeMessages(MESSAGE_REDUCE_TIME);
        }
        if (timer != null) {
            timer.cancel();
        }
        if (task != null) {
            task.cancel();
        }
    }

}
