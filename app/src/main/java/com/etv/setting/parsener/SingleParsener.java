package com.etv.setting.parsener;

import android.content.Context;

import com.EtvApplication;
import com.etv.config.AppInfo;
import com.etv.entity.BeanEntity;
import com.etv.entity.ScreenEntity;
import com.etv.task.entity.ViewPosition;
import com.etv.util.MyLog;
import com.etv.util.system.SystemManagerUtil;
import com.ys.etv.R;

import java.util.ArrayList;
import java.util.List;

public class SingleParsener {

    public static int getLayoutFromId(Context context, int id) {
        int imageId = R.mipmap.horizontal_1;
        List<BeanEntity> listsImage = getLayoutListAllInfo(context);
        for (int i = 0; i < listsImage.size(); i++) {
            BeanEntity beanEntity = listsImage.get(i);
            int tag = beanEntity.getTagId();
            imageId = beanEntity.getImageId();
            if (tag == id) {
                break;
            }
        }
        return imageId;
    }


    /***
     *     public static final String PROGRAM_POSITION_MAIN = "1";   //主屏
     *     public static final String PROGRAM_POSITION_SECOND = "2"; //副屏
     * @param context
     * @return
     */
    public static List<BeanEntity> getLayoutList(Context context, String screenInfo) {
        //获取屏幕方向
        boolean isHorVer = SystemManagerUtil.isScreenHorOrVer(context, screenInfo);
        //根据屏幕方向来适配界面
        return addScreenInfiListBack(isHorVer);
    }

    /****
     * @param isHorVer
     * true  横屏
     * false 副屏
     * @return
     */
    private static List<BeanEntity> addScreenInfiListBack(boolean isHorVer) {
        List<BeanEntity> lists = new ArrayList<BeanEntity>();
        if (isHorVer) {//横屏
            lists.add(new BeanEntity(R.mipmap.horizontal_1, ViewPosition.VIEW_LAYOUT_HRO_VIEW + "", ViewPosition.VIEW_LAYOUT_HRO_VIEW));
            lists.add(new BeanEntity(R.mipmap.horizontal_image_only, ViewPosition.VIEW_LAYOUT_IMAGE_HRO + "", ViewPosition.VIEW_LAYOUT_IMAGE_HRO));
            lists.add(new BeanEntity(R.mipmap.horizontal_video_only, ViewPosition.VIEW_LAYOUT_VIDEO_HRO + "", ViewPosition.VIEW_LAYOUT_VIDEO_HRO));
            lists.add(new BeanEntity(R.mipmap.horizontal_wps_only, ViewPosition.VIEW_LAYOUT_WPS_HRO + "", ViewPosition.VIEW_LAYOUT_WPS_HRO));//文档
            lists.add(new BeanEntity(R.mipmap.horizontal_2, ViewPosition.VIEW_LAYOUT_2 + "", ViewPosition.VIEW_LAYOUT_2));
            lists.add(new BeanEntity(R.mipmap.horizontal_3, ViewPosition.VIEW_LAYOUT_3 + "", ViewPosition.VIEW_LAYOUT_3));
            lists.add(new BeanEntity(R.mipmap.horizontal_4, ViewPosition.VIEW_LAYOUT_4 + "", ViewPosition.VIEW_LAYOUT_4));
            lists.add(new BeanEntity(R.mipmap.horizontal_5, ViewPosition.VIEW_LAYOUT_5 + "", ViewPosition.VIEW_LAYOUT_5));
            lists.add(new BeanEntity(R.mipmap.horizontal_6, ViewPosition.VIEW_LAYOUT_6 + "", ViewPosition.VIEW_LAYOUT_6));
            lists.add(new BeanEntity(R.mipmap.horizontal_7, ViewPosition.VIEW_LAYOUT_7 + "", ViewPosition.VIEW_LAYOUT_7));
            lists.add(new BeanEntity(R.mipmap.horizontal_8, ViewPosition.VIEW_LAYOUT_8 + "", ViewPosition.VIEW_LAYOUT_8));
            lists.add(new BeanEntity(R.mipmap.horizontal_9, ViewPosition.VIEW_LAYOUT_9 + "", ViewPosition.VIEW_LAYOUT_9));
            lists.add(new BeanEntity(R.mipmap.horizontal_10, ViewPosition.VIEW_LAYOUT_10 + "", ViewPosition.VIEW_LAYOUT_10));
            lists.add(new BeanEntity(R.mipmap.horizontal_11, ViewPosition.VIEW_LAYOUT_11 + "", ViewPosition.VIEW_LAYOUT_11));
            lists.add(new BeanEntity(R.mipmap.horizontal_12, ViewPosition.VIEW_LAYOUT_12 + "", ViewPosition.VIEW_LAYOUT_12));
            lists.add(new BeanEntity(R.mipmap.horizontal_13, ViewPosition.VIEW_LAYOUT_13 + "", ViewPosition.VIEW_LAYOUT_13));
            lists.add(new BeanEntity(R.mipmap.horizontal_14, ViewPosition.VIEW_LAYOUT_14 + "", ViewPosition.VIEW_LAYOUT_14));
        } else {
            lists.add(new BeanEntity(R.mipmap.vertical_53, ViewPosition.VIEW_LAYOUT_VER_VIEW + "", ViewPosition.VIEW_LAYOUT_VER_VIEW));
            lists.add(new BeanEntity(R.mipmap.vertical_image_only, ViewPosition.VIEW_LAYOUT_IMAGE_VER + "", ViewPosition.VIEW_LAYOUT_IMAGE_VER));
            lists.add(new BeanEntity(R.mipmap.vertical_video_only, ViewPosition.VIEW_LAYOUT_VIDEO_VER + "", ViewPosition.VIEW_LAYOUT_VIDEO_VER));
            lists.add(new BeanEntity(R.mipmap.vertical_wps_only, ViewPosition.VIEW_LAYOUT_WPS_VER + "", ViewPosition.VIEW_LAYOUT_WPS_VER));
            lists.add(new BeanEntity(R.mipmap.vertical_54, ViewPosition.VIEW_LAYOUT_54 + "", ViewPosition.VIEW_LAYOUT_54));
            lists.add(new BeanEntity(R.mipmap.vertical_55, ViewPosition.VIEW_LAYOUT_55 + "", ViewPosition.VIEW_LAYOUT_55));
            lists.add(new BeanEntity(R.mipmap.vertical_56, ViewPosition.VIEW_LAYOUT_56 + "", ViewPosition.VIEW_LAYOUT_56));
            lists.add(new BeanEntity(R.mipmap.vertical_57, ViewPosition.VIEW_LAYOUT_57 + "", ViewPosition.VIEW_LAYOUT_57));
            lists.add(new BeanEntity(R.mipmap.vertical_58, ViewPosition.VIEW_LAYOUT_58 + "", ViewPosition.VIEW_LAYOUT_58));
            lists.add(new BeanEntity(R.mipmap.vertical_59, ViewPosition.VIEW_LAYOUT_59 + "", ViewPosition.VIEW_LAYOUT_59));
            lists.add(new BeanEntity(R.mipmap.vertical_60, ViewPosition.VIEW_LAYOUT_60 + "", ViewPosition.VIEW_LAYOUT_60));
            lists.add(new BeanEntity(R.mipmap.vertical_61, ViewPosition.VIEW_LAYOUT_61 + "", ViewPosition.VIEW_LAYOUT_61));
            lists.add(new BeanEntity(R.mipmap.vertical_62, ViewPosition.VIEW_LAYOUT_62 + "", ViewPosition.VIEW_LAYOUT_62));
            lists.add(new BeanEntity(R.mipmap.vertical_63, ViewPosition.VIEW_LAYOUT_63 + "", ViewPosition.VIEW_LAYOUT_63));
            lists.add(new BeanEntity(R.mipmap.vertical_64, ViewPosition.VIEW_LAYOUT_64 + "", ViewPosition.VIEW_LAYOUT_64));
            lists.add(new BeanEntity(R.mipmap.vertical_65, ViewPosition.VIEW_LAYOUT_65 + "", ViewPosition.VIEW_LAYOUT_65));
            lists.add(new BeanEntity(R.mipmap.vertical_66, ViewPosition.VIEW_LAYOUT_66 + "", ViewPosition.VIEW_LAYOUT_66));
        }
        return lists;
    }

