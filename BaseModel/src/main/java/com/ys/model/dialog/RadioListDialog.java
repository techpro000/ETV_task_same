package com.ys.model.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ys.model.R;
import com.ys.model.adapter.RadioItemAdapter;
import com.ys.model.config.DialogConfig;
import com.ys.model.entity.RedioEntity;
import com.ys.model.listener.AdapterItemClickListener;
import com.ys.model.listener.RadioChooiceListener;

import java.util.ArrayList;
import java.util.List;

public class RadioListDialog implements AdapterItemClickListener {

    private Context context;
    private Dialog dialog;
    private Button btn_sure, btn_dialog_cacel;
    private TextView dialog_title;
    private ListView lv_content_view;
    private RadioItemAdapter radioItemAdapter;
    private List<RedioEntity> listRadio = null;

    public RadioListDialog(Context context) {
        this.context = context;
        dialog = new Dialog(context, R.style.MyDialog_Base);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialog_view = View.inflate(context, R.layout.radio_list_dialog, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DialogConfig.SCREEN_WIDTH, DialogConfig.SCREEN_HEIGHT);
        dialog.setContentView(dialog_view, params);
        dialog.setCancelable(true); // true点击屏幕以外关闭dialog
        btn_sure = (Button) dialog_view.findViewById(R.id.btn_dialog_yes);
        btn_dialog_cacel = (Button) dialog_view.findViewById(R.id.btn_dialog_cacel);
        dialog_title = (TextView) dialog_view.findViewById(R.id.dialog_title);
        lv_content_view = (ListView) dialog_view.findViewById(R.id.lv_content_view);
        listRadio = new ArrayList<RedioEntity>();
        radioItemAdapter = new RadioItemAdapter(context, listRadio);
        radioItemAdapter.setAdapterItemClickListener(this);
        lv_content_view.setAdapter(radioItemAdapter);
        btn_sure.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dissmiss();
                if (radioChooiceListener == null) {
                    return;
                }
                radioChooiceListener.backChooiceInfo(redioEntityCache, chooicePositionCache);
            }
        });
        btn_dialog_cacel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dissmiss();
            }
        });

        lv_content_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                chooicePositionCache = position;
                radioItemAdapter.setRadioList(listRadio, position);
            }
        });
    }

    RedioEntity redioEntityCache;
    int chooicePositionCache = 0;

    public void show(String title, List<RedioEntity> lists, int checkPosition) {
        try {
            chooicePositionCache = checkPosition;
            if (lists != null && lists.size() > 1) {
                redioEntityCache = lists.get(checkPosition);
            }
            dialog_title.setText(title);
            this.listRadio = lists;
            radioItemAdapter.setRadioList(listRadio, checkPosition);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void adapterItemClick(Object object, int position) {
        radioItemAdapter.setRadioList(listRadio, position);
        redioEntityCache = listRadio.get(position);
        chooicePositionCache = position;
    }


    public void dissmiss() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    RadioChooiceListener radioChooiceListener;

    public void setRadioChooiceListener(RadioChooiceListener radioChooiceListener) {
        this.radioChooiceListener = radioChooiceListener;
    }


}
