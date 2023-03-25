package com.etv.task.parsener;

import android.content.Context;

import com.etv.config.ApiInfo;
import com.etv.config.AppInfo;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.etv.service.TaskWorkService;
import com.etv.task.entity.SceneEntity;
import com.etv.task.entity.TaskWorkEntity;
import com.etv.task.model.TaskCheckLimitListener;
import com.etv.task.model.TaskGetDbListener;
import com.etv.task.model.TaskModelUtil;
import com.etv.task.model.TaskModelmpl;
import com.etv.task.model.TaskMudel;
import com.etv.task.model.TaskRequestListener;
import com.etv.task.view.TaskView;
import com.etv.util.MyLog;
import com.etv.util.NetWorkUtils;
import com.etv.util.SharedPerManager;

import org.json.JSONObject;

import java.util.List;

import okhttp3.Call;

public class TaskParsener implements TaskRequestListener {

    Context context;
    TaskView taskView;
    TaskMudel taskMudel;

    public TaskParsener(Context context, TaskView taskView) {
        this.taskView = taskView;
        this.context = context;
        taskMudel = new TaskModelmpl();
    }

    public void requestTaskUrl(String printTag) {
        if (!NetWorkUtils.isNetworkConnected(context)) {
            parserJsonOver("requestTaskUrl");   //没有网络去加载本地的素材
            return;
        }
        taskMudel.requestTaskInfo(this, printTag);
    }

    /***
     * 不管解析成功还是失败
     * 都要去从数据库中获取数据来判断是否需要下载新文件
     * @param tag
     */
    @Override
    public void parserJsonOver(String tag) {
        TaskWorkService.setCurrentTaskType(TaskWorkService.TASK_TYPE_DEFAULT, "解析完成，这里恢复状态");
        //检查已经下载得文件信息。主要是清理多余得素材
        if (taskView == null) {
            return;
        }
        taskMudel.getPlayTaskListFormDb(new TaskGetDbListener() {
            @Override
            public void getTaskFromDb(List<TaskWorkEntity> taskWorkEntityList) {
                if (taskWorkEntityList == null || taskWorkEntityList.size() < 1) {
                    MyLog.task("========parserJsonOver=======没有获取到任务=====");
                    taskView.finishMyShelf("没有合适的任务");
                    return;
                }
                MyLog.task("========parserJsonOver=======准备去播放===== " + tag + " / " + taskWorkEntityList.size());
                taskView.backTaskList(taskWorkEntityList, tag);
            }

            @Override
            public void getTaskTigerFromDb(TaskWorkEntity taskWorkEntity) {

            }
        }, "========请求解析完成，准备去播放=====", TaskModelUtil.DEL_LASTDATE_ONLY);
    }

    @Override
    public void finishMySelf(String errorDesc) {
        TaskWorkService.setCurrentTaskType(TaskWorkService.TASK_TYPE_DEFAULT, "解析完成，这里恢复原始状态");
        MyLog.task("=========finishMySelf========" + errorDesc);
        if (taskView == null) {
            return;
        }
        taskView.finishMyShelf(errorDesc);
    }

    /**
     * 获取内存阀值
     * 以及下载限制
     */
    public void getSystemSettingInfoTask(String printMsg) {
        String requestUrl = ApiInfo.getSdManagerCheckUrl();
        OkHttpUtils
                .post()
                .url(requestUrl)
                .addParams("userName", SharedPerManager.getUserName())
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, String errorDesc, int id) {
                        MyLog.cdl("获取内存阀值=====" + printMsg + " / " + errorDesc);
                    }

                    @Override
                    public void onResponse(String json, int id) {
                        MyLog.cdl("获取内存阀值==" + printMsg + " / " + json);
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            int code = jsonObject.getInt("code");
                            if (code == 0) {
                                String data = jsonObject.getString("data");
                                JSONObject jsonData = new JSONObject(data);
                                int riLevel = Integer.parseInt(jsonData.getString("riLevel"));
                                String riSpeedLimit = jsonData.getString("riSpeedLimit");  //下载速度
                                String riDownNumLimit = jsonData.getString("riDownNumLimit"); //下载台数限制
                                if (riDownNumLimit == null || riDownNumLimit.length() < 1) {
                                    riDownNumLimit = "200";
                                }
                                if (riSpeedLimit == null || riSpeedLimit.length() < 1) {
                                    riSpeedLimit = "5000";
                                }
                                int limitSpeed = Integer.parseInt(riSpeedLimit);
                                int limitNumLine = Integer.parseInt(riDownNumLimit);
                                SharedPerManager.setLimitDevNum(limitNumLine);
                                SharedPerManager.setLimitSpeed(limitSpeed);
                                SharedPerManager.setSdcardManagerAuthor(riLevel);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 检查当前是否有资格去下载
     */
    public void checkDownLimit(TaskCheckLimitListener limitListener) {
        if (!NetWorkUtils.isNetworkConnected(context)) {
            limitListener.checkDownLimitSpeed(false, 100, "网络异常或者未连接服务器");
            return;
        }
        taskMudel.checkDownLimit(limitListener);
    }


    @Override
    public void modifyTxtInfoStatues(boolean isSuccess, String desc) {

    }

    /***
     * 判断取播放下一个节目，playTaskActivity界面调用
     * @param isBack
     * @param listPm
     */
    @Override
    public void playNextProgram(boolean isBack, List<SceneEntity> listPm, int tag) {

    }


}
