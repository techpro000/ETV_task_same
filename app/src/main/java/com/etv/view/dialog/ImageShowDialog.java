package com.etv.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.etv.util.image.glide.GlideImageUtil;
import com.ys.etv.R;

/***
 * 图片加载 dialog
 */
public class ImageShowDialog {

    private Context context;
    private Dialog dialog;
    ImageView iv_image_show;
    Button btn_cacel;

    public ImageShowDialog(Context context) {
        this.context = context;
        dialog = new Dialog(context, R.style.MyDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
        View dialog_view = View.inflate(context, R.layout.dialog_image_show, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(800, 800);
        dialog.setContentView(dialog_view, params);
        dialog.setCancelable(true);
        initDialog(dialog_view);
    }

    private void initDialog(View viewPop) {
        iv_image_show = (ImageView) viewPop.findViewById(R.id.iv_image_show);
        btn_cacel = (Button) viewPop.findViewById(R.id.btn_cacel);
        btn_cacel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dissmiss();
            }
        });
    }

    public void showDialog(String imagePath) {
        try {
            dissmiss();
            GlideImageUtil.loadImageByPath(context, imagePath, iv_image_show);
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
}
