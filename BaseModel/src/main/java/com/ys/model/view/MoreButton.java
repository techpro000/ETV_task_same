package com.ys.model.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ys.model.R;
import com.ys.model.listener.MoreButtonListener;


/***
 * 手机端topBar统一类型
 *
 * @author Administrator
 */
public class MoreButton extends RelativeLayout {

    private final TextView tv_content;
    private final ImageView iv_device_line;
    private ImageView iv_right;
    private View view_top;
    private TextView tv_right_content;

    public MoreButton(Context context) {
        this(context, null);
    }

    public MoreButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    View view;
    String title_content = "";
    int imageId = R.mipmap.ic_launcher;
    boolean showright;
    Button mybutton_click;
    View view_bottom;
    boolean showbottom;
    boolean showtop;
    private String right_content;
    int defaultTextColor = 0xff000000;


    public MoreButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.More_Button);
        title_content = a.getString(R.styleable.More_Button_title_content);
        imageId = a.getResourceId(R.styleable.More_Button_image_left, R.mipmap.ic_launcher);
        defaultTextColor = a.getInt(R.styleable.More_Button_text_font_color, 0xff000000);
        showright = a.getBoolean(R.styleable.More_Button_showright, true);
        showbottom = a.getBoolean(R.styleable.More_Button_showbottom, true);
        showtop = a.getBoolean(R.styleable.More_Button_showtop, true);
        right_content = a.getString(R.styleable.More_Button_right_content);
        view = LayoutInflater.from(context).inflate(R.layout.more_button, null);
        //左边边的图标
        iv_device_line = (ImageView) view.findViewById(R.id.iv_device_line);
        iv_device_line.setBackgroundResource(imageId);

        tv_right_content = (TextView) view.findViewById(R.id.tv_right);
        tv_right_content.setText(right_content);

        tv_content = (TextView) view.findViewById(R.id.tv_content);
        tv_content.setText(title_content);

        view_top = view.findViewById(R.id.view_top);
        view_bottom = view.findViewById(R.id.view_bottom);

        view_top.setVisibility(showtop ? View.VISIBLE : View.INVISIBLE);
        view_bottom.setVisibility(showbottom ? View.VISIBLE : View.INVISIBLE);

        iv_right = (ImageView) view.findViewById(R.id.iv_right);
        iv_right.setVisibility(showright ? View.VISIBLE : View.INVISIBLE);

        tv_content.setTextColor(defaultTextColor);
        tv_right_content.setTextColor(defaultTextColor);

        mybutton_click = (Button) view.findViewById(R.id.mybutton_click);
        mybutton_click.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("main", "------用户点击了按键");
                if (listener == null) {
                    return;
                }
                listener.clickView(MoreButton.this);
            }
        });
        if (right_content != null && !right_content.isEmpty()) {
            tv_right_content.setVisibility(View.VISIBLE);
            tv_right_content.setText(right_content);
        }

        addView(view);
        a.recycle();
    }

    public void setRigt(String content) {
        tv_right_content.setVisibility(VISIBLE);
        tv_right_content.setText(content);
    }

    public void setRightImg(boolean isShow) {
        if (isShow) {
            iv_right.setVisibility(VISIBLE);
        } else {
            iv_right.setVisibility(GONE);
        }
    }

    public void setTextTitle(String text) {
        tv_content.setText(text);
    }

    public void setRightImage(int imageId) {
        iv_right.setBackgroundResource(imageId);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            layout(l, t, r, b);
        }
    }

    MoreButtonListener listener;

    public void setOnMoretListener(MoreButtonListener listener) {
        this.listener = listener;
    }


}
