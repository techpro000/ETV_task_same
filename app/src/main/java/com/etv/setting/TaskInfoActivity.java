package com.etv.setting;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.etv.config.ApiInfo;
import com.etv.config.AppInfo;
import com.ys.model.dialog.MyToastView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.etv.listener.ObjectClickListener;
import com.etv.setting.adapter.ProTaskAdater;
import com.etv.task.db.DBTaskUtil;
import com.etv.task.entity.TaskWorkEntity;
import com.etv.task.model.TaskModelmpl;
import com.etv.util.CodeUtil;
import com.etv.util.FileUtil;
import com.etv.util.MyLog;
import com.etv.util.SharedPerManager;
import com.ys.model.dialog.WaitDialogUtil;
import com.ys.model.dialog.OridinryDialog;
import com.ys.model.listener.OridinryDialogClick;
import com.ys.etv.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class TaskInfoActivity extends SettingBaseActivity implements View.OnClickListener {

    Button btn_clear_pro;
    ProTaskAdater adapter;
    ListView list_pro;
    TextView tv_no_date;
    LinearLayout iv_no_data;

    List<TaskWorkEntity> listPro = new ArrayList<TaskWorkEntity>();
    TaskModelmpl taskModelmpl;

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.fragment_task_info);
        initView();
    }

    LinearLayout lin_exit;

    private void initView() {
        AppInfo.startCheckTaskTag = false;
        taskModelmpl = new TaskModelmpl();
        lin_exit = (LinearLayout) findViewById(R.id.lin_exit);
        lin_exit.setOnClickListener(this);
        tv_no_date = (TextView) findViewById(R.id.tv_no_date);
        list_pro = (ListView) findViewById(R.id.list_pro);
        adapter = new ProTaskAdater(TaskInfoActivity.this, listPro);
        list_pro.setAdapter(adapter);
        iv_no_data = (LinearLayout) findViewById(R.id.iv_no_data);
        btn_clear_pro = (Button) findViewById(R.id.btn_clear_pro);
        btn_clear_pro.setOnClickListener(this);

        adapter.setOnClickListener(new ObjectClickListener() {
            @Override
            public void clickSure(Object object) {
                TaskWorkEntity taskWorkEntity = (TaskWorkEntity) object;
                if (taskWorkEntity == null) {
                    showToastView("任务==null");
                    return;
                }
                showDelTaskDialog(taskWorkEntity);
            }
        });
    }


    private Handler handler = new Handler();

    @Override
    protected void onResume() {
        super.onResume();
        getTaskFormDb();
    }

    private void showDelTaskDialog(TaskWorkEntity taskWorkEntity) {
        OridinryDialog oridinryDialog = new OridinryDialog(TaskInfoActivity.this);
        oridinryDialog.setOnDialogClickListener(new OridinryDialogClick() {
            @Override
            public void sure() {
                delTaskToWeb(taskWorkEntity);
            }

            @Override
            public void noSure() {

            }
        });
        oridinryDialog.show(getLanguageFromResurce(R.string.if_del_program), getLanguageFromResurce(R.string.delete), getLanguageFromResurce(R.string.cancel));
    }

    private void delNetLoadModelTask() {
        showWaitDialog(true);
        //网络导出模式
        DBTaskUtil.clearAllDbInfo("终端清理所有得任务");
        String taskFilePath = AppInfo.BASE_TASK_URL();
        FileUtil.deleteDirOrFilePath(taskFilePath, "手动清理文件");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showWaitDialog(false);
                getTaskFormDb();
            }
        }, 2000);
    }


    private void delTaskToWeb(TaskWorkEntity taskWorkEntity) {
        showWaitDialog(true);
        String requestUrl = ApiInfo.DEL_TASK_BY_USER();
        String taskId = taskWorkEntity.getTaskId();
        OkHttpUtils
                .post()
                .url(requestUrl)
                .addParams("taskId", taskId + "")
                .addParams("clientNo", CodeUtil.getUniquePsuedoID())
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, String errorDesc, int id) {
                        showWaitDialog(false);
                        MyLog.task("===修改字幕状态==" + errorDesc);
                        showToastView(getLanguageFromResurce(R.string.del_failed) + ":" + errorDesc);
                    }

                    @Override
                    public void onResponse(String json, int id) {
                        showWaitDialog(false);
                        MyLog.task("===删除任务==" + json);
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            int code = jsonObject.getInt("code");
                            String msg = jsonObject.getString("msg");
                            if (code != 0) {
                                //获取信息失败，清理数据库
                                showToastView(getLanguageFromResurce(R.string.del_failed) + " : " + msg);
                                return;
                            }
                            boolean isDel = DBTaskUtil.delTaskById(taskId);
                            if (isDel) {
                                getTaskFormDb();
                                showToastView(getLanguageFromResurce(R.string.del_success) + " : " + msg);
                            } else {
                                showToastView(getLanguageFromResurce(R.string.del_failed));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void getTaskFormDb() {
        listPro.clear();
        adapter.setList(listPro);
        listPro = DBTaskUtil.getTaskInfoList();
        iv_no_data.setVisibility(View.GONE);
        if (listPro == null || listPro.size() < 1) {
            showToastView(getLanguageFromResurce(R.string.no_data));
            iv_no_data.setVisibility(View.VISIBLE);
            tv_no_date.setText(getLanguageFromResurce(R.string.no_data));
            return;
        }
        adapter.setList(listPro);
//        TaskWorkEntity taskWorkEntity = taskModelmpl.getPlayTaskEntityFormDb();
//        if (taskWorkEntity == null) {
//            return;
//        }
//        adapter.setTaskEntityRed(taskWorkEntity);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lin_exit:
                finish();
                break;
            case R.id.btn_clear_pro:
                break;

        }
    }

    OridinryDialog oridinaryDialog;

    WaitDialogUtil waitDialogUtil;

    private void showWaitDialog(boolean isShow) {
        if (waitDialogUtil == null) {
            waitDialogUtil = new WaitDialogUtil(TaskInfoActivity.this);
        }
        if (isShow) {
            waitDialogUtil.show(getLanguageFromResurce(R.string.clear) + "...");
        } else {
            waitDialogUtil.dismiss();
        }
    }


}
