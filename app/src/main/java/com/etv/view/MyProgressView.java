package com.etv.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ys.etv.R;

public class MyProgressView extends View {

    int COLOR_BLUE = 0xff538ede;
    int COLOR_GREY = 0xffd8dbde;

    int width;
    int height;
    int progressheight = 4;
    int proCicrle = progressheight / 2;   //圆角半径
    int color_bg;
    int color_progress;
    int progress = 30;                    //当前的进度

    public void setProgress(int progress) {
        this.progress = progress;
        if (progress < 60 && progress > 0) {
            this.color_progress = 0xff169678;  //绿色
        } else if (progress > 30 && progress < 91) {
            this.color_progress = 0xfff18c14;   //橙色
        } else {
            this.color_progress = 0xffF0001D;  //红色
        }
        invalidate();
    }

    public MyProgressView(Context context) {
        this(context, null);
    }

    public MyProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.MyProgressView);
        color_progress = ta.getColor(R.styleable.MyProgressView_progresscolor, COLOR_BLUE);
        color_bg = ta.getColor(R.styleable.MyProgressView_progressbg, COLOR_GREY);
        ta.recycle();
        init();
    }

    //通用画笔
    Paint mPaint;

    private void init() {
        //初始化进度画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);  //抗锯齿
        mPaint.setStyle(Paint.Style.FILL);//充满
        mPaint.setDither(true);// 设置抖动,颜色过渡更均匀
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getWidth();
        height = getHeight();

        drawBg(canvas);  //绘制背景
        drawProgress(canvas);
    }


    private void drawProgress(Canvas canvas) {
        mPaint.setColor(color_progress);
        int top = height / 2 - progressheight / 2;
        int bottom = height / 2 + progressheight / 2;
        int right = width * progress / 100;
        Log.i("main", "right  ==" + right + "  /width =" + width);
        RectF oval3 = new RectF(0, top, right, bottom);// 设置个新的长方形
        canvas.drawRoundRect(oval3, proCicrle, proCicrle, mPaint);//第二个参数是x半径，第三个参数是y半径
//        canvas.drawRoundRect(oval3, 20, 15, mPaint);//第二个参数是x半径，第三个参数是y半径
    }

    private void drawBg(Canvas canvas) {
        mPaint.setColor(color_bg);
        int top = height / 2 - progressheight / 2;
        int bottom = height / 2 + progressheight / 2;
        RectF rectBg = new RectF(0, top, width, bottom);// 设置个新的长方形
        canvas.drawRoundRect(rectBg, proCicrle, proCicrle, mPaint);//第二个参数是x半径，第三个参数是y半径
    }
}