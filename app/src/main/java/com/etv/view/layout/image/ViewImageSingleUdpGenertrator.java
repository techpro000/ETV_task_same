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

import java.util.ArrayList;
import java.util.List;

/***
 * 加载图片资源
 * 加载单张图片，主要用户混播结合得控件
 */
public class ViewImageSingleUdpGenertrator extends Generator {

    Context context;
    View view;
    MediAddEntity mediAddEntity;
    int width;
    int height;
    CpListEntity cpListEntity;

    public ViewImageSingleUdpGenertrator(Context context, CpListEntity cpListEntity,
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
        handler.sendEmptyMessageDelayed(MESSAGE_DISSMISS_TASK, playTime * 1000L);
    }

    private static final int MESSAGE_DISSMISS_TASK = 9586;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == MESSAGE_DISSMISS_TASK) {
                playComplet();
            }
        }
    };

    @Override
    public int getVideoPlayCurrentDuartion() {
        return 0;
    }

    @Override
    public void timeChangeToUpdateView() {
    }


    @Override
    public void resumePlayView() {
    }

    @Override
    public void pauseDisplayView() {
    }

    @Override
    public void updateView(Object object, boolean isShowBtn) {
    }

    @Override
    public void playComplet() {
        MyLog.banner("========单张图片开始播放==playComplet===");
        if (listener != null) {
            listener.playComplete(TaskPlayStateListener.TAG_PLAY_PICTURE);
        }
    }

    @Override
    public void playPositionScenProgram(int position, List<MediAddEntity> mediAddEntities, SceneEntity currentScentity) {

    }


    @Override
    public View getView() {
        return view;
    }

    @Override
    public void clearMemory() {
        try {
            if (handler != null) {
                handler.removeMessages(MESSAGE_DISSMISS_TASK);
            }
            if (iv_abs != null) {
                GlideImageUtil.clearViewCache(iv_abs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeCacheView(String tag) {
    }


}
