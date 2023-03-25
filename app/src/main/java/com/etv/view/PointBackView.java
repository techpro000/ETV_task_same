package com.etv.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.etv.util.MyLog;

public class PointBackView extends View {

    Paint paint = new Paint();
    Context context;

    public PointBackView(Context context) {
        super(context, null);
    }

    public PointBackView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public PointBackView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircleView(canvas);
        drawTextByCircle(canvas);
    }

    private void drawTextByCircle(Canvas canvas) {
        int leftCircle = (int) (currentX - (textWidth / 2));
        int rightCircle = leftCircle + textWidth;
        int topCircle = (int) (currentY - (textWidth / 2));
        int bottomCircle = topCircle + textWidth;

        Rect rect1 = new Rect(leftCircle, topCircle, rightCircle, bottomCircle);//画一个矩形
        paint.setColor(Color.WHITE);      //白色
        paint.setTextSize(15);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;
        int baseLineY = (int) (rect1.centerY() - top / 2 - bottom / 2);//基线中间点的y轴计算公式
        canvas.drawText("X", rect1.centerX(), baseLineY, paint);
    }

    int textWidth = 10;

    private void drawCircleView(Canvas canvas) {
        paint.setColor(Color.WHITE); //白色
        canvas.drawCircle(currentX, currentY, circle_fadius, paint);
        paint.setColor(Color.RED);  //红色
        canvas.drawCircle(currentX, currentY, circle_fadius - 4, paint);
        paint.setColor(Color.RED);  //中蓝色
        canvas.drawCircle(currentX, currentY, circle_fadius - 10, paint);
    }

    public float currentX = 30;
    public float currentY = 30;
    float lastX = currentX;
    float lastY = currentY;

    public float xDown = 0;
    public float yDown = 0;

    private int circle_fadius = 15;
    boolean isRightPosition = true;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        currentX = event.getX();
        currentY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDown = event.getX();
                yDown = event.getY();
                isRightPosition = jujlePositionIsRight(currentX, currentY);
                MyLog.cdl("=========是否合法的点击==" + isRightPosition);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPointPosition();
                break;
            case MotionEvent.ACTION_UP:
                MyLog.cdl("========执行点击事件====X=" + currentX + " / " + xDown);
                MyLog.cdl("========执行点击事件=====Y" + currentY + " / " + yDown);
                if (Math.abs(currentX - xDown) < 3 && Math.abs(currentY - yDown) < 3) {
                    MyLog.cdl("========执行点击事件=====000");
                    if (isRightPosition && listener != null) {
                        MyLog.cdl("=========不合法的的移动==");
                        listener.clickCloseView();
                    }
                }
                drawPointPosition();
                break;
        }
        return true;
    }

    private void drawPointPosition() {
        if (!isRightPosition) {
            MyLog.cdl("=========不合法的的移动==");
            return;
        }
        if (currentX < circle_fadius) {
            currentX = circle_fadius;
        }
        int width = getWidth();
        int height = getHeight();
        if (currentX > (width - circle_fadius)) {
            currentX = width - circle_fadius;
        }
        if (currentY > (height - circle_fadius)) {
            currentY = height - circle_fadius;
        }
        if (currentY < circle_fadius) {
            currentY = circle_fadius;
        }
        MyLog.cdl("=========移动的坐标==" + currentX + " / " + currentY);
        lastX = currentX;
        lastY = currentY;
        invalidate();
    }

    private boolean jujlePositionIsRight(float currentX, float currentY) {
        int disX = Math.abs((int) (currentX - lastX));
        if (disX > (circle_fadius * 2)) {
            return false;
        }
        int disY = Math.abs((int) (currentY - lastY));
        if (disY > (circle_fadius * 2)) {
            return false;
        }
        return true;
    }


    ViewClickListener listener;

    public void setViewClickListener(ViewClickListener listener) {
        this.listener = listener;
    }

    public interface ViewClickListener {
        void clickCloseView();
    }

}