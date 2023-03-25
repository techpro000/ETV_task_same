package com.etv.setting.framenew;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.EtvApplication;
import com.etv.activity.FileListActivity;
import com.etv.adapter.PopStringAdapter;
import com.etv.config.AppConfig;
import com.etv.config.AppInfo;
import com.etv.entity.ScreenEntity;
import com.etv.http.util.GetMediaListFromPathNewRunnable;
import com.etv.service.EtvService;
import com.etv.setting.ScreenShowSetting;
import com.etv.setting.SingleSettingActivity;
import com.etv.setting.framenew.util.SettingFragmentUtil;
import com.etv.setting.parsener.SingleParsener;
import com.etv.task.entity.MediAddEntity;
import com.etv.task.entity.SingleTaskEntity;
import com.etv.task.entity.ViewPosition;
import com.etv.util.MyLog;
import com.etv.util.SharedPerManager;
import com.etv.util.system.LanguageChangeUtil;
import com.ys.model.dialog.MyToastView;
import com.etv.view.dialog.CustomPopWindow;
import com.ys.etv.R;
import com.ys.model.dialog.RadioListDialog;
import com.ys.model.entity.RedioEntity;
import com.ys.model.listener.RadioChooiceListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 单机模式设定
 */
public class SingleWorkFragment extends Fragment implements View.OnClickListener {

    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle) {
        View view = View.inflate(getActivity(), R.layout.fragment_single_work_setting, null);
        initView(view);
        initPopWindow();
        return view;
    }

    Button btn_time_show;
    Button btn_anim_type;
    TextView tv_show_num;
    SeekBar seek_video_num;
    TextView tv_show_num_video;
    Button btn_choice_view;
    ImageView iv_show_layout;
    CustomPopWindow pop_pic_type;
    ListView lv_content;
    List<String> list_anim_tye = new ArrayList<>();
    PopStringAdapter adapter;
    TextView tv_video_num, tv_pic_num;
    Button btn_see_local_video;
    Button btn_reduce, btn_add;
    LinearLayout lin_animal_layout;
    private Button btn_reduce_wps, btn_add_wps;
    private TextView tv_show_num_wps;
    private TextView tv_doc_num;

    Button btn_choice_view_second;  //副屏布局选择按钮
    Button btn_screen_show;
    ImageView iv_show_layout_second;  //副屏显示小控件
    LinearLayout lin_layout_second;   //副屏LinLayout
    SeekBar seek_video_back_num;
    TextView tv_show_back_num_video;


    private void initView(View view) {
        list_anim_tye = SettingFragmentUtil.getAnimType(getActivity());
        lin_layout_second = (LinearLayout) view.findViewById(R.id.lin_layout_second);
        btn_screen_show = (Button) view.findViewById(R.id.btn_screen_show);
        btn_screen_show.setOnClickListener(this);
        tv_doc_num = (TextView) view.findViewById(R.id.tv_doc_num);
        tv_show_num_wps = (TextView) view.findViewById(R.id.tv_show_num_wps);
        btn_reduce_wps = (Button) view.findViewById(R.id.btn_reduce_wps);
        btn_add_wps = (Button) view.findViewById(R.id.btn_add_wps);
        btn_reduce_wps.setOnClickListener(this);
        btn_add_wps.setOnClickListener(this);

        btn_time_show = (Button) view.findViewById(R.id.btn_time_show);
        btn_time_show.setOnClickListener(this);
        btn_choice_view_second = (Button) view.findViewById(R.id.btn_choice_view_second);
        btn_choice_view_second.setOnClickListener(this);
        iv_show_layout_second = (ImageView) view.findViewById(R.id.iv_show_layout_second);

        lin_animal_layout = (LinearLayout) view.findViewById(R.id.lin_animal_layout);
        iv_show_layout = (ImageView) view.findViewById(R.id.iv_show_layout);
        seek_video_num = (SeekBar) view.findViewById(R.id.seek_video_num);
        seek_video_num.setOnSeekBarChangeListener(seekListenerVideo);


        seek_video_back_num = (SeekBar) view.findViewById(R.id.seek_video_back_num);
        seek_video_back_num.setOnSeekBarChangeListener(seekListenerback);
        tv_show_back_num_video = (TextView) view.findViewById(R.id.tv_show_back_num_video);

        tv_show_num_video = (TextView) view.findViewById(R.id.tv_show_num_video);
        tv_video_num = (TextView) view.findViewById(R.id.tv_video_num);
        tv_pic_num = (TextView) view.findViewById(R.id.tv_pic_num);
        tv_show_num = (TextView) view.findViewById(R.id.tv_show_num);
        btn_anim_type = (Button) view.findViewById(R.id.btn_anim_type);
        btn_anim_type.setOnClickListener(this);
        btn_choice_view = (Button) view.findViewById(R.id.btn_choice_view);
        btn_choice_view.setOnClickListener(this);
        btn_see_local_video = (Button) view.findViewById(R.id.btn_see_local_video);
        btn_see_local_video.setOnClickListener(this);

        btn_reduce = (Button) view.findViewById(R.id.btn_reduce);
        btn_reduce.setOnClickListener(this);
        btn_add = (Button) view.findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);
        updateView("initView");
    }

    /**
     * 因为每次进入界面都是replace，所以不用在onResume里面刷新了
     */
    public static boolean autoUpdate = false;

    public void onResumeView() {
        if (autoUpdate) {
            updateView("onResumeView");
            autoUpdate = false;
        }
    }

    private void updateView(String tag) {
        int timeShow = SharedPerManager.getShowTimeEnable();
        btn_time_show.setText(timeShow == 0 ? getString(R.string.time_hiddle) : getString(R.string.time_show));
        MyLog.cdl("========界面刷新==" + tag);
        int defaultNum = SharedPerManager.getPicDistanceTime();
        tv_show_num.setText(defaultNum + "");
        int defaultVideoNum = SharedPerManager.getSingleVideoVoiceNum();
        tv_show_num_video.setText(defaultVideoNum + "");
        seek_video_num.setProgress(defaultVideoNum);
        //背景音乐
        int defaultBackNum = SharedPerManager.getSingleBackVoiceNum();
        tv_show_back_num_video.setText(defaultBackNum + "");
        seek_video_back_num.setProgress(defaultBackNum);

        int pic_anim = SharedPerManager.getSinglePicAnimiType();
        String type = list_anim_tye.get(pic_anim);
        btn_anim_type.setText(type);
        int viewId = SharedPerManager.getSingleLayoutTag();

        btn_choice_view.setText(getActivity().getString(R.string.layout_id) + " (" + viewId + ")");

        int imageId = SingleParsener.getLayoutFromId(getActivity(), viewId);
        iv_show_layout.setBackgroundResource(imageId);
        if (viewId == ViewPosition.VIEW_LAYOUT_HRO_VIEW || viewId == ViewPosition.VIEW_LAYOUT_VER_VIEW) {
            lin_animal_layout.setVisibility(View.GONE);
        } else {
            lin_animal_layout.setVisibility(View.VISIBLE);
        }
        //副屏相关操作
        List<ScreenEntity> listScreen = EtvApplication.getInstance().getListScreen();
        if (listScreen == null || listScreen.size() < 2) {
            lin_layout_second.setVisibility(View.GONE);
        } else {
            lin_layout_second.setVisibility(View.VISIBLE);
        }
        int viewSecondId = SharedPerManager.getSingleSecondLayoutTag();
        int imageSecondId = SingleParsener.getLayoutFromId(getActivity(), viewSecondId);
        iv_show_layout_second.setBackgroundResource(imageSecondId);
        btn_choice_view_second.setText(getActivity().getString(R.string.layout_id) + " (" + viewSecondId + ")");

        int disTimeWps = SharedPerManager.getWpsDistanceTime();
        tv_show_num_wps.setText(disTimeWps + "");
        getFileFromSdCard();
    }

    private void showToastView(String s) {
        MyToastView.getInstance().Toast(getActivity(), s);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_time_show:
                showTaskTimeDialog();
                break;
            case R.id.btn_screen_show:
                startActivity(new Intent(getActivity(), ScreenShowSetting.class));
                break;
            case R.id.btn_reduce_wps:  //文档切换时间
                int disTimeWps = SharedPerManager.getWpsDistanceTime();
                if (disTimeWps < 4) {
                    showToastView(getActivity().getString(R.string.time_morethree));
                    return;
                }
                disTimeWps--;
                SharedPerManager.setWpsDistanceTime(disTimeWps);
                tv_show_num_wps.setText(disTimeWps + "");
                showToastView(LanguageChangeUtil.getLanguageFromResurceWithPosition(getActivity(), R.string.dis_timemin, disTimeWps + ""));
                break;
            case R.id.btn_add_wps:   //文档切换时间
                int disTimeAddWps = SharedPerManager.getWpsDistanceTime();
                disTimeAddWps++;
                SharedPerManager.setWpsDistanceTime(disTimeAddWps);
                tv_show_num_wps.setText(disTimeAddWps + "");
                showToastView(LanguageChangeUtil.getLanguageFromResurceWithPosition(getActivity(), R.string.dis_timemin, disTimeAddWps + ""));
                break;
            case R.id.btn_reduce:
                int disTime = SharedPerManager.getPicDistanceTime();
                if (disTime < 4) {
                    showToastView(getActivity().getString(R.string.time_morethree));
                    return;
                }
                disTime--;
                SharedPerManager.setPicDistanceTime(disTime);
                tv_show_num.setText(disTime + "");
                showToastView(LanguageChangeUtil.getLanguageFromResurceWithPosition(getActivity(), R.string.dis_timemin, disTime + ""));
                break;
            case R.id.btn_add:
                int disTimeAdd = SharedPerManager.getPicDistanceTime();
                disTimeAdd++;
                SharedPerManager.setPicDistanceTime(disTimeAdd);
                tv_show_num.setText(disTimeAdd + "");
                showToastView(LanguageChangeUtil.getLanguageFromResurceWithPosition(getActivity(), R.string.dis_timemin, disTimeAdd + ""));
                break;
            case R.id.btn_see_local_video:
                String path = AppInfo.TASK_SINGLE_PATH();
                Intent intentFile = new Intent();
                intentFile.setClass(getActivity(), FileListActivity.class);
                intentFile.putExtra(FileListActivity.PATH_SEARCH, path);
                startActivity(intentFile);
                break;
            case R.id.btn_choice_view_second:  //设置副屏
                Intent intentSecond = new Intent(getActivity(), SingleSettingActivity.class);
                intentSecond.putExtra(SingleSettingActivity.SCREEN_TAG, AppInfo.PROGRAM_POSITION_SECOND);
                startActivity(intentSecond);
                break;
            case R.id.btn_choice_view:  //设置主屏
                Intent intent = new Intent(getActivity(), SingleSettingActivity.class);
                intent.putExtra(SingleSettingActivity.SCREEN_TAG, AppInfo.PROGRAM_POSITION_MAIN);
                startActivity(intent);
                break;
            case R.id.btn_anim_type:
                pop_pic_type.showAsDropDown(view);
                break;
        }
    }

    private void showTaskTimeDialog() {
        List<RedioEntity> lists = new ArrayList<RedioEntity>();
        lists.add(new RedioEntity(getString(R.string.time_hiddle)));
        lists.add(new RedioEntity(getString(R.string.time_show)));
        int showPosition = SharedPerManager.getShowTimeEnable();
        RadioListDialog radioListDialog = new RadioListDialog(getActivity());
        radioListDialog.setRadioChooiceListener(new RadioChooiceListener() {
            @Override
            public void backChooiceInfo(RedioEntity redioEntity, int chooicePosition) {
                SharedPerManager.setShowTimeEnable(chooicePosition);
                updateView("修改单机时间标签");
            }
        });
        radioListDialog.show(getString(R.string.show_time), lists, showPosition);
    }


    private void initPopWindow() {
        View popView = View.inflate(getActivity(), R.layout.view_pop_procityarea, null);
        pop_pic_type = new CustomPopWindow.PopupWindowBuilder(getActivity())
                .setView(popView)
                .size(300, 300)
                .create();
        lv_content = (ListView) popView.findViewById(R.id.lv_content);
        adapter = new PopStringAdapter(getActivity(), list_anim_tye);
        lv_content.setAdapter(adapter);
        lv_content.setOnItemClickListener(onItemClienListener);
    }

    AdapterView.OnItemClickListener onItemClienListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int positon, long l) {
            pop_pic_type.dissmiss();
//            if (positon == 0) {
//                positon = 1;
//            }
            btn_anim_type.setText(list_anim_tye.get(positon));
            showToastView(getActivity().getString(R.string.set_anim) + list_anim_tye.get(positon));
            SharedPerManager.setSinglePicAnimiType(positon);
            updateView("布局选中回调");
        }
    };

    SeekBar.OnSeekBarChangeListener seekListenerback = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int position, boolean b) {
            SharedPerManager.setSingleBackVoiceNum(position);
            tv_show_back_num_video.setText(position + "");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };


    SeekBar.OnSeekBarChangeListener seekListenerVideo = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int position, boolean b) {
            SharedPerManager.setSingleVideoVoiceNum(position);
            tv_show_num_video.setText(position + "");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private void getFileFromSdCard() {
        tv_video_num.setText("0");
        tv_pic_num.setText("0");
        String path = AppInfo.TASK_SINGLE_PATH();
        GetMediaListFromPathNewRunnable runnable = new GetMediaListFromPathNewRunnable(path, new GetMediaListFromPathNewRunnable.GetSingleTaskEntityListener() {
            @Override
            public void backTaskEntity(boolean isTrue, SingleTaskEntity singleTaskEntity, String errorDesc) {
                if (!isTrue) {
                    tv_video_num.setText("0");
                    tv_pic_num.setText("0");
                    return;
                }
                try {
                    List<MediAddEntity> list_image_main = singleTaskEntity.getList_image();
                    List<MediAddEntity> list_video_main = singleTaskEntity.getList_video();
                    List<MediAddEntity> list_doc_main = singleTaskEntity.getList_doc();
                    List<MediAddEntity> list_image_double = singleTaskEntity.getList_image_double();
                    List<MediAddEntity> list_video_double = singleTaskEntity.getList_video_double();
                    List<MediAddEntity> list_doc_double = singleTaskEntity.getList_doc_double();
                    int imageSize = list_image_main.size() + list_image_double.size();
                    int videoSize = list_video_main.size() + list_video_double.size();
                    int docSize = list_doc_main.size() + list_doc_double.size();
                    tv_video_num.setText(videoSize + "");
                    tv_pic_num.setText(imageSize + "");
                    tv_doc_num.setText(docSize + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        EtvService.getInstance().executor(runnable);
    }
}
