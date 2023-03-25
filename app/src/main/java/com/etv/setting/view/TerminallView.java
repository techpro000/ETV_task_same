package com.etv.setting.view;

public interface TerminallView {

    void showWaitDialog(boolean isShow);

    void shotToastView(String toast);

    void queryNickName(boolean isSuccess, String nickName);
}
