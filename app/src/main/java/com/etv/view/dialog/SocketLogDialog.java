//package com.etv.view.dialog;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.widget.AdapterView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.RelativeLayout;
//
//import com.etv.listener.ObjectClickListener;
//import com.etv.util.SharedPerManager;
//import com.ys.etv.R;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
///***
// * 通用dialog,一句话，两个按钮
// */
//public class SocketLogDialog {
//    private Context context;
//    private Dialog dialog;
//    SocketClickListener dialogClick;
//    ListView lv_content;
//    List<SocketEntity> lists = new ArrayList<SocketEntity>();
//    SocketLineAdapter adapter;
//
//    public SocketLogDialog(Context context) {
//        this.context = context;
//        dialog = new Dialog(context, R.style.MyDialog);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        View dialog_view = View.inflate(context, R.layout.socket_line_log, null);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(SharedPerManager.getScreenWidth(), SharedPerManager.getScreenHeight());
//        dialog.setContentView(dialog_view, params);
//        dialog.setCancelable(true); // true点击屏幕以外关闭dialog
//        lv_content = (ListView) dialog_view.findViewById(R.id.lv_content);
//        adapter = new SocketLineAdapter(context, lists);
//        lv_content.setAdapter(adapter);
//
//        lv_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                dissmiss();
//                if (dialogClick == null) {
//                    return;
//                }
//                dialogClick.clickSocketEntity(lists.get(position));
//            }
//        });
//
//        adapter.setOnAdapterClickListener(new ObjectClickListener() {
//            @Override
//            public void clickSure(Object object) {
//                dissmiss();
//                if (dialogClick == null) {
//                    return;
//                }
//                dialogClick.delSocketEntity((SocketEntity) object);
//            }
//        });
//
//        RelativeLayout rela_bgg = (RelativeLayout) dialog_view.findViewById(R.id.rela_bgg);
//        rela_bgg.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dissmiss();
//            }
//        });
//    }
//
//    public void show(List<SocketEntity> listContent) {
//        try {
//            lists = listContent;
//            adapter.setListContent(lists);
//            dialog.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void dissmiss() {
//        try {
//            if (dialog != null && dialog.isShowing()) {
//                dialog.dismiss();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public void setOnDialogClickListener(SocketClickListener dc) {
//        dialogClick = dc;
//    }
//
//    public interface SocketClickListener {
//        void clickSocketEntity(SocketEntity socketEntity);
//
//        void delSocketEntity(SocketEntity socketEntity);
//    }
//
//
//}
