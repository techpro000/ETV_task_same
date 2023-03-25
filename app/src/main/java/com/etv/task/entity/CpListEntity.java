package com.etv.task.entity;

import org.litepal.crud.LitePalSupport;

import java.util.List;

/**
 * 控件属性信息--
 */
public class CpListEntity extends LitePalSupport {

    /**
     * id : 1
     * coPmId : 1
     * coType : img
     * coState : 1
     * coLeftPosition : 0
     * coRightPosition : 0
     * coWidth : 492
     * coHeight : 492
     * mpList : [{"playParam":"5","mid":10,"url":"upload/srcImgPath\\test2.png"}]
     * txList : null
     */

    private String cpid;                       //控件编号
    private String coScId;                     //场景编号
    private String coType;                     //控件类型
    private String coLeftPosition;             //左边坐标
    private String coRightPosition;            //顶端坐标
    private String coWidth;                    //控件的宽度
    private String coHeight;                   //控件的高度
    private String coActionType;               //互动类型
    private String coLinkAction;               //互动行为--场景序号
    private String coLinkId;                   //互动跳转场景的 ID
    private String coScreenProtectTime;        //屏保时间
    private List<TextInfo> txList;             //文本属性
    private List<MpListEntity> mpList;         //资源属性
    private int pmResolutionType;              // 屏幕类型  1：分辨率  2：自适应  3：4K
    private int pmFixedScreen;                 // 1横屏 2 竖屏

    public static final int SCREEN_TYPE_DPI = 1;  //1:分辨率
    public static final int SCREEN_TYPE_AUTO_SCREEN = 2;  //2：自适应
    public static final int SCREEN_TYPE_4K_SHOW = 3;  // 3:4K

    public static final int FIX_SCREEN_HRO = 1;  //1:横屏
    public static final int FIX_SCREEN_VER = 2;  //2竖屏


    public CpListEntity() {
    }

    public CpListEntity(String cpid, String coScId, String coType, String coLeftPosition,
                        String coRightPosition, String coWidth, String coHeight, String coActionType, String coLinkAction,
                        String coScreenProtectTime, int pmResolutionType, int pmFixedScreen, String coLinkId) {
        this.cpid = cpid;
        this.coScId = coScId;
        this.coType = coType;
        this.coLeftPosition = coLeftPosition;
        this.coRightPosition = coRightPosition;
        this.coWidth = coWidth;
        this.coHeight = coHeight;
        this.coActionType = coActionType;
        this.coLinkAction = coLinkAction;
        this.coScreenProtectTime = coScreenProtectTime;
        this.pmResolutionType = pmResolutionType;
        this.pmFixedScreen = pmFixedScreen;
        this.coLinkId = coLinkId;
    }

    public String getCoLinkId() {
        return coLinkId;
    }

    public void setCoLinkId(String coLinkId) {
        this.coLinkId = coLinkId;
    }

    public int getPmResolutionType() {
        return pmResolutionType;
    }

    public void setPmResolutionType(int pmResolutionType) {
        this.pmResolutionType = pmResolutionType;
    }

    public int getPmFixedScreen() {
        return pmFixedScreen;
    }

    public void setPmFixedScreen(int pmFixedScreen) {
        this.pmFixedScreen = pmFixedScreen;
    }

    public String getCoScreenProtectTime() {
        return coScreenProtectTime;
    }

    public void setCoScreenProtectTime(String coScreenProtectTime) {
        this.coScreenProtectTime = coScreenProtectTime;
    }

    public String getCoActionType() {
        return coActionType;
    }

    public void setCoActionType(String coActionType) {
        this.coActionType = coActionType;
    }

    public String getCoLinkAction() {
        return coLinkAction;
    }

    public void setCoLinkAction(String coLinkAction) {
        this.coLinkAction = coLinkAction;
    }

    public String getCpidId() {
        return cpid;
    }

    public void setCpId(String id) {
        this.cpid = id;
    }

    public String getCoScId() {
        return coScId;
    }

    public void setCoScId(String coScId) {
        this.coScId = coScId;
    }

    public String getCoType() {
        return coType;
    }

    public void setCoType(String coType) {
        this.coType = coType;
    }


    public String getCoLeftPosition() {
        return coLeftPosition;
    }

    public void setCoLeftPosition(String coLeftPosition) {
        this.coLeftPosition = coLeftPosition;
    }

    public String getCoRightPosition() {
        return coRightPosition;
    }

    public void setCoRightPosition(String coRightPosition) {
        this.coRightPosition = coRightPosition;
    }

    public String getCoWidth() {
        return coWidth;
    }

    public void setCoWidth(String coWidth) {
        this.coWidth = coWidth;
    }

    public String getCoHeight() {
        return coHeight;
    }

    public void setCoHeight(String coHeight) {
        this.coHeight = coHeight;
    }

    public List<TextInfo> getTxList() {
        return txList;
    }

    public void setTxList(List<TextInfo> txList) {
        this.txList = txList;
    }

    public List<MpListEntity> getMpList() {
        return mpList;
    }

    public void setMpList(List<MpListEntity> mpList) {
        this.mpList = mpList;
    }

    public String getScreenSize() {
        return "coLeftPosition='" + coLeftPosition + '\'' +
                ", coRightPosition='" + coRightPosition + '\'' +
                ", coWidth='" + coWidth + '\'' +
                ", coHeight='" + coHeight;
    }

    @Override
    public String toString() {
        return "CpListEntity{" +
                "cpid='" + cpid + '\'' +
                ", coScId='" + coScId + '\'' +
                ", coType='" + coType + '\'' +
                ", coLeftPosition='" + coLeftPosition + '\'' +
                ", coRightPosition='" + coRightPosition + '\'' +
                ", coWidth='" + coWidth + '\'' +
                ", coHeight='" + coHeight + '\'' +
                ", coActionType='" + coActionType + '\'' +
                ", coLinkAction='" + coLinkAction + '\'' +
                ", coScreenProtectTime='" + coScreenProtectTime + '\'' +
                '}';
    }
}
