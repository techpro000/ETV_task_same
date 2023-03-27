package com.ys.model.file;

/***
 * 文件夹拷贝监听
 */
public interface WriteFilesListener {

    void writeStart(String copyPath, String pastePath);

    void writeProgress(int currentPosition, int totalPosition, int writeProgress);

    void writeSuccess();

    void writrFailed(String errorrDesc);
}
