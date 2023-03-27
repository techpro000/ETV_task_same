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


/***
 * 手机端topBar统一类型
 *
 * @author Administrator
 */
public class MoreButtonToggle extends RelativeLayout {

    private final TextView tv_content;
    private final ImageView iv_device_line;
    private View view_top;

    public MoreButtonToggle(Context context) {
        this(context, null);
    }

    public MoreButtonToggle(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    View view;
    String title_content = "";
    int imageId = R.mipmap.radio_chooice;
    boolean showright;
    View view_bottom;
    boolean showbottom;
    boolean showtop;
    MyToggleButton toggle_switch;
    Button mytoggle_click;
    int defaultTextColor = 0xff000000;

    public MoreButtonToggle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.More_Button);
        title_content = a.getString(R.styleable.More_Button_title_content);
        imageId = a.getResourceId(R.styleable.More_Button_image_left, R.mipmap.ic_launcher);
        showright = a.getBoolean(R.styleable.More_Button_showright, true);
        defaultTextColor = a.getInt(R.styleable.More_Button_text_font_color, 0xff000000);
        showbottom = a.getBoolean(R.styleable.More_Button_showbottom, true);
        showtop = a.getBoolean(R.styleable.More_Button_showtop, true);
        view = LayoutInflater.from(context).inflate(R.layout.more_button_toggle, null);
        //左边边的图标
        iv_device_line = (ImageView) view.findViewById(R.id.iv_device_line);
        iv_device_line.setBackgroundResource(imageId);
        tv_content = (TextView) view.findViewById(R.id.tv_content);
        tv_content.setText(title_content);
        tv_content.setTextColor(defaultTextColor);

        view_top = view.findViewById(R.id.view_top);
        view_bottom = view.findViewById(R.id.view_bottom);

        view_top.setVisibility(showtop ? View.VISIBLE : View.INVISIBLE);
        view_bottom.setVisibility(showbottom ? View.VISIBLE : View.INVISIBLE);

        toggle_switch = (MyToggleButton) view.findViewById(R.id.toggle_switch);
        toggle_switch.setIsChoice(false);
        toggle_switch.setOnToggleListener(new MyToggleButton.ToggleClickListener() {
            @Override
            public void click(View view, boolean isClick) {
                if (listener != null) {
                    listener.switchToggleView(MoreButtonToggle.this, isClick);
                }
            }
        });
        mytoggle_click = (Button) view.findViewById(R.id.mytoggle_click);
        mytoggle_click.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listener != null) {
                    listener.switchToggleView(MoreButtonToggle.this, !reciveChekcStatues);
                }
            }
        });
        addView(view);
        a.recycle();
    }

    boolean reciveChekcStatues = false;

    public void setChcekcAble(boolean isCheck) {
        reciveChekcStatues = isCheck;
        toggle_switch.setIsChoice(isCheck);
    }

    public void setText(String text) {
        tv_content.setText(text);
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
