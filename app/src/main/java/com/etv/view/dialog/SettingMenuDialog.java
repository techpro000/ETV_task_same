package com.etv.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.etv.config.AppConfig;
import com.etv.config.AppInfo;
import com.etv.util.CodeUtil;
import com.etv.util.MyLog;
import com.etv.util.QRCodeUtil;
import com.etv.util.SharedPerManager;
import com.etv.util.SharedPerUtil;
import com.etv.util.image.glide.GlideImageUtil;
import com.ys.etv.R;
import com.ys.model.dialog.MyToastView;

/***
 * 主界面 弹窗二维码菜单界面
 */
public class SettingMenuDialog implements OnClickListener {

    private Context context;
    private Dialog dialog;
    SettingMenuClickListener listener;
    Button btn_work_model;
    Button btn_exit;
    RelativeLayout rela_dialog_bgg;
    ImageView iv_qr_code_scan;
    TextView tv_ip, tv_mac, tv_version_sys, tv_version_app, tv_nickname, tv_line_type;
    LinearLayout lin_wechat_code, lin_bind_ercode, lin_bottom_info, lin_all_qrcode;
    ImageView iv_qr_code_chat;
    TextView tv_scan_view;

    public SettingMenuDialog(Context context) {
        this.context = context;
        dialog = new Dialog(context, R.style.MyDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialog_view = View.inflate(context, R.layout.dialog_setting_menu, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(SharedPerManager.getScreenWidth(), SharedPerManager.getScreenHeight());
        dialog.setContentView(dialog_view, params);
        dialog.setCancelable(true); // true点击屏幕以外关闭dialog
        iv_qr_code_scan = (ImageView) dialog_view.findViewById(R.id.iv_qr_code_scan);
        btn_work_model = (Button) dialog_view.findViewById(R.id.btn_work_model);
        btn_exit = (Button) dialog_view.findViewById(R.id.btn_exit);
        rela_dialog_bgg = (RelativeLayout) dialog_view.findViewById(R.id.rela_dialog_bgg);
        tv_ip = (TextView) dialog_view.findViewById(R.id.tv_ip);
        tv_mac = (TextView) dialog_view.findViewById(R.id.tv_mac);
        tv_version_sys = (TextView) dialog_view.findViewById(R.id.tv_version_sys);
        tv_version_app = (TextView) dialog_view.findViewById(R.id.tv_version_app);
        tv_nickname = (TextView) dialog_view.findViewById(R.id.tv_nickname);
        tv_line_type = (TextView) dialog_view.findViewById(R.id.tv_line_type);
        tv_scan_view = (TextView) dialog_view.findViewById(R.id.tv_scan_view);
        iv_qr_code_chat = (ImageView) dialog_view.findViewById(R.id.iv_qr_code_chat);
        rela_dialog_bgg.setOnClickListener(this);
        btn_work_model.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
        btn_work_model.requestFocus();
        lin_wechat_code = (LinearLayout) dialog_view.findViewById(R.id.lin_wechat_code);
        lin_bind_ercode = (LinearLayout) dialog_view.findViewById(R.id.lin_bind_ercode);
        lin_bottom_info = (LinearLayout) dialog_view.findViewById(R.id.lin_bottom_info);
        lin_all_qrcode = (LinearLayout) dialog_view.findViewById(R.id.lin_all_qrcode);
        lin_wechat_code.setVisibility(View.VISIBLE);
        iv_qr_code_chat.setBackgroundResource(R.mipmap.icon_etv_code);
    }

    private static final int DISSMISS_DIALOG_AUTO = 4521;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DISSMISS_DIALOG_AUTO:
                    dissmiss();
                    break;
            }
        }
    };

    public void show(String backTitle) {
        Log.e("5555", "====焦点状态==弹窗显示==");
        try {
            String ipaddress = CodeUtil.getIpAddress(context, "主界面弹窗调用");
            tv_ip.setText(ipaddress);

            tv_mac.setText(CodeUtil.getUniquePsuedoID());
            String sysCodeVersion = CodeUtil.getSysVersion();
            String appCodeVersion = CodeUtil.getAppVersion(context);
            tv_version_sys.setText(sysCodeVersion);
            tv_version_app.setText(appCodeVersion);
            tv_nickname.setText(SharedPerManager.getDevNickName());
            btn_exit.setText(backTitle);
            String socket_type = SharedPerUtil.SOCKEY_TYPE() == AppConfig.SOCKEY_TYPE_WEBSOCKET ? "WebSocket" : "Socket";
            tv_line_type.setText(socket_type + "-SAME");
            dialog.show();
            createErCode();
            handler.sendEmptyMessageDelayed(DISSMISS_DIALOG_AUTO, 15 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dissmiss() {
        try {
            handler.removeMessages(DISSMISS_DIALOG_AUTO);
            if (iv_qr_code_scan != null) {
                GlideImageUtil.clearViewCache(iv_qr_code_scan);
            }
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String imagePath = AppInfo.ER_CODE_PATH();

    private void createErCode() {
        String devCode = CodeUtil.getUniquePsuedoID();
        String ipConnect = SharedPerUtil.getWebHostIpAddress();
        String isLine = "-1";
        if (AppConfig.isOnline) {
            isLine = "1";
        } else {
            isLine = "-1";
        }
        String nickName = SharedPerManager.getDevNickName();
        String guardianCode = "{\"type\":\"bind\",\"code\":\"" + devCode + "\",\"ip\":\"" + ipConnect + "\",\"isLine\":\"" + isLine + "\",\"nickName\":\"" + nickName + "\"}";
        MyLog.cdl("====生成二维码===start===" + System.currentTimeMillis() + " / " + guardianCode);
        QRCodeUtil qrCodeUtil = new QRCodeUtil(context, new QRCodeUtil.ErCodeBackListener() {

            @Override
            public void createErCodeState(String errorDes, boolean isCreate, String path) {
                MyLog.cdl("====生成二维码===" + System.currentTimeMillis() + " / " + errorDes + " / " + isCreate + " / " + path);
                if (isCreate) {
                    GlideImageUtil.loadImageNoCache(context, imagePath, iv_qr_code_scan);
                } else {
                    MyToastView.getInstance().Toast(context, "创建二维码失败");
                }
            }
        });
        qrCodeUtil.createErCode(guardianCode, imagePath);
    }

    public void setOnDialogClickListener(SettingMenuClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rela_dialog_bgg:
                dissmiss();
                break;
            case R.id.btn_work_model:
                dissmiss();
                if (listener == null) {
                    return;
                }
                listener.clickWorkModel();
                break;
            case R.id.btn_exit:
                dissmiss();
                if (listener == null) {
                    return;
                }
                listener.exitApp();
                break;
        }
    }


    public interface SettingMenuClickListener {

        void clickWorkModel();


        void exitApp();
    }
}
