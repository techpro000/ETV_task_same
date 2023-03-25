package com.etv.setting.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.etv.config.AppInfo;
import com.etv.listener.ObjectClickListener;
import com.etv.task.entity.TaskWorkEntity;
import com.etv.util.MyLog;
import com.etv.util.SimpleDateUtil;
import com.ys.etv.R;

import java.util.List;

public class ProTaskAdater extends BaseAdapter {

    List<TaskWorkEntity> list;
    Context context;
    LayoutInflater inflater;

    public void setList(List<TaskWorkEntity> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public ProTaskAdater(Context context, List<TaskWorkEntity> list) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.item_task_show, null);
            viewHolder.tv_task_type = (TextView) view.findViewById(R.id.tv_task_type);
            viewHolder.tv_task_name = (TextView) view.findViewById(R.id.tv_task_name);
            viewHolder.tv_task_week = (TextView) view.findViewById(R.id.tv_task_week);
            viewHolder.tv_task_start = (TextView) view.findViewById(R.id.tv_task_start);
            viewHolder.tv_task_end = (TextView) view.findViewById(R.id.tv_task_end);
            viewHolder.tv_task_send = (TextView) view.findViewById(R.id.tv_task_send);
            viewHolder.tv_task_play = (TextView) view.findViewById(R.id.tv_task_play);
            viewHolder.btn_del_task = (Button) view.findViewById(R.id.btn_del_task);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        TaskWorkEntity entity = list.get(i);
        viewHolder.btn_del_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener == null) {
                    return;
                }
                listener.clickSure(entity);
            }
        });


        boolean etMon = Boolean.parseBoolean(entity.getEtMon());
        boolean etTue = Boolean.parseBoolean(entity.getEtTue());
        boolean etWed = Boolean.parseBoolean(entity.getEtWed());
        boolean etThur = Boolean.parseBoolean(entity.getEtThur());
        boolean etFri = Boolean.parseBoolean(entity.getEtFri());
        boolean etSat = Boolean.parseBoolean(entity.getEtSat());
        boolean etSun = Boolean.parseBoolean(entity.getEtSun());
        String week = "";
        if (etMon) {
            week = week + "1";
        }
        if (etTue) {
            week = week + "2";
        }
        if (etWed) {
            week = week + "3";
        }
        if (etThur) {
            week = week + "4";
        }
        if (etFri) {
            week = week + "5";
        }
        if (etSat) {
            week = week + "6";
        }
        if (etSun) {
            week = week + "7";
        }
        viewHolder.tv_task_name.setTextColor(context.getResources().getColor(R.color.grey));
        viewHolder.tv_task_start.setTextColor(context.getResources().getColor(R.color.grey));
        viewHolder.tv_task_end.setTextColor(context.getResources().getColor(R.color.grey));
        viewHolder.tv_task_send.setTextColor(context.getResources().getColor(R.color.grey));
        viewHolder.tv_task_week.setTextColor(context.getResources().getColor(R.color.grey));
        if (curentTaskEntity != null) {
            String currentTaskId = curentTaskEntity.getTaskId();
            String taskId = entity.getTaskId();
            if (taskId.equals(currentTaskId)) {
                viewHolder.tv_task_name.setTextColor(context.getResources().getColor(R.color.blue));
                viewHolder.tv_task_start.setTextColor(context.getResources().getColor(R.color.blue));
                viewHolder.tv_task_end.setTextColor(context.getResources().getColor(R.color.blue));
                viewHolder.tv_task_send.setTextColor(context.getResources().getColor(R.color.blue));
                viewHolder.tv_task_week.setTextColor(context.getResources().getColor(R.color.blue));
            }
        }
        String taskName = entity.getEtName();
        if (taskName.length() > 10) {
            taskName = taskName.substring(0, 10);
        }
        viewHolder.tv_task_name.setText(taskName);
        viewHolder.tv_task_start.setText(entity.getStartDate() + "\n" + entity.getStartTime());
        String endDate = entity.getEndDate();
        String endTime = entity.getEndTime();
        viewHolder.tv_task_end.setText(endDate + "\n" + endTime);
        viewHolder.tv_task_send.setText(SimpleDateUtil.formatTaskTimeShow(entity.getSendTime()));
        viewHolder.tv_task_week.setText(week);
        //====================================================================
        String taskTypeModel = entity.getEtTaskType();
        MyLog.e("haha", "===显示的任务taskTypeModel==" + taskTypeModel);
        if (taskTypeModel.contains(AppInfo.TASK_TYPE_DEFAULT)) {
            viewHolder.tv_task_type.setText(getTextByString(R.string.normal_play));
        } else if (taskTypeModel.contains(AppInfo.TASK_TYPE_DOUBLE)) {
            viewHolder.tv_task_type.setText(getTextByString(R.string.double_screen));
        } else if (taskTypeModel.contains(AppInfo.TASK_TYPE_TOUCH)) {
            viewHolder.tv_task_type.setText(getTextByString(R.string.play_other));
        } else {
            viewHolder.tv_task_type.setText(getTextByString(R.string.normal_play));
        }

        String taskType = entity.getEtLevel();
        MyLog.e("haha", "===显示的任务==" + entity.toString());
        if (taskType.contains(AppInfo.TASK_PLAY_ADD_TASK)) {
            viewHolder.tv_task_play.setText(getTextByString(R.string.add_play));
            viewHolder.tv_task_play.setTextColor(context.getResources().getColor(R.color.terminal_play_down));
        } else if (taskType.contains(AppInfo.TASK_PLAY_REPLACE)) {
            viewHolder.tv_task_play.setText(getTextByString(R.string.replace_play));
            viewHolder.tv_task_play.setTextColor(context.getResources().getColor(R.color.terminal_play_down));
        } else if (taskType.contains(AppInfo.TASK_PLAY_CALL_WAIT)) {
            viewHolder.tv_task_play.setTextColor(context.getResources().getColor(R.color.red));
            viewHolder.tv_task_play.setText(getTextByString(R.string.insert_play));
        } else if (taskType.contains(AppInfo.TASK_PLAY_PLAY_SAME)) {
            viewHolder.tv_task_play.setTextColor(context.getResources().getColor(R.color.terminal_play_down));
            viewHolder.tv_task_play.setText(getTextByString(R.string.same_play));
        }  else {
            viewHolder.tv_task_play.setText(getTextByString(R.string.add_play));
            viewHolder.tv_task_play.setTextColor(context.getResources().getColor(R.color.terminal_play_down));
        }
        return view;
    }

    private String getTextByString(int id) {
        return context.getString(id);
    }


    TaskWorkEntity curentTaskEntity = null;

    public void setTaskEntityRed(TaskWorkEntity taskWorkEntity) {
        if (taskWorkEntity == null) {
            return;
        }
        this.curentTaskEntity = taskWorkEntity;
        notifyDataSetChanged();
    }

    ObjectClickListener listener;

    public void setOnClickListener(ObjectClickListener clickListener) {
        this.listener = clickListener;
    }


    class ViewHolder {
        Button btn_del_task;
        TextView tv_task_name, tv_task_week, tv_task_start, tv_task_end, tv_task_send, tv_task_type, tv_task_play;
    }

}
