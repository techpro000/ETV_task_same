package com.etv.view.layout.image;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.etv.config.AppInfo;
import com.etv.db.DbStatiscs;
import com.etv.entity.StatisticsEntity;
import com.etv.listener.TaskPlayStateListener;
import com.etv.task.entity.CpListEntity;
import com.etv.task.entity.MediAddEntity;
import com.etv.task.entity.SceneEntity;
import com.etv.util.MyLog;
import com.etv.util.image.glide.GlideImageUtil;
import com.etv.view.layout.Generator;
import com.ys.etv.R;
import java.util.List;

/***
 * 加载图片资源
 * 加载单张图片，主要用户混播结合得控件
 */
public class ViewImageSingleGenertrator extends Generator {

    Context context;
    View view;
    MediAddEntity mediAddEntity;
    int width;
    int height;
    CpListEntity cpListEntity;

    public ViewImageSingleGenertrator(Context context, CpListEntity cpListEntity,
                                      int startX, int StartY, int width, int height, MediAddEntity mediAddEntity) {
        super(context, startX, StartY, width, height);
        this.context = context;
        this.width = width;
        this.height = height;
        this.mediAddEntity = mediAddEntity;
        this.cpListEntity = cpListEntity;
        view = LayoutInflater.from(context).inflate(R.layout.view_image_abs, null);
        initView(view);
    }

    @Override
    public void updateTextInfo(Object object) {

    }

    ImageView iv_abs;
    int playTime = 10;

    private void initView(View view) {
        if (mediAddEntity == null) {
            return;
        }
        iv_abs = (ImageView) view.findViewById(R.id.iv_abs);
        iv_abs.setScaleType(ImageView.ScaleType.FIT_XY);
        String delayTime = mediAddEntity.getPlayParam();
        try {
            playTime = Integer.parseInt(delayTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (playTime < 3) {
            playTime = 3;
        }
        String imagePath = mediAddEntity.getUrl();
        GlideImageUtil.loadImageByPath(context, imagePath, iv_abs);
        MyLog.banner("========单张图片开始播放=====" + playTime + "/ imagePath = " + imagePath);
        handler.sendEmptyMessageDelayed(TIME_DELAY, playTime * 1000);
    }

    private static final int TIME_DELAY = 78954;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            handler.removeMessages(TIME_DELAY);
            if (msg.what == TIME_DELAY) {
                if (isStartRun) {
                    //运行状态
                    playComplet();
                } else {
                    //暂停状态
                    handler.sendEmptyMessageDelayed(TIME_DELAY, playTime * 1000);
                }
            }
        }
    };

    @Override
    public int getVideoPlayCurrentDuartion() {
        return 0;
    }

    //当前是否是运行状态
    private boolean isStartRun = true;

    @Override
    public void resumePlayView() {
        isStartRun = true;
    }

    @Override
    public void pauseDisplayView() {
        isStartRun = false;
    }


    @Override
    public void updateView(Object object, boolean isShowBtn) {
//        initView(view);
    }

    @Override
    public void playComplet() {
        MyLog.playMix("======单张图片播放playComplet=====");
        toUpdatePlayNum();
        if (listener != null) {
            listener.playComplete(TaskPlayStateListener.TAG_PLAY_PICTURE);
        }
    }

    @Override
    public void playPositionScenProgram(int position, List<MediAddEntity> mediAddEntities, SceneEntity currentScentity) {

    }

    public void toUpdatePlayNum() {
        try {
            if (listener == null) {
                return;
            }
            if (mediAddEntity == null) {
                return;
            }
            String midId = mediAddEntity.getMidId();
            String delayTime = mediAddEntity.getPlayParam();
            int playTime = 10;
            try {
                playTime = Integer.parseInt(delayTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
            addImageUpdateToWeb(midId, playTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addImageUpdateToWeb(String midId, int timeDisplay) {
        String resourType = AppInfo.VIEW_IMAGE;
        long saveTime = System.currentTimeMillis();
        StatisticsEntity statisticsEntity = new StatisticsEntity(midId, resourType, timeDisplay, 1, saveTime);
        DbStatiscs.saveStatiseToLocal(statisticsEntity, "图片单张添加统计");
    }

    @Override
    public void timeChangeToUpdateView() {

    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public void clearMemory() {
        try {
            if (iv_abs != null) {
                GlideImageUtil.clearViewCache(iv_abs);
            }
            if (handler != null) {
                handler.removeMessages(TIME_DELAY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeCacheView(String tag) {
    }


}
