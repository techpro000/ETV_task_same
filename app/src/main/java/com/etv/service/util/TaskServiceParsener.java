package com.etv.service.util;

import android.content.Context;

import com.etv.service.model.TaskWorkModel;
import com.etv.service.model.TaskWorkModelmpl;
import com.etv.util.MyLog;
import com.etv.util.SharedPerManager;

import java.io.File;
import java.util.List;

public class TaskServiceParsener {

    TaskWorkModel taskWorkModel;
    Context context;

    public TaskServiceParsener(Context context) {
        this.context = context;
        taskWorkModel = new TaskWorkModelmpl();
    }

    public void startToCheckBggImage() {
        if (context == null) {
            return;
        }
        taskWorkModel.startToCheckBggImage(context);
    }


}
