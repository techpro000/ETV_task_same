package com.etv.task.util;

import android.os.Handler;
import android.text.TextUtils;

import com.etv.config.AppConfig;
import com.etv.config.AppInfo;
import com.etv.service.EtvService;
import com.etv.task.db.DBTaskUtil;
import com.etv.task.entity.CpListEntity;
import com.etv.task.entity.MpListEntity;
import com.etv.task.entity.PmListEntity;
import com.etv.task.entity.SceneEntity;
import com.etv.task.entity.TaskWorkEntity;
import com.etv.task.entity.TextInfo;
import com.etv.task.model.TaskRequestListener;
import com.etv.util.MyLog;
import com.etv.util.SharedPerManager;
import com.etv.util.SimpleDateUtil;

import org.json.JSONArray;
import org.json.JSONObject;

public class ParsenerJsonRunnable implements Runnable {

    private String json;
    private TaskRequestListener taskRequestListener;
    private boolean isParsenerNet;  //是否是网络模式
    private Handler handlerjson = new Handler();

    public ParsenerJsonRunnable(boolean isParsenerNet, String json, TaskRequestListener taskRequestListener) {
        this.json = json;
        this.taskRequestListener = taskRequestListener;
        this.isParsenerNet = isParsenerNet;
    }

    @Override
    public void run() {
        if (isParsenerNet) {  //网络模式节目解析
            if (json == null || json.length() < 8) {
                DBTaskUtil.clearAllDbInfo("网络任务==ParsenerJsonRunnable");
                parserJsonOver("网络任务 = 解析异常,没有相关的 JSON 数据");
                return;
            }
            parsenerTaskEntity(json);
            return;
        }
        //离线单机任务
        if (json == null || json.length() < 8) {
            DBTaskUtil.clearAllDbInfo("单机软件==ParsenerJsonRunnable");
            parserJsonOver("单机软件 = 解析异常,没有相关的 JSON 数据");
            return;
        }
        parsenerSingleTaskEntity(json);
    }

    /***
     * 解析网络任务资源
     * @param data
     */
    public void parsenerTaskEntity(String data) {
        try {
            JSONArray jsonArray = new JSONArray(data);
            int lengthArray = jsonArray.length();
            MyLog.task("=====任务的数据==" + lengthArray);
            DBTaskUtil.clearAllDbInfo("parsenerTaskEntity==000");
            if (lengthArray < 1) {
                finishMySelf("未获取到任务");
                return;
            }
            //如果解析到节目数据，清理数据库，重新保存
            DBTaskUtil.clearAllDbInfo("如果解析到节目数据，清理数据库，重新保存");
            for (int i = 0; i < lengthArray; i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                String taskid = jsonData.getString("id");
                String etName = jsonData.getString("etName");
                String etLevel = jsonData.getString("etLevel"); //1替换 2追加 3插播  4同步  5触发
                String etTaskType = jsonData.getString("etTaskType");  // 1普通节目任务 2双屏异显任务
                String startDate = jsonData.getString("etStartDate");
                String startTime = jsonData.getString("etStartTime");
                String endDate = jsonData.getString("etEndDate");
                String endTime = jsonData.getString("etEndTime");
                String sendTime = jsonData.getString("etSendTime");
                String etMon = jsonData.getString("etMon");
                String etTue = jsonData.getString("etTue");
                String etWed = jsonData.getString("etWed");
                String etThur = jsonData.getString("etThur");
                String etFri = jsonData.getString("etFri");
                String etSat = jsonData.getString("etSat");
                String etSun = jsonData.getString("etSun");
                String streamIdOne = jsonData.optString("streamIdOne");
                String streamIdTwo = jsonData.optString("streamIdTwo");
                int etIsLinkScreeen = 1;   //是否联动
                if (jsonData.toString().contains("etIsLinkScreeen")) {
                    String linkScreen = jsonData.getString("etIsLinkScreeen").trim();
                    if (linkScreen != null && linkScreen.length() > 0) {
                        etIsLinkScreeen = Integer.parseInt(linkScreen);
                    }
                }
                TaskWorkEntity workEntity = new TaskWorkEntity();
                workEntity.setStreamIdOne(streamIdOne);
                workEntity.setStreamIdTwo(streamIdTwo);
                workEntity.setEtIsLinkScreeen(etIsLinkScreeen);
                workEntity.setTaskId(taskid);
                workEntity.setEtName(etName);
                workEntity.setStartDate(startDate);
                workEntity.setEndDate(endDate);
                workEntity.setStartTime(startTime);
                workEntity.setEndTime(endTime);
                workEntity.setEtMon(etMon);
                workEntity.setEtTue(etTue);
                workEntity.setEtWed(etWed);
                workEntity.setEtThur(etThur);
                workEntity.setEtFri(etFri);
                workEntity.setEtSat(etSat);
                workEntity.setEtSun(etSun);
                workEntity.setEtLevel(etLevel);
                workEntity.setEtTaskType(etTaskType);
                workEntity.setSendTime(SimpleDateUtil.StringToLongTime(sendTime));
                //获取任务的结束日期
                long endDateTask = SimpleDateUtil.formatStringtoDate(endDate);
                long endTimeTask = SimpleDateUtil.formatStringTime(endTime);
                long currentDate = SimpleDateUtil.getCurrentDateLong();
                long currentHoMin = SimpleDateUtil.getCurrentHourMinSecond();
                MyLog.task("===解析任务属性=" + workEntity.toString());
                if (endDateTask < currentDate) { //过期的任务
                    MyLog.task("=====当前任务的结束时间在当前时候之前，删除任务");
                    delTaskByIdOrParsenError("111当前任务的结束时间在当前时候之前，删除任务", taskid);
                }
                if (endDateTask == currentDate) {  //当天的任务，就比任务的结束时间
                    if (endTimeTask < currentHoMin) {
                        MyLog.task("=====当前任务的结束时间在当前时候之前，删除任务");
                        delTaskByIdOrParsenError("000当前任务的结束时间在当前时候之前，删除任务", taskid);
                    }
                }
                boolean isSaveTask = DBTaskUtil.saveTaskEntity(workEntity);
                MyLog.task("=====保存任务到数据库状态===" + isSaveTask + " /taskId=" + taskid + " / etLevel = " + etLevel + " /type =  " + workEntity.getEtTaskType());
                if (isSaveTask) {
                    //保存成功并且是普通任务
                    String pmList = jsonData.getString("pmList");
                    MyLog.task("=====节目列表=" + pmList);
                    parsenerPmList(taskid, etLevel, pmList, etIsLinkScreeen);
                }
            }
        } catch (Exception e) {
            MyLog.ExceptionPrint("解析任务error: " + e.toString());
            e.printStackTrace();
        }
        parserJsonOver("任务解析完成--往界面进行回调");
    }

