package com.etv.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etv.setting.util.InterUtil;
import com.ys.etv.R;
import com.ys.model.util.KeyBoardUtil;
import com.etv.util.SharedPerManager;
import com.ys.model.dialog.MyToastView;

/***
 * intent dialog
 */
public class IntentDialog implements View.OnFocusChangeListener {
    private Context context;
    private Dialog dialog;
    EditTextInitDialogListener dialogClick;
    public Button btn_modify, btn_exit;
    public TextView dialog_title;
    EditText tv_ip_address, tv_subnet_mask, tv_gateway, tv_dns1, tv_dns2;

    public IntentDialog(final Context context) {
        this.context = context;
        dialog = new Dialog(context, R.style.MyDialog);
        dialog.setCancelable(false); // true点击屏幕以外关闭dialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialog_view = View.inflate(context, R.layout.dialog_intent, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(SharedPerManager.getScreenWidth(), SharedPerManager.getScreenHeight());
        dialog.setContentView(dialog_view, params);

        btn_modify = (Button) dialog_view.findViewById(R.id.btn_modify);
        btn_exit = (Button) dialog_view.findViewById(R.id.btn_exit);
        tv_ip_address = (EditText) dialog_view.findViewById(R.id.tv_ip_address);
        tv_subnet_mask = (EditText) dialog_view.findViewById(R.id.tv_subnet_mask);
        tv_gateway = (EditText) dialog_view.findViewById(R.id.tv_gateway);
        tv_dns1 = (EditText) dialog_view.findViewById(R.id.tv_dns1);
        tv_dns2 = (EditText) dialog_view.findViewById(R.id.tv_dns2);
        dialog_title = (TextView) dialog_view.findViewById(R.id.tv_dialog_title);
        tv_ip_address.setOnFocusChangeListener(this);
        tv_subnet_mask.setOnFocusChangeListener(this);
        tv_gateway.setOnFocusChangeListener(this);
        tv_dns1.setOnFocusChangeListener(this);
        tv_dns2.setOnFocusChangeListener(this);
        btn_modify.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (dialogClick != null) {
                    String ip_address = tv_ip_address.getText().toString().trim();
                    String subnet_mask = tv_subnet_mask.getText().toString().trim();
                    String gateway = tv_gateway.getText().toString().trim();
                    String dns1 = tv_dns1.getText().toString().trim();
                    String dns2 = tv_dns2.getText().toString().trim();
                    boolean isIpRight = InterUtil.isIpNetMacSuccess(ip_address);
                    if (!isIpRight) {
                        showToastView("请输入合法的IP地址");
                        return;
                    }
                    if (!InterUtil.isIpNetMacSuccess(subnet_mask)) {
                        showToastView("请输入正确的子网掩码");
                        return;
                    }
                    if (!InterUtil.isIpNetMacSuccess(gateway)) {
                        showToastView("请输入合法的网关");
                        return;
                    }
                    if (!InterUtil.isIpNetMacSuccess(dns1)) {
                        showToastView("请输入合法的DNS1");
                        return;
                    }
                    if (!InterUtil.isIpNetMacSuccess(dns2)) {
                        showToastView("请输入合法的DNS2");
                        return;
                    }
                    dialogClick.commit(ip_address, subnet_mask, gateway, dns1, dns2);
                }
                dissmiss();
            }
        });
        btn_exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogClick.exit();
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

    private void showToastView(String desc) {
        MyToastView.getInstance().Toast(context, desc);
    }

    public void show(String ip, String mask, String gateway, String dns1, String dns2) {
        tv_ip_address.setText(ip);
        tv_subnet_mask.setText(mask);
        tv_gateway.setText(gateway);
        tv_dns1.setText(dns1);
        tv_dns2.setText(dns2);
        dialog.show();
    }

    public void dissmiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void setOnDialogClickListener(EditTextInitDialogListener dc) {
        dialogClick = dc;
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (b) {
            KeyBoardUtil.showKeyBord(view);
        } else {
            KeyBoardUtil.hiddleBord(view);
        }
    }

    public interface EditTextInitDialogListener {
        void commit(String ip, String mask, String gateway, String dns1, String dns2);

        void exit();
    }
}
