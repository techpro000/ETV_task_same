package com.etv.task.model;

import com.etv.config.ApiInfo;
import com.etv.config.AppInfo;
import com.etv.entity.DeviceTaskSameInfoEntity;
import com.etv.listener.TaskPlayStateListener;
import com.etv.service.EtvService;
import com.etv.service.TaskWorkService;
import com.etv.task.entity.CpListEntity;
import com.etv.task.entity.SceneEntity;
import com.etv.task.entity.TaskWorkEntity;
import com.etv.task.util.GetTaskDownFileListRunnable;
import com.etv.task.util.ParsenerJsonRunnable;
import com.etv.task.util.TaskGetFromDbRunnable;
import com.etv.util.Biantai;
import com.etv.util.CodeUtil;
import com.etv.util.MyLog;
import com.etv.util.SharedPerManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class TaskModelmpl implements TaskMudel {

    @Override
    public void getTaskDownListInfoFromDbByTaskIdRunnable(List<TaskWorkEntity> lists, TaskGetDownListListener taskGetDownListListener, String printTag) {
        MyLog.cdl("=====获取下载列表信息===" + printTag);
        GetTaskDownFileListRunnable runnable = new GetTaskDownFileListRunnable(lists, taskGetDownListListener);
        EtvService.getInstance().executor(runnable);
    }

    /**
     * 检查有没有下载资格
     *
     * @param listener
     */
    @Override
    public void checkDownLimit(final TaskCheckLimitListener listener) {
        String devId = CodeUtil.getUniquePsuedoID();
        String userName = SharedPerManager.getUserName();
        String requestUrl = ApiInfo.CHECK_DOWN_LIMIT();
        OkHttpUtils
                .post()
                .url(requestUrl)
                .addParams("clNo", devId)
                .addParams("userName", userName)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, String errorDesc, int id) {
                        MyLog.task("===检查下载资格failed==" + errorDesc);
                        listener.checkDownLimitSpeed(false, 0, errorDesc);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        MyLog.task("===检查下载资格success==" + response);
                        parsenerSpeedLimit(response, listener);
                    }
                });
    }

    /**
     * 解析自己有没有下载资格
     *
     * @param json
     */
    private void parsenerSpeedLimit(String json, TaskCheckLimitListener listener) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            int code = jsonObject.getInt("code");
            String errorDesc = jsonObject.getString("msg");
            if (code != 0) {
                listener.checkDownLimitSpeed(false, 0, errorDesc);
                return;
            }
            String data = jsonObject.getString("data");
            JSONObject jsonData = new JSONObject(data);
            int currDownNum = jsonData.getInt("currDownNum");
            listener.checkDownLimitSpeed(true, currDownNum, "请求成功,准备判断资格");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取符合当前时间的任务需要播放的task
    @Override
    public void getPlayTaskListFormDb(TaskGetDbListener listener, String printTag, int delTag) {
        MyLog.task("==从服务器中获取任务==" + printTag);
        TaskGetFromDbRunnable runnable = new TaskGetFromDbRunnable(listener, printTag, delTag);
        EtvService.getInstance().executor(runnable);
    }

    /***
     * 判断是否可以播放下一个节目
     * @param sceneEntityList
     * @param playTag
     * @return
     */
    @Override
    public void playNextProgram(List<SceneEntity> sceneEntityList, int currentPositionPm, int playTag, TaskRequestListener listener) {
        try {
            if (sceneEntityList == null || sceneEntityList.size() < 2) {
                listener.playNextProgram(false, null, -1);
                MyLog.playTask("2222====节目==场景数量只有0个或者一天，不用切换");
                return;
            }
            SceneEntity sceneEntity = sceneEntityList.get(currentPositionPm);
            List<CpListEntity> cpList = sceneEntity.getListCp();
            if (cpList == null || cpList.size() < 1) {
                MyLog.playTask("2222====获取的控件信息==null");
                listener.playNextProgram(false, null, 0);
                return;
            }
            //2:混播的优先级次之
            boolean isHasVideoImage = false;
            for (int i = 0; i < cpList.size(); i++) {
                String cpType = cpList.get(i).getCoType();
                if (cpType.contains(AppInfo.VIEW_IMAGE_VIDEO)) {
                    isHasVideoImage = true;
                }
            }
            if (isHasVideoImage) {  //如果有视频控件的话监听tag = TaskPlayStateListener.VIDEO
                if (playTag == TaskPlayStateListener.TAG_PLAY_VIDEO_IMAGE) {
                    MyLog.playTask("2222====混播播放完毕，可以切换下一个了");
                    listener.playNextProgram(true, sceneEntityList, 0);
                }
                return;
            }

            //3：视频的优先级第三
            boolean isHasVideo = false;
            for (int i = 0; i < cpList.size(); i++) {
                String cpType = cpList.get(i).getCoType();
                if (cpType.contains(AppInfo.VIEW_VIDEO)) {
                    isHasVideo = true;
                }
            }
            if (isHasVideo) {  //如果有视频控件的话监听tag = TaskPlayStateListener.VIDEO
                if (playTag == TaskPlayStateListener.TAG_PLAY_VIDEO) {
                    MyLog.playTask("2222====视频播放完毕，可以切换下一个了");
                    listener.playNextProgram(true, sceneEntityList, 0);
                }
                return;
            }
//            5：图片排第五
            boolean isHasPic = false;
            for (int i = 0; i < cpList.size(); i++) {
                String cpType = cpList.get(i).getCoType();
                if (cpType.contains(AppInfo.VIEW_IMAGE)) {
                    isHasPic = true;
                }
            }
            if (isHasPic) {  //如果有视频控件的话监听tag = TaskPlayStateListener.VIDEO
                if (playTag == TaskPlayStateListener.TAG_PLAY_PICTURE) {
                    MyLog.playTask("2222====图片播放完毕，可以切换下一个了");
                    listener.playNextProgram(true, sceneEntityList, 0);
                }
                return;
            }
            listener.playNextProgram(false, null, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //=================================请求网络,解析部分,解析任务===================================================================================

    /***
     * 从服务器中获取数据
     * @param listener
     */
    @Override
    public void requestTaskInfo(final TaskRequestListener listener, final String printTag) {
        if (Biantai.isRequestTaskInfo()) {
            return;
        }
        TaskWorkService.setCurrentTaskType(TaskWorkService.TASK_TYPE_REQUEST, "开启请求任务信息");
        String code = CodeUtil.getUniquePsuedoID();
        String requestUrl = ApiInfo.getClientTaskUrl();
        String logTask = requestUrl + "?clNo=" + code;
        MyLog.task("===请求任务得接口==" + logTask);
        OkHttpUtils
                .post()
                .url(requestUrl)
                .addParams("clNo", code)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, String errorDesc, int id) {
                        TaskWorkService.setCurrentTaskType(TaskWorkService.TASK_TYPE_DEFAULT, "请求失败，返回信息");
                        MyLog.task("===failed==" + errorDesc);
                        if (listener != null) {
                            listener.parserJsonOver("网络请求失败: " + errorDesc);
                        }
                    }

                    @Override
                    public void onResponse(String json, int id) {
                        TaskWorkService.setCurrentTaskType(TaskWorkService.TASK_TYPE_DEFAULT, "请求成功，恢复状态");
                        MyLog.task("===任务请求success==" + json);
                        if (json == null || json.length() < 5) {
                            listener.parserJsonOver("网络请求失败: json==null");
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            int code = jsonObject.getInt("code");
                            String msg = jsonObject.getString("msg");
                            if (code != 0) { //获取信息失败，清理数据库
                                listener.parserJsonOver("网络请求 msg=" + msg);
                                return;
                            }
                            String data = jsonObject.getString("data");
                            parsenerJson("请求完毕，去解析", data, listener);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void parsenerJson(String tag, String data, final TaskRequestListener listener) {
        MyLog.task("=======谁在解析？==" + tag);
        try {
            TaskWorkService.setCurrentTaskType(TaskWorkService.TASK_TYPE_PARSENER_JSON, "请求成功，切换解析状态");
            ParsenerJsonRunnable runnable = new ParsenerJsonRunnable(true, data, listener);
            EtvService.getInstance().executor(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析单机版本的任务
     *
     * @param data
     * @param listener
     */
    public void parsenerSingleTaskEntity(String data, TaskRequestListener listener) {
        ParsenerJsonRunnable runnable = new ParsenerJsonRunnable(false, data, listener);
        EtvService.getInstance().executor(runnable);
    }

    /**
     * 获取同步任务得设备信息
     */
    @Override
    public void getSameTaskDevInfoFromServer(String etId, TaskSameScreenLinkListener listener) {
        String requestUrl = ApiInfo.SAME_TASK_URL();
        OkHttpUtils
                .post()
                .url(requestUrl)
                .addParams("etId", etId)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, String errorDesc, int id) {
                        MyLog.task("===修改字幕状态==" + errorDesc);
                        if (listener == null) {
                            return;
                        }
                        listener.backDevListInfo(false, null, errorDesc);
                    }

                    @Override
                    public void onResponse(String json, int id) {
                        MyLog.task("===getSameTaskDevInfoFromServer==" + json);
                        if (listener == null) {
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            int code = jsonObject.getInt("code");
                            String msg = jsonObject.getString("msg");
                            if (code != 0) {
                                listener.backDevListInfo(false, null, msg);
                                return;
                            }
                            String data = jsonObject.getString("data");
                            MyLog.task("===getSameTaskDevInfoFromServer==data==" + data);
                            JSONArray jsonArray = new JSONArray(data);
                            int devNum = jsonArray.length();
                            if (devNum < 2) {  //只有一台设备或者没有，就直接中断
                                listener.backDevListInfo(false, null, "只有一台设备，不往下执行了");
                                return;
                            }
                            List<DeviceTaskSameInfoEntity> deviceEntities = new ArrayList<DeviceTaskSameInfoEntity>();
                            for (int i = 0; i < devNum; i++) {
                                JSONObject jsonData = jsonArray.getJSONObject(i);
                                int lineState = Integer.parseInt(jsonData.getString("lineState"));
                                String ip = jsonData.getString("ip");
                                String mac = jsonData.getString("mac");
                                if (lineState > 0) {
                                    DeviceTaskSameInfoEntity deviceTaskSameInfoEntity = new DeviceTaskSameInfoEntity(lineState, ip, mac);
                                    deviceEntities.add(deviceTaskSameInfoEntity);
                                }
                            }

                            if (deviceEntities.size() < 2) {//只有一台设备在线，不去往下执行
                                listener.backDevListInfo(false, null, "只有一台设备在线，不往下执行了");
                            } else {
                                listener.backDevListInfo(true, deviceEntities, "获取同步任务信息返回成功");
                            }
                        } catch (Exception e) {
                            listener.backDevListInfo(false, null, "解析error== > " + e.toString());
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void modifyTextInfoToWeb(String taId, String subtitle, final TaskRequestListener listener) {
        String requestUrl = ApiInfo.updateTxtInfo();
        OkHttpUtils
                .post()
                .url(requestUrl)
                .addParams("taId", taId)
                .addParams("subtitle", subtitle)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, String errorDesc, int id) {
                        MyLog.task("===修改字幕状态==" + errorDesc);
                        listener.modifyTxtInfoStatues(false, errorDesc);
                    }

                    @Override
                    public void onResponse(String json, int id) {
                        MyLog.task("===修改字幕状态==" + json);
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            int code = jsonObject.getInt("code");
                            String msg = jsonObject.getString("msg");
                            if (code != 0) { //获取信息失败, 清理数据库
                                listener.modifyTxtInfoStatues(false, msg);
                                return;
                            }
                            listener.modifyTxtInfoStatues(true, msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