    /***
     * 解析单个任务
     * 如果石是U盘，直接解析单个
     * 如果是网络的，循环便利此方法即可
     * @param data
     */
    public void parsenerSingleTaskEntity(String data) {
        try {
            if (data == null || TextUtils.isEmpty(data)) {
                finishMySelf("JSON异常，解析停止");
                return;
            }
            DBTaskUtil.clearAllDbInfo("parsenerSingleTaskEntity");
            JSONObject jsonData = new JSONObject(data);
            String taskid = jsonData.getString("id");
            String etName = jsonData.getString("etName");
            String etLevel = jsonData.getString("etLevel"); //1替换 2追加 3插播
            String etTaskType = jsonData.getString("etTaskType");  // 1普通节目任务 2双屏异显任务
            String startDate = "2000-01-01";        //开始日期
            String startTime = "00:00:00";          //开始时间
            String endDate = "2100-01-01";          //结束日期
            String endTime = "23:59:59";            //单机时间
            String etMon = jsonData.getString("etMon");
            String etTue = jsonData.getString("etTue");
            String etWed = jsonData.getString("etWed");
            String etThur = jsonData.getString("etThur");
            String etFri = jsonData.getString("etFri");
            String etSat = jsonData.getString("etSat");
            String etSun = jsonData.getString("etSun");
            int etIsLinkScreeen = 1;   //是否联动
            if (jsonData.toString().contains("etIsLinkScreeen")) {
                String linkScreen = jsonData.getString("etIsLinkScreeen").trim();
                if (linkScreen != null && linkScreen.length() > 0) {
                    etIsLinkScreeen = Integer.parseInt(linkScreen);
                }
            }
            TaskWorkEntity workEntity = new TaskWorkEntity();
            workEntity.setEtIsLinkScreeen(etIsLinkScreeen);
            workEntity.setTaskId(taskid);
            workEntity.setEtName(etName);
            workEntity.setStartDate(startDate);
            workEntity.setEndDate(endDate);
            workEntity.setStartTime(startTime);
            workEntity.setEndTime(endTime);
            workEntity.setEtMon(etMon);
            workEntity.setEtTue(etTue);
            workEntity.setEtWed(etWed);
            workEntity.setEtThur(etThur);
            workEntity.setEtFri(etFri);
            workEntity.setEtSat(etSat);
            workEntity.setEtSun(etSun);
            workEntity.setEtLevel(etLevel);
            workEntity.setEtTaskType(etTaskType);
            workEntity.setSendTime(SimpleDateUtil.StringToLongTime("2019-07-01 11:50:03"));
            //获取任务的结束日期
            long endDateTask = SimpleDateUtil.formatStringtoDate(endDate);
            long endTimeTask = SimpleDateUtil.formatStringTime(endTime);
            long currentDate = SimpleDateUtil.getCurrentDateLong();
            long currentHoMin = SimpleDateUtil.getCurrentHourMinSecond();
            MyLog.task("===任务的中止日期=" + endDateTask + " / " + endTimeTask + " /当前时间=" + currentDate + " / " + currentHoMin);
            if (endDateTask < currentDate) { //过期的任务
                MyLog.task("=====当前任务的结束时间在当前时候之前，删除任务");
                delTaskByIdOrParsenError("111当前任务的结束时间在当前时候之前，删除任务", taskid);
            }
            if (endDateTask == currentDate) {
                //当天的任务，就比任务的结束时间
                if (endTimeTask < currentHoMin) {
                    MyLog.task("=====当前任务的结束时间在当前时候之前，删除任务");
                    delTaskByIdOrParsenError("000当前任务的结束时间在当前时候之前，删除任务", taskid);
                }
            }
            boolean isSaveTask = DBTaskUtil.saveTaskEntity(workEntity);
            MyLog.task("=====保存任务到数据库状态000===" + isSaveTask + " /taskId=" + taskid);
            if (isSaveTask) {
                //保存成功并且是普通任务
                String pmList = jsonData.getString("pmList");
                MyLog.task("=====节目列表=" + pmList);
                parsenerPmList(taskid, etLevel, pmList, etIsLinkScreeen);
            } else {
                finishMySelf("任务解析失败");
            }
        } catch (Exception e) {
            MyLog.ExceptionPrint("解析任务error: " + e.toString());
            e.printStackTrace();
        }
        parserJsonOver("parsenerSingleTaskEntity");
    }

