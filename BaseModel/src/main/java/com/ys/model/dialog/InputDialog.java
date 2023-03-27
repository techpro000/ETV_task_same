//package com.ys.model.dialog;
//
//import android.content.Context;
//import android.view.View;
//import android.widget.EditText;
//
//import com.ys.model.R;
//import com.ys.model.util.TextUtils;
//
//public class InputDialog extends BaseDialog implements View.OnClickListener {
//
//    private boolean btnClose = true;
//    private OnLeftListener leftListener;
//    private OnRightListener rightListener;
//
//    private final DialogInputBinding mView;
//
//    public InputDialog(Context context) {
//        super(context);
//        mView = getBindView();
//    }
//
//
//    @Override
//    protected int getLayoutRes() {
//        return R.layout.dialog_input;
//    }
//
//    @Override
//    protected void initView() {
//        mView.ivCancel.setOnClickListener(this);
//        mView.leftBtn.setOnClickListener(this);
//        mView.rightBtn.setOnClickListener(this);
//    }
//
//    @Override
//    public void onClick(View v) {
//        if (v.getId() == R.id.iv_cancel) {
//            dismiss();
//            return;
//        }
//        if (v.getId() == R.id.leftBtn) {
//            if (leftListener != null) {
//                leftListener.onLeft();
//            }
//        }
//        if (v.getId() == R.id.rightBtn) {
//            if (TextUtils.isEmpty(mView.etInput)) {
//                MyToastView.getInstance().Toast(v.getContext(), v.getContext().getString(R.string.tips_input));
//                return;
//            }
//            if (rightListener != null) {
//                rightListener.onRight(mView.etInput.getText().toString());
//            }
//        }
//        if (btnClose) {
//            dismiss();
//        }
//    }
//
//    public EditText getEdittext() {
//        return mView.etInput;
//    }
//
//    public void setShowTitle(boolean show) {
//        mView.rlTitle.setVisibility(show ? View.VISIBLE : View.GONE);
//    }
//
//    public void setShowLeftBtn(boolean show) {
//        mView.leftBtn.setVisibility(show ? View.VISIBLE : View.GONE);
//    }
//
//    public void setShowRightBtn(boolean show) {
//        mView.rightBtn.setVisibility(show ? View.VISIBLE : View.GONE);
//    }
//
//    public void setTitle(String title) {
//        mView.tvTitle.setText(title);
//    }
//
//    public void setLeftText(String text) {
//        mView.leftBtn.setText(text);
//    }
//
//    public void setRightText(String text) {
//        mView.rightBtn.setText(text);
//    }
//
//    public void setBtnClose(boolean close) {
//        this.btnClose = close;
//    }
//
//    public void setLeftListener(OnLeftListener leftListener) {
//        this.leftListener = leftListener;
//    }
//
//    public void setRightListener(OnRightListener rightListener) {
//        this.rightListener = rightListener;
//    }
//
//    public interface OnLeftListener{
//        void onLeft();
//    }
//
//    public interface OnRightListener{
//        void onRight(String text);
//    }
//}
