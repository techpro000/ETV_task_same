//package com.etv.task.util;
//
//import android.os.Handler;
//import android.util.Log;
//
//import com.etv.config.AppInfo;
//import com.etv.task.db.DBTaskUtil;
//import com.etv.task.db.DbTaskManager;
//import com.etv.task.entity.TaskWorkEntity;
//import com.etv.task.model.TaskGetDbListener;
//import com.etv.util.MyLog;
//
//import java.util.List;
//
//public class TaskGetTigerFromDbRunnable implements Runnable {
//
//    TaskGetDbListener listener;
//    private Handler handler = new Handler();
//
//    public TaskGetTigerFromDbRunnable(TaskGetDbListener listener) {
//        this.listener = listener;
//    }
//
//    @Override
//    public void run() {
//        List<TaskWorkEntity> taskWorkCacheList = DBTaskUtil.getTaskInfoList();
//        if (taskWorkCacheList == null || taskWorkCacheList.size() < 1) {
//            backTaskList(null, "从数据库获取得数据==null");
//            return;
//        }
//        MyLog.playTask("==backTaskList===便利数据库得数量==" + taskWorkCacheList.size());
//
//        TaskWorkEntity taskBack = null;
//        for (TaskWorkEntity taskWorkEntity : taskWorkCacheList) {
//            if (taskWorkEntity == null) {
//                continue;
//            }
//            String etLevel = taskWorkEntity.getEtLevel();
//            MyLog.playTask("==backTaskList===便利数据库==" + taskWorkEntity.toString());
//            if (etLevel.contains(AppInfo.TASK_PLAY_TRIGGER)) {
//                taskBack = taskWorkEntity;
//                break;
//            }
//        }
//        if (taskBack == null) {
//            backTaskList(null, "便利数集合，没有数据==");
//        } else {
//            searchTaskInfo(taskBack);
//        }
//    }
//
//    /***
//     *获取任务信息
//     * @param taskBack
//     */
//    private void searchTaskInfo(TaskWorkEntity taskBack) {
//        TaskWorkEntity taskWorkEntity = DbTaskManager.getTaskEntityFormDb(taskBack);
//        backTaskList(taskWorkEntity, "获取任务信息");
//    }
//
//    private void backTaskList(TaskWorkEntity taskWorkEntity, String printTag) {
//        MyLog.playTask("==backTaskList===" + printTag + " / " + taskWorkEntity);
//        if (handler == null) {
//            return;
//        }
//        if (listener == null) {
//            return;
//        }
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                listener.getTaskTigerFromDb(taskWorkEntity);
//            }
//        });
//    }
//
//}
