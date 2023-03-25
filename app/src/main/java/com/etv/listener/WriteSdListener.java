package com.etv.listener;

/***
 * 读写文件监听
 */
public interface WriteSdListener {

    void writeProgress(int progress);

    void writeSuccess(String savePath);

    void writrFailed(String errorrDesc);
}
