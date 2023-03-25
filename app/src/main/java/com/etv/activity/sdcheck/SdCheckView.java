package com.etv.activity.sdcheck;


public interface SdCheckView {

    void showToastView(String toast);

    void setThreeClose(String desc);

    void addInfoToList(String desc);

    void writeFileProgress(int progress);

}
