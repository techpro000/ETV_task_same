package com.etv.util.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import com.etv.http.util.BitmapWriteLocalRunnable;
import com.etv.listener.WriteBitmapToLocalListener;
import com.etv.service.EtvService;
import com.etv.util.MyLog;
import com.etv.util.RootCmd;
import com.etv.util.SharedPerManager;
import com.etv.util.system.CpuModel;
import com.etv.util.system.SystemManagerUtil;

import java.io.File;

public class ImageRotateUtil {

    Context context;

    public ImageRotateUtil(Context context) {
        this.context = context;
    }

    /**
     * 高通得旋转图片
     *
     * @param filePath
     * @param listener
     */
    public void rotateBitmapGaotong(String filePath, WriteBitmapToLocalListener listener) {
        File file = new File(filePath);
        if (!file.exists()) {
            listener.writeStatues(false, "压缩文件不存在");
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        if (bitmap == null) {
            listener.writeStatues(false, "传入的bitmap==null");
            return;
        }
        int roateNum = getScreenRoate();
        int degress = 0;
        if (roateNum == 90) {
            degress = -90;
        }
        Matrix m = new Matrix();
        m.postRotate(degress);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, 1080,
                1920, m, true);
        writeBitmapToSdcard(bitmap, writePath, listener);
    }

    //根据旋转角度来旋转
    public void rotateBitmapByRoate(String filePath, int roateNum, WriteBitmapToLocalListener listener) {
        File file = new File(filePath);
        if (!file.exists()) {
            listener.writeStatues(false, "压缩文件不存在");
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        if (bitmap == null) {
            listener.writeStatues(false, "传入的bitmap==null");
            return;
        }
        Matrix m = new Matrix();
        m.postRotate(roateNum);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), m, true);
        String writePath = "/sdcard/capture_cache_roate.jpg";
        writeBitmapToSdcard(bitmap, writePath, listener);
    }

    //旋转照片3128
    public void rotateBitmap3128(String filePath, WriteBitmapToLocalListener listener) {
        File file = new File(filePath);
        if (!file.exists()) {
            listener.writeStatues(false, "压缩文件不存在");
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        if (bitmap == null) {
            listener.writeStatues(false, "传入的bitmap==null");
            return;
        }
        int degress = 0 - getScreenRoate();
        if (degress == 0) {
            listener.writeStatues(true, filePath);
            return;
        }
        Matrix m = new Matrix();
        m.postRotate(degress);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), m, true);
        writeBitmapToSdcard(bitmap, writePath, listener);
    }

    private static final String writePath = "/sdcard/capture_cache.jpg";

    private void writeBitmapToSdcard(Bitmap bitmap, String writePath, WriteBitmapToLocalListener listener) {
        BitmapWriteLocalRunnable runnable = new BitmapWriteLocalRunnable(bitmap, writePath, listener);
        EtvService.getInstance().executor(runnable);
    }

    /**
     * 获取屏幕旋转的角度
     *
     * @return
     */
    public int getScreenRoate() {
        String cpuModel = CpuModel.getMobileType();
        int roateNum = 0;
        if (cpuModel.contains("rk312x")) {
            roateNum = SystemManagerUtil.getScreenRoate(context);
        } else {
            String roate = RootCmd.getProperty(RootCmd.PROOERTY_INFO, "0");
            roateNum = Integer.parseInt(roate);
        }
        return roateNum;
    }


}
