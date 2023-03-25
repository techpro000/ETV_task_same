package com.etv.activity.sdcheck.model;


import android.content.Context;

public interface SdCheckModel {

    void copyTaskToSdcard(Context context, String filePath);

    void installApk(Context context, String filePath);

    void unZipTaskFile(Context context, String filePath);

    void stopZipTask();
}
