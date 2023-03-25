package com.etv.util.rxjava;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.etv.task.entity.DownStatuesEntity;
import com.etv.task.entity.SameTaskEntity;
import com.etv.test.SameTaskVideo;
import com.etv.test.StartUploadEntity;
import com.udpsync.bean.CmdData;

/***
 * 软件状态监听
 */
public class AppStatuesListener extends ViewModel {

    public static AppStatuesListener instance;

    public static AppStatuesListener getInstance() {
        if (instance == null) {
            synchronized (AppStatuesListener.class) {
                if (instance == null) {
                    instance = new AppStatuesListener();
                }
            }
        }
        return instance;
    }

    //网络变化
    public MutableLiveData<Boolean> NetChange = new MutableLiveData<Boolean>();
    // 用来更新系统时间得
    public MutableLiveData<String> timeChangeEvent = new MutableLiveData<String>();
    //用来更新主界面得通知类
    public MutableLiveData<String> UpdateMainBggEvent = new MutableLiveData<String>();
    //定时开关机设置完成，这里需要刷新界面
    public MutableLiveData<String> PowerOnOffEvent = new MutableLiveData<String>();
    //更新播放界面音量得问题
    public MutableLiveData<String> UpdateMainMediaVoiceEvent = new MutableLiveData<String>();
    //下载状态更新
    public MutableLiveData<DownStatuesEntity> DownStatuesEntity = new MutableLiveData<DownStatuesEntity>();
    //播放进度
    public MutableLiveData<CmdData> playProgress = new MutableLiveData<CmdData>();
    //开始同步主屏任务通知
    public MutableLiveData<CmdData> startToUploadSameTask = new MutableLiveData<CmdData>();

}
