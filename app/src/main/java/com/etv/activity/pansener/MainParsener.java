package com.etv.activity.pansener;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.etv.activity.MainActivity;
import com.etv.activity.view.MainView;
import com.etv.config.AppConfig;
import com.etv.config.AppInfo;
import com.etv.listener.TimeChangeListener;
import com.etv.service.EtvService;
import com.etv.service.util.EtvServerModule;
import com.etv.service.util.EtvServerModuleImpl;
import com.etv.service.util.TaskServiceView;
import com.etv.util.MyLog;
import com.etv.util.NetWorkUtils;
import com.etv.util.SharedPerManager;
import com.etv.util.SimpleDateUtil;
import com.etv.util.TimerDealUtil;
import com.ys.etv.R;
import com.ys.model.util.ActivityCollector;

public class MainParsener {

    Context context;
    MainView mainView;
    private static final String TAG = "cdl";

    public MainParsener(Context context, MainView mainView) {
        this.mainView = mainView;
        this.context = context;
        getView();
    }

    TextView tv_time;
    ImageView iv_net_state;
    ImageView iv_line_state;
    ImageView iv_work_model;

    private void getView() {
        if (mainView == null) {
            return;
        }
        try {
            iv_work_model = mainView.getIvWorkModel();
            iv_net_state = mainView.getIvWifiState();
            iv_line_state = mainView.getIvlineState();
            tv_time = mainView.getTimeView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateTimeNetLineView() {
        try {
            iv_work_model.setBackgroundResource(R.mipmap.dialog_net);
            //更新网络状态
            boolean isNetLine = NetWorkUtils.isNetworkConnected(context);
            iv_net_state.setBackgroundResource(isNetLine ? R.mipmap.net_line : R.mipmap.net_disline);
            //更新时间
            String currentTime = SimpleDateUtil.formatCurrentTime();
            tv_time.setText(currentTime);
            if (isNetLine) {
                boolean isOnline = AppConfig.isOnline;
                iv_line_state.setBackgroundResource(isOnline ? R.mipmap.line_web : R.mipmap.line_web_dis);
            } else {
                iv_line_state.setBackgroundResource(R.mipmap.line_web_dis);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int addTimerNum = 0;

    public void startTimerToPlay(String tag) {
        if (!MainActivity.isMainForst) {
            MyLog.i("main", "====开始计时-不再主界面，不及时====");
            addTimerNum = 0;
            return;
        }
        addTimerNum = 0;
        int timeDelay = AppConfig.CHECK_TIMER_TO_PLAY();
        long currentTime = System.currentTimeMillis();

        TimerDealUtil.getInstance().setTimeChangeListener(currentTime, new TimeChangeListener() {
            @Override
            public void timeChangeMin(long classId) {
                addTimerNum++;
                if (addTimerNum > timeDelay) {
                    addTimerNum = 0;
                    MyLog.i("main", "=====开始计时=倒计时完成=====");
                    toBackMainView();
                }
            }
        });
    }

    public void onStopParsener() {
        MyLog.i("main", "====开始计时===关闭及时功能==");
        TimerDealUtil.getInstance().setTimeChangeListener(-1, null);
    }

    private void toBackMainView() {
        if (!MainActivity.isMainForst) {
            MyLog.i("main", "=====开始计时去播放任务=1111=主界面不再前台====");
            return;
        }
        boolean mainForst = ActivityCollector.isForeground(context, MainActivity.class.getName());
        MyLog.i("main", "=====开始计时去播放任务=22222=====" + mainForst);
        if (!mainForst) {
            return;
        }
        mainView.startToCheckTaskToActivity("主界面倒计时");
    }

    /***
     * 修改信息给服务器
     */
    EtvServerModule etvServerModule;

    public void updateDevInfoToWeb() {
        long lastTime = SharedPerManager.getLastUpdateDevTime();
        long curreTime = System.currentTimeMillis();
        if (Math.abs(curreTime - lastTime) < (30 * 1000)) {
            MyLog.cdl("======提交设备信息===频率太高了，中断操作=");
            return;
        }
        SharedPerManager.setLastUpdateDevTime(curreTime);
        MyLog.cdl("======提交设备信息===00000=");
        if (etvServerModule == null) {
            etvServerModule = new EtvServerModuleImpl();
        }
        etvServerModule.queryDeviceInfoFromWeb(context, new TaskServiceView() {

            @Override
            public void getDevInfoFromWeb(boolean isSuccess, String errorDesc) {
                if (!isSuccess) {
                    return;
                }
                EtvService.getInstance().updateDevStatuesToWeb(context);
            }
        });
    }
}