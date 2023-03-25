package com.etv.task.entity;

import org.litepal.crud.LitePalSupport;

/**
 * 非关联素材，文本属性
 */
public class TextInfo extends LitePalSupport {

    /**
     * 向左移动
     */
    public final static int MOVE_LEFT = 0;    //自右向左
    public final static int MOVE_RIGHT = 1;   //自左向右
    public final static int MOVE_UP = 2;      //自下网上

    public final static int POI_LEFT_TOP = 1;     //1：左上角
    public final static int POI_TOP_CENTER = 2;   //2：顶部居中
    public final static int POI_RIGHT_TOP = 3;   //3：右上角
    public final static int POI_LEFT_CENTER = 4;    //4：左对齐居中
    public final static int POI_CENTER = 5;    //5：居中
    public final static int POI_RIGHT_CENTER = 6;    //6：右对齐居中
    public final static int POI_LEFT_BOTTOM = 7;    //7：左下角
    public final static int POI_BOTTOM_CENTER = 8;    //8：底部居中
    public final static int POI_RIGHT_BOTTOM = 9;    //9：右下角

    private String txtid;
    private String taskId;         //任务ID
    private String taCoId;         //控件编号
    private String taContent;      //字体内容
    private String taColor;        //字体颜色
    private String taNo;           //序号
    private String taFontSize;  //字体大小
    private String taMove;      //用来判断字幕运动的方向
    private String taAddress;   //城市地址
    private String taBgColor;    //背景色
    private String taMoveSpeed;  //移动速度  0:静止  1：慢速  2：默认  3:快速
    private String taAlignment;   //对齐方式
    private String taCountDown;   //倒计时控件得秒
    private String taFonType;     //字体
    private int parentCoId;     //关联控件id  -1是普通得控件  有数值得是关联得ID
    private String taBgImage;   //button 背景图
    private String taBgimageSize; //button背景图大小尺寸
    private String pmType;    //节目的类型

    public TextInfo() {

    }

    public TextInfo(String taColor, String taFontSize, String taBgColor) {
        this.taFontSize = taFontSize;
        this.taColor = taColor;
        this.taBgColor = taBgColor;
    }

    public TextInfo(String taskId, String txtid, String taCoId, String taContent, String taMove,
                    int parentCoId, String taBgImage, String pmType, String taBgimageSize) {
        this.taskId = taskId;
        this.txtid = txtid;
        this.taCoId = taCoId;
        this.taContent = taContent;
        this.taMove = taMove;
        this.parentCoId = parentCoId;
        this.taBgImage = taBgImage;
        this.pmType = pmType;
        this.taBgimageSize = taBgimageSize;
    }

    public TextInfo(String taskId, String txtid, String taCoId, String taContent, String taColor, String taNo, String taFontSize,
                    String taMove, String taAddress, String taBgColor, String taMoveSpeed, String taAlignment, String taCountDown,
                    String taFonType, int parentCoId, String taBgImage, String pmType, String taBgimageSize) {
        this.parentCoId = parentCoId;
        this.taskId = taskId;
        this.txtid = txtid;
        this.taCoId = taCoId;
        this.taContent = taContent;
        this.taAddress = taAddress;
        this.taColor = taColor;
        this.taNo = taNo;
        this.taMove = taMove;
        this.taFontSize = taFontSize;
        this.taBgColor = taBgColor;
        this.taMoveSpeed = taMoveSpeed;
        this.taAlignment = taAlignment;
        this.taCountDown = taCountDown;
        this.taFonType = taFonType;
        this.taBgImage = taBgImage;
        this.pmType = pmType;
        this.taBgimageSize = taBgimageSize;
    }

    public String getTaBgimageSize() {
        return taBgimageSize;
    }

    public void setTaBgimageSize(String taBgimageSize) {
        this.taBgimageSize = taBgimageSize;
    }

    public String getPmType() {
        return pmType;
    }

    public void setPmType(String pmType) {
        this.pmType = pmType;
    }

    public String getTaBgImage() {
        return taBgImage;
    }

    public void setTaBgImage(String taBgImage) {
        this.taBgImage = taBgImage;
    }

    public int getParentCoId() {
        return parentCoId;
    }

    public void setParentCoId(int parentCoId) {
        this.parentCoId = parentCoId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaFonType() {
        return taFonType;
    }

    public void setTaFonType(String taFonType) {
        this.taFonType = taFonType;
    }

    public String getTaCountDown() {
        return taCountDown;
    }

    public void setTaCountDown(String taCountDown) {
        this.taCountDown = taCountDown;
    }

    public String getTaAlignment() {
        return taAlignment;
    }

    public void setTaAlignment(String taAlignment) {
        this.taAlignment = taAlignment;
    }

    public String getTaBgColor() {
        return taBgColor;
    }

    public void setTaBgColor(String taBgColor) {
        this.taBgColor = taBgColor;
    }

    public String getTaMoveSpeed() {
        return taMoveSpeed;
    }

    public void setTaMoveSpeed(String taMoveSpeed) {
        this.taMoveSpeed = taMoveSpeed;
    }

    public String getTaAddress() {
        return taAddress;
    }

    public void setTaAddress(String taAddress) {
        this.taAddress = taAddress;
    }

    public String getTaMove() {
        return taMove;
    }

    public void setTaMove(String taMove) {
        this.taMove = taMove;
    }

    public void setTxtId(String txtid) {
        this.txtid = txtid;
    }

    public void setTaCoId(String taCoId) {
        this.taCoId = taCoId;
    }

    public void setTaContent(String taContent) {
        this.taContent = taContent;
    }

    public void setTaColor(String taColor) {
        this.taColor = taColor;
    }

    public void setTaNo(String taNo) {
        this.taNo = taNo;
    }

    public void setTaFontSize(String taFontSize) {
        this.taFontSize = taFontSize;
    }


    public String getTxtId() {
        return txtid;
    }

    public String getTaCoId() {
        return taCoId;
    }

    public String getTaContent() {
        return taContent;
    }

    public String getTaColor() {
        return taColor;
    }

    public String getTaNo() {
        return taNo;
    }

    public String getTaFontSize() {
        return taFontSize;
    }

    @Override
    public String toString() {
        return "TextInfo{" +
                "txtid='" + txtid + '\'' +
                ", taCoId='" + taCoId + '\'' +
                ", taContent='" + taContent + '\'' +
                ", taColor='" + taColor + '\'' +
                ", taNo='" + taNo + '\'' +
                ", taFontSize='" + taFontSize + '\'' +
                ", taMove='" + taMove + '\'' +
                ", taAddress='" + taAddress + '\'' +
                ", taBgColor='" + taBgColor + '\'' +
                ", taMoveSpeed='" + taMoveSpeed + '\'' +
                ", taAlignment='" + taAlignment + '\'' +
                ", taCountDown='" + taCountDown + '\'' +
                ", taFont='" + taFonType + '\'' +
                ", 素材关联类型='" + parentCoId + '\'' +
                ", taBgImage='" + taBgImage + '\'' +
                ", taBgImageSize='" + taBgimageSize + '\'' +
                '}';
    }
}
