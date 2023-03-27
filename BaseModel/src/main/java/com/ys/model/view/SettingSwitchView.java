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
import com.ys.model.listener.MoreButtonToggleListener;

public class SettingSwitchView extends RelativeLayout {

    public SettingSwitchView(Context context) {
        this(context, null);
    }

    public SettingSwitchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    View view;
    ImageView iv_image_show;
    TextView tv_title, tv_content;
    MyToggleButton toggle_switch;

    int imegLeft = R.drawable.time;
    String txt_title = "";
    String txt_content = "";
    Button mytoggle_click;

    public SettingSwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Setting_Switch);
        txt_title = a.getString(R.styleable.Setting_Switch_txt_title_switch);
        txt_content = a.getString(R.styleable.Setting_Switch_txt_content_switch);
        imegLeft = a.getResourceId(R.styleable.Setting_Switch_img_left_switch, R.drawable.time);
        view = LayoutInflater.from(context).inflate(R.layout.view_setting_switch, null);
        iv_image_show = (ImageView) view.findViewById(R.id.iv_image_show);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_content = (TextView) view.findViewById(R.id.tv_content);
        toggle_switch = (MyToggleButton) view.findViewById(R.id.toggle_switch);
        toggle_switch.setIsChoice(false);
        iv_image_show.setBackgroundResource(imegLeft);
        tv_title.setText(txt_title);
        tv_content.setText(txt_content);
        mytoggle_click = (Button) view.findViewById(R.id.mytoggle_click);
        initListener();
        addView(view);
        a.recycle();
    }

    private boolean isChooiceReceive = false;

    public void setSwitchStatues(boolean isChooice) {
        isChooiceReceive = isChooice;
        if (toggle_switch != null) {
            toggle_switch.setIsChoice(isChooice);
        }
    }

    public void setTxtContent(String getresolution) {
        if (tv_content != null) {
            tv_content.setText(getresolution);
        }
    }

    private void initListener() {
        toggle_switch.setOnToggleListener(new MyToggleButton.ToggleClickListener() {
            @Override
            public void click(View view, boolean isClick) {
                if (listener != null) {
                    listener.switchToggleView(SettingSwitchView.this, isClick);
                }
            }
        });
        mytoggle_click.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.switchToggleView(SettingSwitchView.this, !isChooiceReceive);
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

    MoreButtonToggleListener listener;

    public void setOnMoretListener(MoreButtonToggleListener listener) {
        this.listener = listener;
    }


}
