package com.ys.model.file;

/***
 * 读写文件监听
 */
public interface WriteSdListener {

    void writeProgress(int currentPostion, int totalNum, int progress);

    void writeSuccess(String savePath);

    void writrFailed(String errorrDesc);
}
