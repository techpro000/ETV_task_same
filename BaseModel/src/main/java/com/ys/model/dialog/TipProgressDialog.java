//package com.ys.model.dialog;
//
//import android.content.Context;
//import android.view.View;
//
//import com.ys.model.R;
//
//public class TipProgressDialog extends BaseDialog implements View.OnClickListener {
//
//    private OnLeftListener leftListener;
//    private OnRightListener rightListener;
//
//    private final DialogTipProgressBinding mView;
//
//    public TipProgressDialog(Context context) {
//        super(context);
//        mView = getBindView();
//    }
//
//
//    @Override
//    protected int getLayoutRes() {
//        return R.layout.dialog_tip_progress;
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
//        if (v.getId() == R.id.leftBtn) {
//            if (leftListener != null) {
//                leftListener.onLeft();
//            }
//        }
//        if (v.getId() == R.id.rightBtn) {
//            if (rightListener != null) {
//                rightListener.onRight();
//            }
//        }
//        dismiss();
//    }
//
//    public void setProgress(int progress) {
//        mView.pbBar.setProgress(progress);
//    }
//
//    public void setTitle(String title) {
//        mView.tvTitle.setText(title);
//    }
//
//    public void setContent(String content) {
//        mView.tvContent.setText(content);
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
//        void onRight();
//    }
//}
