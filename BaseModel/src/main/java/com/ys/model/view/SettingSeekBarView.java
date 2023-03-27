package com.ys.model.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ys.model.R;
import com.ys.model.listener.MoreButtonSeekBarListener;
import com.ys.model.listener.MoreButtonToggleListener;


public class SettingSeekBarView extends RelativeLayout {

    public SettingSeekBarView(Context context) {
        this(context, null);
    }

    public SettingSeekBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    View view;
    ImageView iv_image_show;
    TextView tv_title, tv_content;


    int imegLeft = R.drawable.time;
    String txt_title = "";
    String txt_content = "";
    SeekBar seekbar_view;

    public SettingSeekBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Setting_Seekbar);
        txt_title = a.getString(R.styleable.Setting_Seekbar_txt_title_seekbar);
        txt_content = a.getString(R.styleable.Setting_Seekbar_txt_content_seekbar);
        imegLeft = a.getResourceId(R.styleable.Setting_Seekbar_img_left_seekbar, R.drawable.time);
        view = LayoutInflater.from(context).inflate(R.layout.view_setting_seekbar, null);
        iv_image_show = (ImageView) view.findViewById(R.id.iv_image_show);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_content = (TextView) view.findViewById(R.id.tv_content);
        seekbar_view = (SeekBar) view.findViewById(R.id.seekbar_view);
        iv_image_show.setBackgroundResource(imegLeft);
        tv_title.setText(txt_title);
        tv_content.setText(txt_content);
        initListener();
        addView(view);
        a.recycle();
    }

    public void setProgressNum(int progressNum) {
        if (tv_content != null) {
            tv_content.setText(progressNum + " %");
        }
        if (seekbar_view != null) {
            seekbar_view.setProgress(progressNum);
        }
    }

    private void initListener() {
        seekbar_view.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (listener != null) {
                    listener.switchToggleView(SettingSeekBarView.this, progress);
                }
                setProgressNum(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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

    MoreButtonSeekBarListener listener;

    public void setOnMoretListener(MoreButtonSeekBarListener listener) {
        this.listener = listener;
    }


}
