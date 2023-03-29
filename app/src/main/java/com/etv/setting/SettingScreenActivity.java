package com.etv.setting;

import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.recyclerview.widget.GridLayoutManager;

import com.etv.adapter.recycle.BaseViewHolder;
import com.etv.adapter.recycle.QuickAdapter;
import com.etv.config.AppInfo;
import com.etv.http.SameScreenHelper;
import com.etv.listener.TextInputListener;
import com.etv.setting.entity.RowCow;
import com.etv.util.MyLog;
import com.etv.util.SharedPerManager;
import com.etv.util.SharedPerUtil;
import com.ys.etv.R;
import com.ys.etv.databinding.ActivityScreenLinkSettingBinding;
import com.ys.model.dialog.RadioListDialog;
import com.ys.model.entity.RedioEntity;
import com.ys.model.listener.MoreButtonListener;
import com.ys.model.listener.RadioChooiceListener;

import java.util.ArrayList;
import java.util.List;

/***
 * 连屏设置界面
 */
public class SettingScreenActivity extends SettingBaseActivity {

    private static final String TAG = "SettingSysActivity";

    ActivityScreenLinkSettingBinding mBinding;
    private Runnable hiddenRunnable;

    private int viewWidth;
    private RowCow selRowCow;
    private QuickAdapter<RowCow> adapter;

    private View mainToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityScreenLinkSettingBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initView();
        initListener();