    private void parserJsonOver(final String desc) {
        if (taskRequestListener == null) {
            return;
        }
        handlerjson.post(new Runnable() {
            @Override
            public void run() {
                taskRequestListener.parserJsonOver(desc);
            }
        });
    }


    private void finishMySelf(final String desc) {
        if (taskRequestListener == null) {
            return;
        }
        handlerjson.post(new Runnable() {
            @Override
            public void run() {
                taskRequestListener.finishMySelf(desc);
            }
        });
    }


    /***
     * 解析节目
     * @param taskid
     * 任务ID
     * @param pmList
     * 节目信息
     */
    private void parsenerPmList(String taskid, String etLevel, String pmList, int etIsLinkScreeen) {
        try {
            pmList = pmList.trim();
            if (TextUtils.isEmpty(pmList) || pmList.length() < 3) { //获取的数据为null
                delTaskByIdOrParsenError("解析节目获取的数据为null", taskid);
                finishMySelf("当前没有节目");
                return;
            }
            JSONArray jsonArray = new JSONArray(pmList);
            int lengthArray = jsonArray.length();
            if (lengthArray < 1) {
                delTaskByIdOrParsenError("解析的节目数组==null", taskid);
                finishMySelf("当前没有节目");
                return;
            }
            for (int i = 0; i < lengthArray; i++) {
                JSONObject jsonpnlist = jsonArray.getJSONObject(i);
                String proid = jsonpnlist.getString("id");                 //节目ID
                String pmName = jsonpnlist.getString("pmName");            //节目名称
                String pmType = jsonpnlist.getString("pmType");            //节目类型
                if (pmType.contains(AppInfo.PROGRAM_TOUCH)) {
                    MyLog.task("=====任务的数据=当前互动关闭直接跳过任务=");
                    delTaskByIdOrParsenError("当前互动关闭直接跳过任务", taskid);
                    continue;
                }
                String displayPos = jsonpnlist.getString("displayPos");    //显示在那一块屏幕上
                int pmResolutionType = 1;
                if (jsonpnlist.toString().contains("pmResolutionType")) {
                    pmResolutionType = jsonpnlist.getInt("pmResolutionType");
                }
                int pmFixedScreen = 1;
                if (jsonpnlist.toString().contains("pmFixedScreen")) {
                    pmFixedScreen = jsonpnlist.getInt("pmFixedScreen");
                }
                PmListEntity entity = new PmListEntity();
                entity.setProId(proid);
                entity.setPmResolutionType(pmResolutionType);  // 屏幕类型
                entity.setPmFixedScreen(pmFixedScreen);      // 1横屏 2 竖屏
                entity.setTaskid(taskid);
                entity.setPmName(pmName);
                entity.setPmType(pmType);
                entity.setDisplayPos(displayPos);
                MyLog.task("======准备保存节目===" + entity.toString());
                boolean isSavePro = DBTaskUtil.saveProjectorEntity(entity);
                MyLog.task("======保存节目数据 status===" + isSavePro);
                if (isSavePro) {
                    String sceneList = jsonpnlist.getString("sceneList");
                    MyLog.task("======节目保存成功，去解析场景===" + sceneList);
                    parsenerScenecList(proid, taskid, pmType, etLevel, displayPos, sceneList, etIsLinkScreeen, pmResolutionType, pmFixedScreen);
                } else {
                    //如果节目保存失败，删除任务数据库
                    DBTaskUtil.delTaskById(taskid);
                    finishMySelf("节目保存失败");
                }
            }
        } catch (Exception e) {
            MyLog.ExceptionPrint("解析任务error: " + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * 解析场景
     *
     * @param proid     节目ID
     * @param sceneList 场景
     */
    private void parsenerScenecList(String proid, String taskid, String pmType, String etLevel, String displayPos, String sceneList, int etIsLinkScreeen, int pmResolutionType, int pmFixedScreen) {
        MyLog.task("======准备解析场景===" + sceneList);
        try {
            if (sceneList == null || sceneList.length() < 3) {
                finishMySelf("没有场景属性");
                return;
            }
            JSONArray jsonArray = new JSONArray(sceneList);
            int lengthArray = jsonArray.length();
            if (lengthArray < 1) {
                finishMySelf("当前节目没有场景属性，终端操作");
                return;
            }
            for (int i = 0; i < lengthArray; i++) {
                JSONObject jsonSenceList = jsonArray.getJSONObject(i);
                String SenceId = jsonSenceList.getString("id");  //场景 ID
                String scBackImg = jsonSenceList.getString("scBackImg");  //背景图
                //这里解决超哥的个人错误。多了一个
                if (scBackImg.startsWith("/")) {
                    scBackImg = scBackImg.substring(scBackImg.indexOf("/") + 1, scBackImg.length());
                }
                String scBackimgSize = jsonSenceList.getString("scBackimgSize");  //文件大小
                String scTime = "0";
                if (jsonSenceList.toString().contains("scTime")) {
                    scTime = jsonSenceList.getString("scTime");
                    if (scTime == null || scTime.length() < 1) {
                        scTime = "0";
                    }
                }
                long currentTime = System.currentTimeMillis();
                SceneEntity sceneEntity = new SceneEntity(SenceId, proid, taskid, scBackImg, scBackimgSize, pmType, displayPos,
                        etLevel, scTime, etIsLinkScreeen, currentTime);
                MyLog.task("=====保存场景属性===" + sceneEntity.toString());
                boolean isSaveSencen = DBTaskUtil.saveSenceEntity(sceneEntity);
                MyLog.task("=====保存场景属性===" + isSaveSencen);
                if (isSaveSencen && sceneList.contains("componentsList")) {
                    String componentsList = jsonSenceList.getString("componentsList");
                    parsenerCpList(taskid, pmType, SenceId, componentsList, pmResolutionType, pmFixedScreen);
                } else {
                    //如果节目保存失败，删除任务数据库
                    DBTaskUtil.delSenceByProId(SenceId);
                    finishMySelf("节目保存失败");
                }
            }
        } catch (Exception e) {
            MyLog.ExceptionPrint("解析任务error: " + e.toString());
            e.printStackTrace();
        }
    }

    /***
     * 解析控件
     *  @param sencenId
     *  场景ID
     * @param cpList
     * 控件集合JSON
     */
    private void parsenerCpList(String taskid, String pmType, String sencenId, String cpList, int pmResolutionType, int pmFixedScreen) {
        try {
            MyLog.task("=====解析控件======" + cpList);
            if (cpList == null || cpList.length() < 5) {
                delSencenByScenId(sencenId);
                return;
            }
            JSONArray jsonArray = new JSONArray(cpList);
            int lengthArray = jsonArray.length();
            if (lengthArray < 1) {
                delSencenByScenId(sencenId);
                return;
            }
            for (int i = 0; i < lengthArray; i++) {
                JSONObject jsonCplist = jsonArray.getJSONObject(i);
                String cpid = jsonCplist.getString("id");                    //控件编号
                String coType = jsonCplist.getString("coType");             //控件类型
                String coLeftPosition = jsonCplist.getString("coLeftPosition");      //左边坐标
                String coRightPosition = jsonCplist.getString("coRightPosition");     //顶端坐标
                String coWidth = jsonCplist.getString("coWidth");             //控件的宽度
                String coHeight = jsonCplist.getString("coHeight");            //控件的高度
                String coActionType = jsonCplist.getString("coActionType");           //互动类型
                String coLinkAction = jsonCplist.getString("coLinkAction");             //互动行为
                String coScreenProtectTime = jsonCplist.optString("coScreenProtectTime", "0");
                String coLinkId = jsonCplist.optString("coLinkId", "");
                if (coLinkId == null || coLinkId.contains("null")) {
                    coLinkId = "";
                }
                CpListEntity cpListEntity = new CpListEntity(cpid, sencenId, coType, coLeftPosition, coRightPosition, coWidth, coHeight,
                        coActionType, coLinkAction, coScreenProtectTime, pmResolutionType, pmFixedScreen, coLinkId);
                MyLog.task("===保存的控件状态==" + coLinkId + " / " + cpListEntity.toString());
                boolean isSave = DBTaskUtil.saveCpEntity(cpListEntity);
                MyLog.task("===保存控件信息状态==" + isSave + "  / ID=" + cpid + " / " + coActionType);
                if (!isSave) { //保存失败就不往下进行
                    DBTaskUtil.delProById(sencenId);
                    finishMySelf("控件保存数据库失败");
                    return;
                }
                MyLog.task("====当前保存的控件的类型==" + coType);
                if (coType.contains(AppInfo.VIEW_IMAGE)
                        || coType.contains(AppInfo.VIEW_VIDEO)
                        || coType.contains(AppInfo.VIEW_IMAGE_VIDEO)) {
                    MyLog.task("====获取的控件是资源类型的==" + cpListEntity.toString());
                    //文档，图片，音频，视频
                    String mpList = jsonCplist.getString("mpList");          //资源文件
                    parsenerMpList(taskid, pmType, cpid, mpList);
                }
            }
        } catch (Exception e) {
            MyLog.ExceptionPrint("解析任务error: " + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * 当前没有控件
     * 这里清除没有控件的空场景
     *
     * @param sencenId
     */
    private void delSencenByScenId(String sencenId) {
        boolean isDelScen = DBTaskUtil.delSenceByProId(sencenId);
        MyLog.task("============空场景直接删除==" + isDelScen);
        finishMySelf("当前节目没有控件");
    }

    /***
     * 解析文本属性
     * 控件ID
     * @param txList
     */
    private void parsenerTextList(String taskid, String txList, String pmType) {
        if (txList == null || TextUtils.isEmpty(txList) || txList.length() < 5) {
            MyLog.task("==txList=null，终止解析==");
            return;
        }
        try {
            JSONArray jsonArray = new JSONArray(txList);
            int lengthArray = jsonArray.length();
            if (lengthArray < 1) {
                MyLog.task("文本信息里面没有数据");
                return;
            }
            for (int i = 0; i < lengthArray; i++) {
                JSONObject jsonTxtList = jsonArray.getJSONObject(i);
                String txtId = jsonTxtList.getString("id");        //编号
                String taCoId = jsonTxtList.getString("taCoId");   //控件编号
                String taContent = jsonTxtList.getString("taContent");  //文本属性
                String taColor = jsonTxtList.getString("taColor");   //文字颜色
                String taNo = jsonTxtList.getString("taNo");      //序号
                String taFontSize = jsonTxtList.getString("taFontSize");  //字体大小
                String taMove = jsonTxtList.getString("taMove");          //运动方向
                String taBgColor = jsonTxtList.getString("taBgColor");    //背景颜色
                String taMoveSpeed = jsonTxtList.getString("taMoveSpeed");  //运动速度
                String taAddress = jsonTxtList.getString("taAddress");      //天气的地址
                if (taAddress.contains("区")) {
                    taAddress = taAddress.substring(0, taAddress.length() - 1);
                }
                String taAlignment = "5";
                if (jsonTxtList.toString().contains("taAlignment")) {
                    taAlignment = jsonTxtList.getString("taAlignment");      //显示的位置
                }
                String taCountDown = "90";
                if (jsonTxtList.toString().contains("taCountDown")) {
                    taCountDown = jsonTxtList.getString("taCountDown");      //显示的位置
                }
                String taFonType = "0";
                if (jsonTxtList.toString().contains("taFonType")) {
                    taFonType = jsonTxtList.getString("taFonType");      //显示的位置
                }
                MyLog.task("==获取的文本信息==" + taFonType);

                String taBgImage = null;
                if (jsonTxtList.toString().contains("taBgImage")) {
                    taBgImage = jsonTxtList.getString("taBgImage");   //获取按钮得背景图
                }
                String taBgimageSize = "";
                if (jsonTxtList.toString().contains("taBgimageSize")) {
                    taBgimageSize = jsonTxtList.getString("taBgimageSize");   //获取按钮得背景图文件大小
                    if (taBgimageSize == null || taBgimageSize.contains("null")) {
                        taBgimageSize = "";
                    }
                }
                TextInfo textInfo = new TextInfo(taskid, txtId, taCoId, taContent, taColor, taNo, taFontSize, taMove, taAddress, taBgColor,
                        taMoveSpeed, taAlignment, taCountDown, taFonType, DBTaskUtil.MP_DEFAULT, taBgImage, pmType, taBgimageSize);
                MyLog.task("==获取的文本信息==" + textInfo.toString());
                boolean isSaveTxt = DBTaskUtil.saveTxtInfo(textInfo);
                MyLog.task("====保存字体信息数据库==" + isSaveTxt);
            }
        } catch (Exception e) {
            MyLog.ExceptionPrint("解析任务error: " + e.toString());
            e.printStackTrace();
        }
    }

    /***
     * 解析资源文件
     * @param cpid
     * 控件ID
     * @param mpList
     */
    private void parsenerMpList(String taskId, String pmType, String cpid, String mpList) {
        if (mpList == null || TextUtils.isEmpty(mpList) || mpList.length() < 5) {
            MyLog.task("====mpList==null。不操作");
            return;
        }
        MyLog.task("====mpList==" + mpList);
        try {
            JSONArray jsonArray = new JSONArray(mpList);
            int lengthArray = jsonArray.length();
            if (lengthArray < 1) {
                MyLog.task("当前控件没有资源==不用解析");
                return;
            }
            MyLog.task("=====获取当前的资源数组个数===" + lengthArray);
            for (int i = 0; i < lengthArray; i++) {
                JSONObject jsonMplist = jsonArray.getJSONObject(i);
                String playParam = jsonMplist.getString("playParam");  //播放时长
                String mid = jsonMplist.getString("mid");              //素材ID
                String url = jsonMplist.getString("url");              //素材地址
                String size = jsonMplist.getString("size");            //文件大小
                String cartoon = jsonMplist.getString("cartoon");      //动画特效
                String volume = jsonMplist.getString("volume");
                String type = jsonMplist.getString("type");
                if (playParam == null || playParam.length() < 1) {
                    playParam = "10";
                }
                if (url != null && url.length() > 5) {
                    url = url.replace("\\", "/");
                }
                String parentCoId = DBTaskUtil.MP_DEFAULT + "";
                MyLog.task("=====ceshi__log===" + jsonMplist.toString());
                MpListEntity mpListEntity = new MpListEntity(taskId, mid, cpid, url, playParam, cartoon, size, volume, pmType, parentCoId, type);
                boolean isSave = DBTaskUtil.saveMpInfo(mpListEntity);
                MyLog.task("==0000=====保存的素材到数据库===" + isSave + " /cpid= " + cpid + " /数据== " + mpListEntity.toString());
            }
        } catch (Exception e) {
            MyLog.ExceptionPrint("解析任务error: " + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * 删除数据库，提交删除信息给服务器
     *
     * @param taskid
     */
    private void delTaskByIdOrParsenError(final String desc, final String taskid) {
        DBTaskUtil.delTaskById(taskid);
        if (handlerjson == null) {
            return;
        }
        handlerjson.post(new Runnable() {
            @Override
            public void run() {
                EtvService.getInstance().deleteEquipmentTaskById(desc, taskid);
            }
        });
    }
}
