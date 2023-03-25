package com;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.etv.entity.ScreenEntity;
import com.etv.task.entity.TaskWorkEntity;
import com.etv.util.CrashExceptionHandler;
import com.ys.model.util.SharedManagerModel;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.https.HttpsUtils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class EtvApplication extends Application {

    public static EtvApplication instance;
    static Context context;
    public List<ScreenEntity> listScreen;             //缓存屏幕得相关属性
    public List<TaskWorkEntity> taskWorkEntityList;   //缓存任务得相关属性

    public static EtvApplication getInstance() {
        return instance;
    }

    SharedManagerModel sharedPerManager;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        context = this;
        sharedPerManager = new SharedManagerModel(this, "ETV_SHARE");
        LitePal.initialize(this);  //数据库初始化
        initOther();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Context getContext() {
        return context;
    }

    private void initOther() {
        CrashExceptionHandler crashExceptionHandler = CrashExceptionHandler.getCrashInstance();
        crashExceptionHandler.init();
        taskWorkEntityList = new ArrayList<TaskWorkEntity>();
        listScreen = new ArrayList<ScreenEntity>();
        initOkHttp();
    }

    private void initOkHttp() {
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    //获取屏幕得个数
    public List<ScreenEntity> getListScreen() {
        return listScreen;
    }

    //保存屏幕得个数
    public void setListScreen(List<ScreenEntity> listScreen) {
        this.listScreen = listScreen;
    }

    //获取当前播放任务
    public List<TaskWorkEntity> getTaskWorkEntityList() {
        return taskWorkEntityList;
    }

    //设备当前播放得任务续航经
    public void setTaskWorkEntityList(List<TaskWorkEntity> taskWorkEntityList) {
        this.taskWorkEntityList = taskWorkEntityList;
    }

    public void saveData(String key, Object data) {
        sharedPerManager.saveData(key, data);
    }

    public Object getData(String key, Object defaultObject) {
        return sharedPerManager.getData(key, defaultObject);
    }

}
