package com.etv.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.etv.listener.ObjectClickListener;
import com.etv.util.SharedPerManager;
import com.ys.model.dialog.MyToastView;
import com.ys.etv.R;

/**
 * 设置图片播放间隔时间
 */
public class PicChangeTimeDialog implements OnClickListener {

    private Context context;
    private Dialog dialog;
    public Button btn_commit;
    Button btn_cacel;
    Button btn_reduce, btn_add;
    TextView tv_show_num;

    public PicChangeTimeDialog(Context context, int width, int height) {
        this.context = context;
        dialog = new Dialog(context, R.style.MyDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialog_view = View.inflate(context, R.layout.dialog_time_change, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        dialog.setContentView(dialog_view, params);
        dialog.setCancelable(true); // true点击屏幕以外关闭dialog
        btn_commit = (Button) dialog_view.findViewById(R.id.btn_commit);
        btn_cacel = (Button) dialog_view.findViewById(R.id.btn_cacel);
        btn_reduce = (Button) dialog_view.findViewById(R.id.btn_reduce);
        btn_add = (Button) dialog_view.findViewById(R.id.btn_add);
        tv_show_num = (TextView) dialog_view.findViewById(R.id.tv_show_num);
        btn_commit.setOnClickListener(this);
        btn_cacel.setOnClickListener(this);
        btn_reduce.setOnClickListener(this);
        btn_add.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int picDistanceNum = SharedPerManager.getPicDistanceTime();
        switch (view.getId()) {
            case R.id.btn_add:
                picDistanceNum++;
                SharedPerManager.setPicDistanceTime(picDistanceNum);
                updateTvView();
                break;
            case R.id.btn_reduce:
                if (picDistanceNum < 6) {
                    showDialogToast("请设定大于5秒的时间");
                    return;
                }
                picDistanceNum--;
                SharedPerManager.setPicDistanceTime(picDistanceNum);
                updateTvView();
                break;
            case R.id.btn_commit:
                dissmiss();
                if (listener != null) {
                    listener.clickSure(null);
                }
                break;
            case R.id.btn_cacel:
                dissmiss();
                if (listener != null) {
                    listener.clickSure(null);
                }
                break;
        }
    }

    private void showDialogToast(String desc) {
        MyToastView.getInstance().Toast(context, desc);
    }


    private void updateTvView() {
        int picDistanceNum = SharedPerManager.getPicDistanceTime();
        tv_show_num.setText(picDistanceNum + "");
    }

    public void show() {
        try {
            dissmiss();
            dialog.show();
            updateTvView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dissmiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    ObjectClickListener listener;

    public void setClickListener(ObjectClickListener listener) {
        this.listener = listener;
    }

}
