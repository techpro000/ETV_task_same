package com.etv.entity;

/***
 * SOCKET 注册数据实体类
 */
public class RegisterEntity {

    String errorDesc;  //错误信息
    byte[] registerInfo;

    public RegisterEntity() {

    }

    public RegisterEntity(String errorDesc, byte[] registerInfo) {
        this.errorDesc = errorDesc;
        this.registerInfo = registerInfo;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }

    public byte[] getRegisterInfo() {
        return registerInfo;
    }

    public void setRegisterInfo(byte[] registerInfo) {
        this.registerInfo = registerInfo;
    }
}
