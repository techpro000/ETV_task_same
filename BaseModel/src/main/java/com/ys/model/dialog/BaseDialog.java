package com.ys.model.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.ys.model.R;

public abstract class BaseDialog implements IDialog {

    private final Dialog mDialog;
    private WindowParams windowParams;
    private final ViewDataBinding mBinding;

    public BaseDialog(Context context) {
        this(context, R.style.panel_dialog);
    }

    public BaseDialog(Context context, int themeResId) {
        mDialog = new Dialog(context, themeResId) {
            @Override
            protected void onStart() {
                super.onStart();
                Window window = getWindow();
                WindowManager.LayoutParams lp = window.getAttributes();
                if (windowParams != null) {
                    windowParams.pasteParams(lp);
                    window.setAttributes(lp);
                } else {
                    windowParams = new WindowParams();
                }
                windowParams.copyParams(lp);
            }

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(mBinding.getRoot());
                initView();
            }
        };

        LayoutInflater inflater = LayoutInflater.from(context);
        mBinding = DataBindingUtil.inflate(inflater, getLayoutRes(), null, false);
    }

    protected abstract int getLayoutRes();

    protected abstract void initView();

    @Override
    public void show() {
        mDialog.show();
    }

    @Override
    public void dismiss() {
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    public void setCancelable(boolean flag) {
        mDialog.setCancelable(flag);
    }

    public Dialog getDialog() {
        return mDialog;
    }

    public Window getWindow() {
        return mDialog.getWindow();
    }

    public Context getContext(){
        return mDialog.getContext();
    }

    public void setWindowParams(WindowParams params) {
        windowParams = params;
    }

    public void setOnKeyListener(DialogInterface.OnKeyListener listener) {
        mDialog.setOnKeyListener(listener);
    }

    public WindowParams getWindowParams() {
        if (windowParams == null) {
            windowParams = new WindowParams();
        }
        return windowParams;
    }

    @SuppressWarnings("unchecked")
    protected  <T extends ViewDataBinding> T getBindView() {
        return (T) mBinding;
    }
}
