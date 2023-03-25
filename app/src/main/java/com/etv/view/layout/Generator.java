package com.etv.view.layout;

import android.content.Context;
import android.view.View;
import android.widget.AbsoluteLayout;

import com.etv.listener.TaskPlayStateListener;
import com.etv.listener.TimeChangeListener;
import com.etv.task.entity.MediAddEntity;
import com.etv.task.entity.SceneEntity;

import java.util.List;

/**
 * 动态生成控件基类
 * Created by Neo on 2015/11/2 0002.
 */
public abstract class Generator {

    public Generator() {
    }

    public Generator(Context context, int x, int y, int width, int height) {
        this.setX(x);
        this.setY(y);
        this.setWidth(width);
        this.setHeight(height);
    }

    private int x, y;                    //控件左上角坐标
    private int width, height;           //控件宽高

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public abstract void timeChangeToUpdateView();

    /**
     * 获取生成的控件
     *
     * @return 生成好的控件
     */
    public abstract View getView();

    public abstract void clearMemory();

    /**
     * 清理缓存View,用来无缝切换
     */
    public abstract void removeCacheView(String tag);

    /***
     * 提供一个接口去刷新界面
     */
    public abstract void updateView(Object object, boolean isShowBtn);

    public abstract void updateTextInfo(Object object);

    /***
     *  播放完毕回调界面
     *  > 0  正常播放完毕
     *  < 0  单个播放完毕
     */
    public abstract void playComplet();

    /***
     * 获取当前得播放进度
     * @return
     */
    public abstract int getVideoPlayCurrentDuartion();

    /**
     * 获取在AbsoluteLayout中的布局参数,可根据需要进行覆盖
     *
     * @return
     */
    public AbsoluteLayout.LayoutParams getLayoutParams() {
        AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(getWidth(), getHeight(), getX(), getY());
        return params;
    }

    //暂停播放
    public abstract void pauseDisplayView();

    //恢复播放
    public abstract void resumePlayView();

    //播放指定位置得场景
    public abstract void playPositionScenProgram(int position, List<MediAddEntity> mediAddEntities, SceneEntity currentScentity);

    public TaskPlayStateListener listener;

    public void setPlayStateChangeListener(TaskPlayStateListener listener) {
        this.listener = listener;
    }


}
