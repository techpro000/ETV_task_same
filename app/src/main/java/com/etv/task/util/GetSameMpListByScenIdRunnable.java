package com.etv.task.util;

import android.os.Handler;

import com.etv.task.db.DBTaskUtil;
import com.etv.task.entity.CpListEntity;
import com.etv.task.entity.MpListEntity;
import com.etv.util.MyLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 根据场景ID去获取素材信息
 */
public class GetSameMpListByScenIdRunnable implements Runnable {

    String sencenId;
    Handler handler = new Handler();
    TaskSameMpListInfoListener listener;

    public GetSameMpListByScenIdRunnable(String sencenId, TaskSameMpListInfoListener listener) {
        this.sencenId = sencenId;
        this.listener = listener;
    }

    @Override
    public void run() {
        List<MpListEntity> sameList = new ArrayList<MpListEntity>();
        try {
            List<CpListEntity> cpListEntityList = DBTaskUtil.getCoPInfoListByProId(sencenId);
            if (cpListEntityList == null || cpListEntityList.size() < 1) {
                backMpListToView(null);
                return;
            }
            for (int i = 0; i < cpListEntityList.size(); i++) {
                CpListEntity cpListEntity = cpListEntityList.get(i);
                String cpId = cpListEntity.getCpidId();
                String coType = cpListEntity.getCoType();
                if (TaskDealUtil.isResourceType(coType)) {    //文档，图片，音频，视频
                    MyLog.task("=======解析素材信息==" + coType);
                    List<MpListEntity> mpList = DBTaskUtil.getMpListInfoById(cpId, DBTaskUtil.MP_DEFAULT, "根据场景ID去获取素材信息");
                    sameList.addAll(mpList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        backMpListToView(sameList);
    }

    private void backMpListToView(List<MpListEntity> sameList) {
        if (listener == null) {
            return;
        }
        if (handler == null) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.backSameMpListToView(sameList);
            }
        });
    }

    public interface TaskSameMpListInfoListener {
        void backSameMpListToView(List<MpListEntity> sameList);
    }


}
