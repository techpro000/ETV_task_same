package com.etv.setting.framenew;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.etv.config.AppInfo;
import com.etv.util.MyLog;
import com.ys.etv.R;

public abstract class MessageFragment extends Fragment {

    public Animation onCreateAnimation(int paramInt1, boolean paramBoolean, int paramInt2) {
        if (paramBoolean) {
            return AnimationUtils.loadAnimation(getActivity(), R.anim.anim_enter);
        }
        return AnimationUtils.loadAnimation(getActivity(), R.anim.anim_exit);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            MyLog.cdl("=========fragment====" + action);
            if (action.equals(AppInfo.BAIDU_LOCATION_BROAD)) {
                MyLog.message("==================定位success");
                updateNetView();
            } else if (action.equals(AppInfo.SOCKET_LINE_STATUS_CHANGE)) {
                updateNetView();
            }
        }
    };

    public abstract void updateNetView();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppInfo.BAIDU_LOCATION_BROAD);
        filter.addAction(AppInfo.SOCKET_LINE_STATUS_CHANGE);
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }
    }
}
