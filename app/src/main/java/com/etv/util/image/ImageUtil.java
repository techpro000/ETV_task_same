package com.etv.util.image;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.etv.config.AppConfig;
import com.ys.etv.R;

/**
 * Created by Administrator on 2019-04-17.
 */

public class ImageUtil {

    Context context;

    public ImageUtil(Context context) {
        this.context = context;
    }


    /***
     * 获取默认得背景图
     * @return
     */
    public static int getShowBggLogo() {
        int imageBggDefault = R.mipmap.app_logo_default;
        return imageBggDefault;
    }


    /**
     * 计算出所需要压缩的大小
     *
     * @param options
     * @param reqWidth  我们期望的图片的宽，单位px
     * @param reqHeight 我们期望的图片的高，单位px
     * @return
     */
    public static int caculateSampleSize(BitmapFactory.Options options, float reqWidth, float reqHeight) {
        int sampleSize = 1;
        int picWidth = options.outWidth;
        int picHeight = options.outHeight;
        if (picWidth > reqWidth || picHeight > reqHeight) {
            int halfPicWidth = picWidth / 2;
            int halfPicHeight = picHeight / 2;
            while (halfPicWidth / sampleSize > reqWidth || halfPicHeight / sampleSize > reqHeight) {
                sampleSize *= 2;
            }
        }
        return sampleSize;
    }
}
