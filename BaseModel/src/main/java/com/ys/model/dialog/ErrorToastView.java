package com.ys.model.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ys.model.R;


@SuppressLint("ResourceAsColor")
public class ErrorToastView {

    private static ErrorToastView instance = null;

    public static ErrorToastView getInstance() {
        if (instance == null) {
            instance = new ErrorToastView();
        }
        return instance;
    }

    /**
     * 调用方法 MyToast.getInstence.toast(this,info);
     *
     * @param context
     * @param info
     */
    Toast toast;

    public void Toast(Context context, String info) {
        try {
            if (toast != null) {
                toast.cancel();
            }
            View layout = LayoutInflater.from(context).inflate(R.layout.mytoast_view, null);
            TextView text = (TextView) layout.findViewById(R.id.text);
            if (info == null || info.length() < 2) {
                text.setText("   ");
            }
            text.setText(info);
            text.setTextColor(Color.RED);
            toast = Toast.makeText(context, "Custom location Toast", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 50);
            toast.setView(layout);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}