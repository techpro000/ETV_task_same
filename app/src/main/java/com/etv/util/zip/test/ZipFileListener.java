package com.etv.util.zip.test;

public interface ZipFileListener {

    void zipProgress(int progress);

    void zipError(String error);

    void zipComplet(String filePath);

    /***
     * 检查文件是否存在，主要用来延时的，没啥作用
     */
    void checkFileStatues();
}