    /**
     * 用来回显图片信息得
     *
     * @param context
     * @return
     */
    public static List<BeanEntity> getLayoutListAllInfo(Context context) {
        List<BeanEntity> lists = new ArrayList<BeanEntity>();
        lists.add(new BeanEntity(R.mipmap.horizontal_1, ViewPosition.VIEW_LAYOUT_HRO_VIEW + "", ViewPosition.VIEW_LAYOUT_HRO_VIEW));
        lists.add(new BeanEntity(R.mipmap.horizontal_image_only, ViewPosition.VIEW_LAYOUT_IMAGE_HRO + "", ViewPosition.VIEW_LAYOUT_IMAGE_HRO));
        lists.add(new BeanEntity(R.mipmap.horizontal_video_only, ViewPosition.VIEW_LAYOUT_VIDEO_HRO + "", ViewPosition.VIEW_LAYOUT_VIDEO_HRO));
        lists.add(new BeanEntity(R.mipmap.horizontal_wps_only, ViewPosition.VIEW_LAYOUT_WPS_HRO + "", ViewPosition.VIEW_LAYOUT_WPS_HRO));//文档
        lists.add(new BeanEntity(R.mipmap.horizontal_2, ViewPosition.VIEW_LAYOUT_2 + "", ViewPosition.VIEW_LAYOUT_2));
        lists.add(new BeanEntity(R.mipmap.horizontal_3, ViewPosition.VIEW_LAYOUT_3 + "", ViewPosition.VIEW_LAYOUT_3));
        lists.add(new BeanEntity(R.mipmap.horizontal_4, ViewPosition.VIEW_LAYOUT_4 + "", ViewPosition.VIEW_LAYOUT_4));
        lists.add(new BeanEntity(R.mipmap.horizontal_5, ViewPosition.VIEW_LAYOUT_5 + "", ViewPosition.VIEW_LAYOUT_5));
        lists.add(new BeanEntity(R.mipmap.horizontal_6, ViewPosition.VIEW_LAYOUT_6 + "", ViewPosition.VIEW_LAYOUT_6));
        lists.add(new BeanEntity(R.mipmap.horizontal_7, ViewPosition.VIEW_LAYOUT_7 + "", ViewPosition.VIEW_LAYOUT_7));
        lists.add(new BeanEntity(R.mipmap.horizontal_8, ViewPosition.VIEW_LAYOUT_8 + "", ViewPosition.VIEW_LAYOUT_8));
        lists.add(new BeanEntity(R.mipmap.horizontal_9, ViewPosition.VIEW_LAYOUT_9 + "", ViewPosition.VIEW_LAYOUT_9));
        lists.add(new BeanEntity(R.mipmap.horizontal_10, ViewPosition.VIEW_LAYOUT_10 + "", ViewPosition.VIEW_LAYOUT_10));
        lists.add(new BeanEntity(R.mipmap.horizontal_11, ViewPosition.VIEW_LAYOUT_11 + "", ViewPosition.VIEW_LAYOUT_11));
        lists.add(new BeanEntity(R.mipmap.horizontal_12, ViewPosition.VIEW_LAYOUT_12 + "", ViewPosition.VIEW_LAYOUT_12));
        lists.add(new BeanEntity(R.mipmap.horizontal_13, ViewPosition.VIEW_LAYOUT_13 + "", ViewPosition.VIEW_LAYOUT_13));
        lists.add(new BeanEntity(R.mipmap.horizontal_14, ViewPosition.VIEW_LAYOUT_14 + "", ViewPosition.VIEW_LAYOUT_14));
        //==================================================================================================================================
        lists.add(new BeanEntity(R.mipmap.vertical_53, ViewPosition.VIEW_LAYOUT_VER_VIEW + "", ViewPosition.VIEW_LAYOUT_VER_VIEW));
        lists.add(new BeanEntity(R.mipmap.vertical_image_only, ViewPosition.VIEW_LAYOUT_IMAGE_VER + "", ViewPosition.VIEW_LAYOUT_IMAGE_VER));
        lists.add(new BeanEntity(R.mipmap.vertical_video_only, ViewPosition.VIEW_LAYOUT_VIDEO_VER + "", ViewPosition.VIEW_LAYOUT_VIDEO_VER));
        lists.add(new BeanEntity(R.mipmap.vertical_wps_only, ViewPosition.VIEW_LAYOUT_WPS_VER + "", ViewPosition.VIEW_LAYOUT_WPS_VER));
        lists.add(new BeanEntity(R.mipmap.vertical_54, ViewPosition.VIEW_LAYOUT_54 + "", ViewPosition.VIEW_LAYOUT_54));
        lists.add(new BeanEntity(R.mipmap.vertical_55, ViewPosition.VIEW_LAYOUT_55 + "", ViewPosition.VIEW_LAYOUT_55));
        lists.add(new BeanEntity(R.mipmap.vertical_56, ViewPosition.VIEW_LAYOUT_56 + "", ViewPosition.VIEW_LAYOUT_56));
        lists.add(new BeanEntity(R.mipmap.vertical_57, ViewPosition.VIEW_LAYOUT_57 + "", ViewPosition.VIEW_LAYOUT_57));
        lists.add(new BeanEntity(R.mipmap.vertical_58, ViewPosition.VIEW_LAYOUT_58 + "", ViewPosition.VIEW_LAYOUT_58));
        lists.add(new BeanEntity(R.mipmap.vertical_59, ViewPosition.VIEW_LAYOUT_59 + "", ViewPosition.VIEW_LAYOUT_59));
        lists.add(new BeanEntity(R.mipmap.vertical_60, ViewPosition.VIEW_LAYOUT_60 + "", ViewPosition.VIEW_LAYOUT_60));
        lists.add(new BeanEntity(R.mipmap.vertical_61, ViewPosition.VIEW_LAYOUT_61 + "", ViewPosition.VIEW_LAYOUT_61));
        lists.add(new BeanEntity(R.mipmap.vertical_62, ViewPosition.VIEW_LAYOUT_62 + "", ViewPosition.VIEW_LAYOUT_62));
        lists.add(new BeanEntity(R.mipmap.vertical_63, ViewPosition.VIEW_LAYOUT_63 + "", ViewPosition.VIEW_LAYOUT_63));
        lists.add(new BeanEntity(R.mipmap.vertical_64, ViewPosition.VIEW_LAYOUT_64 + "", ViewPosition.VIEW_LAYOUT_64));
        lists.add(new BeanEntity(R.mipmap.vertical_65, ViewPosition.VIEW_LAYOUT_65 + "", ViewPosition.VIEW_LAYOUT_65));
        lists.add(new BeanEntity(R.mipmap.vertical_66, ViewPosition.VIEW_LAYOUT_66 + "", ViewPosition.VIEW_LAYOUT_66));
        return lists;
    }
}
