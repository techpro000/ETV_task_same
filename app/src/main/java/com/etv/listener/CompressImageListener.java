package com.etv.listener;


/**
 * 图片压缩回调接口
 */
public interface CompressImageListener {
    void backErrorDesc(String desc);

    void backImageSuccess(String oldPath, String imagePath);
}