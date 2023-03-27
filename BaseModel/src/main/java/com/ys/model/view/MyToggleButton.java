package com.ys.model.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by dinglong
 */
public class MyToggleButton extends View implements View.OnClickListener {

    private Paint mPaint;

    public MyToggleButton(Context context) {
        this(context, null);
    }

    public MyToggleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        this.setOnClickListener(this);
    }

    private void initView() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setDither(true);// 设置抖动,颜色过渡更均匀
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawView(canvas);
        drawViewNei(canvas);
        drawCircle(canvas);
    }

    int viewHeight = 40;
    int viewWidth = viewHeight * 2;
    int radius = viewHeight / 2;
    int distance_circle = 2;
    int circleRadius = radius;
    boolean isChoice = true;

    public void setIsChoice(boolean isChoice) {
        this.isChoice = isChoice;
        invalidate();
    }

    private void drawCircle(Canvas canvas) {
        int left = getWidth() / 2 - (viewWidth / 2) + circleRadius + distance_circle;
        int right = getWidth() / 2 + (viewWidth / 2) - circleRadius - distance_circle;
        mPaint.setColor(Color.WHITE);
        if (isChoice) {
            canvas.drawCircle(right, getHeight() / 2, (circleRadius - (distance_circle * 2)), mPaint);// 小圆
        } else {
            canvas.drawCircle(left, getHeight() / 2, (circleRadius - (distance_circle * 2)), mPaint);// 小圆
        }
    }

    private void drawViewNei(Canvas canvas) {
        int left = getWidth() / 2 - (viewWidth / 2) + distance_circle;
        int right = getWidth() / 2 + (viewWidth / 2) - distance_circle;
        int top = getHeight() / 2 - (viewHeight / 2) + distance_circle;
        int bottom = getHeight() / 2 + (viewHeight / 2) - distance_circle;
        RectF oval3 = new RectF(left, top, right, bottom);// 设置个新的长方形
        if (isChoice) {
            mPaint.setColor(COOR_GREEN);
        } else {
            mPaint.setColor(Color.GRAY);
        }
        canvas.drawRoundRect(oval3, radius, radius, mPaint);//第二个参数是x半径，第三个参数是y半径
    }

    int COOR_GREEN = 0xff2bb9c6;

    private void drawView(Canvas canvas) {
        int left = getWidth() / 2 - viewWidth / 2;
        int right = getWidth() / 2 + viewWidth / 2;
        int top = getHeight() / 2 - viewHeight / 2;
        int bottom = getHeight() / 2 + viewHeight / 2;
        RectF oval3 = new RectF(left, top, right, bottom);// 设置个新的长方形
        mPaint.setColor(Color.GRAY);
        canvas.drawRoundRect(oval3, radius, radius, mPaint);//第二个参数是x半径，第三个参数是y半径
    }

    @Override
    public void onClick(View v) {
        if (isChoice) {
            isChoice = false;
        } else {
            isChoice = true;
        }
        listener.click(MyToggleButton.this, isChoice);
        invalidate();
    }

    ToggleClickListener listener;

    public void setOnToggleListener(ToggleClickListener listener) {
        this.listener = listener;
    }

    public interface ToggleClickListener {
        void click(View view, boolean isClick);
    }

}