        mainToggle = mBinding.switchTaskMain.findViewById(R.id.toggle_switch);
        if (SharedPerManager.getAutoRowCow()) {
            getRowCowByNetwork();
            mainToggle.setEnabled(false);
        }

    }

    private void initView() {
        mBinding.switchWay.setSwitchStatues(SharedPerManager.getAutoRowCow());
        mBinding.switchTaskMain.setSwitchStatues(SharedPerUtil.getSameTaskMainLeader());
        mBinding.switchWay.setOnMoretListener((view, check) -> {
            SharedPerManager.setAutoRowCow(check);
            mainToggle.setEnabled(!check);
            if (check) {
                getRowCowByNetwork();
            }
        });
        mBinding.switchTaskMain.setOnMoretListener((view, check) -> {
            SharedPerManager.setTaskSameMain(check ? 0 : 1);
        });

        adapter = new QuickAdapter<RowCow>(R.layout.item_row_cow) {
            @Override
            protected void convert(BaseViewHolder holder, RowCow item) {
                holder.setText(R.id.tv_text, item.getPositionStr());
                if (item.check) {
                    selRowCow = item;
                    holder.setTextColorRes(R.id.tv_text, R.color.white);
                    holder.setBackgroundResource(R.id.cl_root, R.drawable.shape_select_row_cow);
                } else {
                    holder.setTextColorRes(R.id.tv_text, R.color.black);
                    holder.setBackgroundResource(R.id.cl_root, R.drawable.shape_row_cow);
                }
            }
        };
        adapter.setOnItemClickListener((baseAdapter, view, position) -> {
            if (SharedPerManager.getAutoRowCow()) {
                showToastView("当前为自动获取模式");
                return;
            }
            selRowCow = adapter.getItem(position);
            for (RowCow rc : adapter.getData()) {
                rc.check = false;
            }
            selRowCow.check = true;
            adapter.notifyDataChanged();
        });
        mBinding.recycleView.setAdapter(adapter);
        selRowCow = SharedPerManager.getShowScreenRowCow();
        mBinding.recycleView.setLayoutManager(new GridLayoutManager(this, selRowCow.cow));
        adapter.setNewData(getRowCowList(selRowCow.row, selRowCow.cow, selRowCow.position));

        mBinding.etRow.setText(selRowCow.row + "");
        mBinding.etCow.setText(selRowCow.cow + "");
    }

    private void initListener() {
        mBinding.btnTtysChooice.setOnMoretListener(new MoreButtonListener() {
            @Override
            public void clickView(View view) {
                //选择串口
                showTtysTypeDialog();
            }
        });

        mBinding.btnMessageStyle.setOnMoretListener(new MoreButtonListener() {
            @Override
            public void clickView(View view) {
                showMessageTypeDialog();
            }
        });

        mBinding.etCow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showModifyRowCowDialog(getString(R.string.cow_nums), selRowCow.cow, (Button) v);
            }
        });
        mBinding.etRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showModifyRowCowDialog(getString(R.string.row_nums), selRowCow.row, (Button) v);
            }
        });

        mBinding.linExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMainActivity();
            }
        });

        mBinding.btSave.setOnClickListener(v -> {
            if (selRowCow == null) {
                showToastView("请为当前设备选择一个播放区域");
                return;
            }
            SharedPerManager.setShowScreenRowCow(selRowCow.row, selRowCow.cow, selRowCow.position);
            showToastView("保存成功");
        });

        TextInputListener inputListener = new TextInputListener() {
            @Override
            public void afterTextChanged(Editable s) {
                String row = mBinding.etRow.getText().toString();
                String cow = mBinding.etCow.getText().toString();
                if (row.length() < 1) {
                    showHiddenListView(false);
                    return;
                }
                if (cow.length() < 1) {
                    showHiddenListView(false);
                    return;
                }
                int intRow = Integer.parseInt(row);
                int intCow = Integer.parseInt(cow);
                parseRowCowInput(intRow, intCow, 0);
            }
        };
        mBinding.etRow.addTextChangedListener(inputListener);
        mBinding.etCow.addTextChangedListener(inputListener);
    }

    private void showTtysTypeDialog() {
        RadioListDialog radioListDialog = new RadioListDialog(SettingScreenActivity.this);
        List<RedioEntity> listShow = new ArrayList<RedioEntity>();
        listShow.add(new RedioEntity("ttyS0"));
        listShow.add(new RedioEntity("ttyS1"));
        listShow.add(new RedioEntity("ttyS2"));
        listShow.add(new RedioEntity("ttyS3"));
        listShow.add(new RedioEntity("ttyS4"));
        listShow.add(new RedioEntity("ttyS5"));
        int ttysPosition = SharedPerManager.getTTysPosition();
        radioListDialog.show(getString(R.string.capture_update_height), listShow, ttysPosition);
        radioListDialog.setRadioChooiceListener(new RadioChooiceListener() {
            @Override
            public void backChooiceInfo(RedioEntity redioEntity, int chooicePosition) {
                MyLog.cdl("===chooicePosition=" + chooicePosition);
                SharedPerManager.setTTysPosition(chooicePosition);
                showToastView(getString(R.string.set_success_reboot_start));
                updateMainView();
            }
        });
    }

    private void showMessageTypeDialog() {
        RadioListDialog radioListDialog = new RadioListDialog(SettingScreenActivity.this);
        List<RedioEntity> listShow = new ArrayList<RedioEntity>();
        listShow.add(new RedioEntity(getString(R.string.type_newwork)));
        listShow.add(new RedioEntity(getString(R.string.type_serialport)));
        int currentType = SharedPerManager.getMessageType();
        radioListDialog.show(getString(R.string.capture_update_height), listShow, currentType);
        radioListDialog.setRadioChooiceListener(new RadioChooiceListener() {
            @Override
            public void backChooiceInfo(RedioEntity redioEntity, int chooicePosition) {
                MyLog.cdl("===chooicePosition=" + chooicePosition);
                SharedPerManager.setMessageType(chooicePosition);
                showToastView(getString(R.string.set_success_reboot_start));
                updateMainView();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMainView();
    }

    private void updateMainView() {
        int currentType = SharedPerManager.getMessageType();
        switch (currentType) {
            case AppInfo.MESSAGE_TYPE_UDP:
                mBinding.btnMessageStyle.setTxtContent(getString(R.string.type_newwork));
                mBinding.btnTtysChooice.setVisibility(View.GONE);
                break;
            case AppInfo.MESSAGE_TYPE_SERIALPORT:
                mBinding.btnMessageStyle.setTxtContent(getString(R.string.type_serialport));
                mBinding.btnTtysChooice.setVisibility(View.VISIBLE);
                break;
        }
        int ttysPosition = SharedPerManager.getTTysPosition();
        mBinding.btnTtysChooice.setTxtContent("ttyS" + ttysPosition);
    }

    private void getRowCowByNetwork() {
        SameScreenHelper.getRowCowByNetwork(data -> {
            mBinding.switchTaskMain.setSwitchStatues(data.isMaster == 1);
            parseRowCowInput(data.rowNum, data.columnNum, data.serialNum - 1);
        });
    }

    /***
     * modify Row cow Nums
     */
    private void showModifyRowCowDialog(String title, int index, Button button) {
        if (SharedPerManager.getAutoRowCow()) {
            showToastView("当前为自动获取模式");
            return;
        }
        RadioListDialog radioListDialog = new RadioListDialog(SettingScreenActivity.this);
        List<RedioEntity> redioEntityList = getCrowListInfo();
        radioListDialog.setRadioChooiceListener((redioEntity, position) -> {
            String chooiceNum = redioEntity.getRadioText();
            button.setText(chooiceNum + "");
        });
        radioListDialog.show(title, redioEntityList, index - 1);
    }

    private void showHiddenListView(boolean show) {
//        if (show) {
//            mBinding.rlTip.setVisibility(View.VISIBLE);
//            mBinding.recycleView.setVisibility(View.VISIBLE);
//        } else {
//            mBinding.rlTip.setVisibility(View.GONE);
//            mBinding.recycleView.setVisibility(View.GONE);
//        }
    }

    private void changeRecycleViewSize(int row, int cow) {
        if (viewWidth < 1) {
            mBinding.recycleView.postDelayed(() -> {
                viewWidth = mBinding.recycleView.getWidth();
                changeRecycleViewSize(row, cow);
            }, 200);
            return;
        }
        ViewGroup.LayoutParams params = mBinding.recycleView.getLayoutParams();
        int width = mBinding.recycleView.getHeight() * cow / row;
        if (row == 1 && cow == 1) {
            width = viewWidth / 2;
        }
        params.width = Math.min(width, viewWidth);
        mBinding.recycleView.setLayoutParams(params);

    }

    private void parseRowCowInput(int row, int cow, int selectPos) {
        if (row < 1 || cow < 1) {
            showHiddenListView(false);
            return;
        }
        showHiddenListView(true);
        mBinding.recycleView.setLayoutManager(new GridLayoutManager(this, cow));
        adapter.setNewData(getRowCowList(row, cow, selectPos));
    }

    private void hiddenInputMethod(long delay) {
        if (hiddenRunnable == null) {
            hiddenRunnable = () -> {
                mBinding.etCow.clearFocus();
                mBinding.etRow.clearFocus();
                InputMethodManager inputMethod = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethod.hideSoftInputFromWindow(mBinding.etRow.getWindowToken(), 0);
                inputMethod.hideSoftInputFromWindow(mBinding.etCow.getWindowToken(), 0);
            };
        }
        mBinding.linExit.removeCallbacks(hiddenRunnable);
        mBinding.linExit.postDelayed(hiddenRunnable, delay);
    }

    private List<RowCow> getRowCowList(int row, int cow, int selPos) {
        List<RowCow> list = new ArrayList<>();
        for (int i = 0; i < row * cow; i++) {
            list.add(new RowCow(row, cow, i, i == selPos));
        }
        changeRecycleViewSize(row, cow);
        return list;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backToMainActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void backToMainActivity() {
        finish();
    }

    public List<RedioEntity> getCrowListInfo() {
        List<RedioEntity> redioEntities = new ArrayList<RedioEntity>();
        redioEntities.add(new RedioEntity("1"));
        redioEntities.add(new RedioEntity("2"));
        redioEntities.add(new RedioEntity("3"));
        redioEntities.add(new RedioEntity("4"));
        redioEntities.add(new RedioEntity("5"));
        redioEntities.add(new RedioEntity("6"));
        return redioEntities;
    }

}
