package com.ys.model.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ys.model.R;
import com.ys.model.listener.MoreButtonHiddleListener;
import com.ys.model.listener.MoreButtonListener;


public class SettingClickView extends RelativeLayout {

    public SettingClickView(Context context) {
        this(context, null);
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    View view;
    ImageView iv_image_show;
    TextView tv_title, tv_content;
    TextView tv_setting;

    int imegLeft = R.drawable.time;
    String txt_title = "";
    String txt_content = "";
    String btn_name = "";
    Button btn_click_View;

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Setting_Button);
        txt_title = a.getString(R.styleable.Setting_Button_txt_title);
        txt_content = a.getString(R.styleable.Setting_Button_txt_content);
        imegLeft = a.getResourceId(R.styleable.Setting_Button_img_left, R.drawable.time);
        btn_name = a.getString(R.styleable.Setting_Button_btn_txt_right);
        view = LayoutInflater.from(context).inflate(R.layout.view_setting_click, null);
        iv_image_show = (ImageView) view.findViewById(R.id.iv_image_show);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        btn_click_View = (Button) view.findViewById(R.id.btn_click_View);
        tv_content = (TextView) view.findViewById(R.id.tv_content);
        tv_setting = (TextView) view.findViewById(R.id.tv_setting);
        tv_setting.setText(btn_name);
        iv_image_show.setBackgroundResource(imegLeft);
        tv_title.setText(txt_title);
        tv_content.setText(txt_content);
        initListener();
        addView(view);
        a.recycle();
    }

    public void setTxtContent(String getresolution) {
        if (tv_content != null) {
            tv_content.setText(getresolution);
        }
    }

    public String getTextContent() {
        if (tv_content != null) {
            return tv_content.getText().toString();
        }
        return "";
    }

    private void initListener() {
        btn_click_View.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.clickView(SettingClickView.this);
                }
            }
        });

        tv_setting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.clickView(SettingClickView.this);
                }
            }
        });

        iv_image_show.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View iv_image_show) {
                if (hiddleListener != null) {
                    hiddleListener.clickView(iv_image_show);
                }
            }
        });
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            layout(l, t, r, b);
        }
    }

    MoreButtonListener listener;
    MoreButtonHiddleListener hiddleListener;

    public void setOnMoretListener(MoreButtonListener listener) {
        this.listener = listener;
    }

    public void setOnMoretHiddleListener(MoreButtonHiddleListener hiddleListener) {
        this.hiddleListener = hiddleListener;
    }

}
