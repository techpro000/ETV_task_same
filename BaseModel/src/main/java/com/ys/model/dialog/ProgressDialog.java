//package com.ys.model.dialog;
//
//import android.content.Context;
//
//import com.ys.model.R;
//import com.ys.model.databinding.YsDialogProgressBinding;
//
//public class ProgressDialog extends BaseDialog{
//
//    private final YsDialogProgressBinding mView;
//
//    public ProgressDialog(Context context) {
//        super(context);
//        mView = getBindView();
//    }
//
//    @Override
//    protected int getLayoutRes() {
//        return R.layout.ys_dialog_progress;
//    }
//
//    @Override
//    protected void initView() {
//
//    }
//
//    public void setProgress(int progress) {
//        mView.pbBar.setProgress(progress);
//    }
//
//    public void setText(String text) {
//        mView.tvContent.setText(text);
//    }
//
//    public void show(String text) {
//        mView.tvContent.setText(text);
//        show();
//    }
//
//}
