package com.etv.adapter.recycle;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;


public class BaseViewHolder extends RecyclerView.ViewHolder {

    private final SparseArray<View> views = new SparseArray<>();

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            if (view == null) {
                throw new IllegalStateException("No view found with id " + viewId);
            }
            views.put(viewId, view);
        }
        return (T) view;
    }

    public void setText(int viewId, CharSequence value) {
        this.<TextView>getView(viewId).setText(value);
    }

    public void setText(int viewId, @StringRes int strId) {
        this.<TextView>getView(viewId).setText(strId);
    }

    public void setTextColor(int viewId, @ColorInt int color) {
        this.<TextView>getView(viewId).setTextColor(color);
    }

    public void setTextColorRes(int viewId, @ColorRes int colorRes) {
        this.<TextView>getView(viewId).setTextColor(itemView.getResources().getColor(colorRes));
    }

    public void setImageResource(int viewId, @DrawableRes int drawableRes) {
        this.<ImageView>getView(viewId).setImageResource(drawableRes);
    }

    public void setImageDrawable(int viewId, Drawable drawable) {
        this.<ImageView>getView(viewId).setImageDrawable(drawable);
    }

    public void setImageBitmap(int viewId, Bitmap bitmap) {
        this.<ImageView>getView(viewId).setImageBitmap(bitmap);
    }

    public void setBackgroundColor(int viewId, @ColorInt int color) {
        this.getView(viewId).setBackgroundColor(color);
    }

    public void setBackgroundResource(int viewId, @DrawableRes int bgRes) {
        this.getView(viewId).setBackgroundResource(bgRes);
    }

    public void setBackgroundColor(@ColorInt int color) {
        this.itemView.setBackgroundColor(color);
    }

    public void setBackgroundResource(@DrawableRes int bgRes) {
        this.itemView.setBackgroundResource(bgRes);
    }

    public void setChecked(int viewId, boolean checked) {
        this.<CompoundButton>getView(viewId).setChecked(checked);
    }

    public void setVisible(int viewId, boolean visible) {
        this.getView(viewId).setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    public void setGone(int viewId, boolean gone) {
        this.getView(viewId).setVisibility(gone ? View.GONE : View.VISIBLE);
    }

    public void setEnabled(int viewId, boolean enable) {
        this.getView(viewId).setEnabled(enable);
    }
}
