package com.ys.model.decoration;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.ys.model.util.UnitUtils;

public class LineDivider extends RecyclerView.ItemDecoration {
    private Paint mPaint;
    private Drawable mDivider;
    private int mPaddingLeft = 0;
    private int mPaddingRight = 0;
    private int mDividerHeight;
    private int mOrientation;
    private boolean isDrawLastItemDivider;

    /**
     * 默认分割线：高度为2px，颜色为灰色
     *
     * @param orientation 列表方向
     */
    public LineDivider(int orientation) {
        this(orientation, 0xff7a7a7a);
    }

    /**
     *
     * @param orientation 列表方向
     * @param drawable  分割线图片
     */
    public LineDivider(int orientation, Drawable drawable) {
        if (orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL) {
            throw new IllegalArgumentException("请输入正确的参数！");
        }
        mDivider = drawable;
        this.mOrientation = orientation;
        mDividerHeight = mDivider.getIntrinsicHeight();
    }

    public LineDivider(int orientation, int dividerColor) {
        this(orientation, UnitUtils.dp2px(1), dividerColor);
    }

    /**
     *
     * @param orientation   列表方向
     * @param dividerHeight 分割线高度
     * @param dividerColor  分割线颜色
     */
    public LineDivider(int orientation, int dividerHeight, int dividerColor) {
        if (orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL) {
            throw new IllegalArgumentException("请输入正确的参数！");
        }
        this.mOrientation = orientation;
        mDividerHeight = dividerHeight;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        mPaint.setStyle(Paint.Style.FILL);
    }

    //获取分割线尺寸
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //super.getItemOffsets(outRect, view, parent, state);
        outRect.set(0, 0, 0, mDividerHeight);
    }

    //绘制分割线
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    //绘制横向 item 分割线
    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        int left = parent.getPaddingLeft() + mPaddingLeft;
        int right = parent.getMeasuredWidth() - parent.getPaddingRight() - mPaddingRight;

        //默认最后一个不画分割线
        int childSize = parent.getChildCount() -1;
        if (isDrawLastItemDivider) {
            childSize++;
        }

        for (int i = 0; i < childSize; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + layoutParams.bottomMargin;
            int bottom = top + mDividerHeight;
            if (mDivider != null) {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
            if (mPaint != null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }

    }

    //绘制纵向 item 分割线
    private void drawVertical(Canvas canvas, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom();
        final int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + layoutParams.rightMargin;
            final int right = left + mDividerHeight;
            if (mDivider != null) {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
            if (mPaint != null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }

    }

    public void setDividerColor(int color) {
        mDivider = null;
        mPaint.setColor(color);
    }

    public void setDividerHeight(int height) {
        this.mDividerHeight = height;
    }

    public void setPaddingLeft(int padding) {
        this.mPaddingLeft = padding;
    }

    public void setPaddingRight(int padding) {
        this.mPaddingRight = padding;
    }

    public void setDrawLastItemDivider(boolean drawLastItemDivider) {
        isDrawLastItemDivider = drawLastItemDivider;
    }
}
