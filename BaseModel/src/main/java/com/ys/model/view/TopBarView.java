package com.ys.model.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ys.model.R;

/***
 * 手机端topBar统一类型
 *
 * @author Administrator
 */
public class TopBarView extends RelativeLayout {

    public TopBarView(Context context) {
        this(context, null);
    }

    public TopBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    View view;
    RelativeLayout rela_bgg;
    Button btn_back, btn_menu;
    TextView tv_title;
    String titleText = "标题";
    int backgroundColor = 0xff33b5e5;
    boolean isLeftShow = true;
    boolean isRightShow = true;
    int rightImage;
    int leftImage;

    public TopBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TopBarView);
        backgroundColor = a.getColor(R.styleable.TopBarView_backColor, 0xff0381F4);
        titleText = a.getString(R.styleable.TopBarView_title);
        isLeftShow = a.getBoolean(R.styleable.TopBarView_leftShow, true);
        isRightShow = a.getBoolean(R.styleable.TopBarView_rightShow, true);
        rightImage = a.getResourceId(R.styleable.TopBarView_rightImage, R.mipmap.ic_launcher);
        leftImage = a.getResourceId(R.styleable.TopBarView_leftImage, R.mipmap.ic_launcher);
        view = LayoutInflater.from(context).inflate(R.layout.view_topbar, null);
        rela_bgg = (RelativeLayout) view.findViewById(R.id.rela_bgg);
        rela_bgg.setBackgroundColor(backgroundColor);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText(titleText);

        btn_back = (Button) view.findViewById(R.id.btn_topBar_back);
        btn_back.setBackgroundResource(leftImage);
        if (!isLeftShow) {
            btn_back.setVisibility(View.GONE);
        }
        btn_menu = (Button) view.findViewById(R.id.btn_topBar_menu);
        btn_menu.setBackgroundResource(rightImage);
        if (!isRightShow) {
            btn_menu.setVisibility(View.GONE);
        }
        btn_back.setOnClickListener(myClick);
        btn_menu.setOnClickListener(myClick);
        addView(view);
        a.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            layout(l, t, r, b);
        }
    }

    /***
     * 设置topBar背景色
     *
     * @param color
     */
    public void setTitleBack(int color) {
        rela_bgg.setBackgroundColor(color);
    }

    public void setLeftImage(int drawable) {
        btn_back.setBackgroundResource(drawable);
    }

    /***
     * 左边按钮显示隐藏
     *
     * @param isShow false隐藏
     */
    public void setLeftShow(boolean isShow) {
        if (!isShow) {
            btn_back.setVisibility(View.INVISIBLE);
        }
    }

    public void setRightImage(int drawable) {
        btn_menu.setBackgroundResource(drawable);
    }

    public void setRightText(String text) {
        btn_menu.setText(text);
        btn_menu.setBackgroundResource(R.color.white_base);
    }

    /***
     * 右边按钮显示隐藏
     *
     * @param isShow false隐藏
     */
    public void setRightShow(boolean isShow) {
        if (!isShow) {
            btn_menu.setVisibility(View.INVISIBLE);
        }
    }

    /***
     * 获取标题内容
     * @return
     */
    public String getTitleText() {
        return tv_title.getText().toString().trim();
    }

    public void setTitleText(String text) {
        tv_title.setText(text);
    }

    OnClickListener myClick = new OnClickListener() {

        public void onClick(View v) {
            if (v.getId() == R.id.btn_topBar_back) {
                if (listener != null) {
                    listener.leftClick();
                }
            } else if (v.getId() == R.id.btn_topBar_menu) {
                if (listener != null) {
                    listener.rightClick();
                }
            }
        }
    };

    TopBarListener listener;

    public void setTopBarListener(TopBarListener listener) {
        this.listener = listener;
    }


    public interface TopBarListener {
        void leftClick();

        void rightClick();
    }

}
