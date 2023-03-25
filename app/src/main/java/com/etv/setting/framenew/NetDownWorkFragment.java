package com.etv.setting.framenew;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.etv.setting.TaskInfoActivity;
import com.ys.etv.R;


/**
 * 网络下发模式设置
 */
public class NetDownWorkFragment extends Fragment implements View.OnClickListener {

    public Animation onCreateAnimation(int paramInt1, boolean paramBoolean, int paramInt2) {
        if (paramBoolean) {
            return AnimationUtils.loadAnimation(getActivity(), R.anim.anim_enter);
        }
        return AnimationUtils.loadAnimation(getActivity(), R.anim.anim_exit);
    }

    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        View view = View.inflate(getActivity(), R.layout.fragment_net_down_setting, null);
        initView(view);
        return view;
    }

    Button btn_task_play;

    private void initView(View view) {
        btn_task_play = (Button) view.findViewById(R.id.btn_task_play);
        btn_task_play.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_task_play:
                startActivity(new Intent(getActivity(), TaskInfoActivity.class));
                break;
        }
    }

}
