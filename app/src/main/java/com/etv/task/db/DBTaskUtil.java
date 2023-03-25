package com.etv.task.db;

import android.content.ContentValues;
import android.text.TextUtils;

import com.etv.config.ApiInfo;
import com.etv.config.AppInfo;
import com.etv.task.entity.CpListEntity;
import com.etv.task.entity.MpListEntity;
import com.etv.task.entity.PmListEntity;
import com.etv.task.entity.SceneEntity;
import com.etv.task.entity.TaskWorkEntity;
import com.etv.task.entity.TextInfo;
import com.etv.util.MyLog;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class DBTaskUtil {

    //=================================================================================================
    public static boolean saveTxtInfo(TextInfo entity) {
        boolean isSave = false;
        try {
            if (entity == null) {
                return false;
            }
            isSave = entity.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSave;
    }

    /***
     * 获取所有得信息
     * @return
     */
    public static List<TextInfo> getTxtListInfoAll() {
        return getTxtParsentListInfoById(null, 0);
    }

    /***
     * 获取普通素材字体相关信息
     * @param taCoId   控件ID
     * @return
     */
    public static List<TextInfo> getTxtListInfoById(String taCoId) {
        return getTxtParsentListInfoById(taCoId, 0);
    }

    /***
     * 获取关联素材
     * @param taCoId
     * -1  默认素材
     * 1： 关联素材
     * 0： 查询控件所有素材
     * @return
     */
    public static List<TextInfo> getTxtParsentListInfoById(String taCoId, int parentId) {
        List<TextInfo> txtList = null;
        try {
            if (taCoId == null || taCoId.length() < 1) {
                txtList = LitePal.findAll(TextInfo.class);
            } else {
                if (parentId == 0) {
                    txtList = LitePal.where("taCoId=?", taCoId + "").find(TextInfo.class);
                } else {
                    txtList = LitePal.where("taCoId=? and parentCoId=? ", taCoId + "", parentId + "").find(TextInfo.class);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return txtList;
    }

    /***================================================================================================
     * 保存素材到数据库
     * @param entity
     * @return
     */
    public static boolean saveMpInfo(MpListEntity entity) {
        if (entity == null) {
            return false;
        }
        boolean isSave = false;
        try {
            isSave = entity.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSave;
    }

    /***
     * 根据控件获取素材信息
     * @param cpId
     *    控件 ID
     * @param parentCoId
     *   是否关联控件   -1普通素材，其他表示关联ID
     * @return
     */
    public static final int MP_DEFAULT = -1;
    public static final int MP_RELATION = 1;

    public static List<MpListEntity> getMpListInfoById(String cpId, int parentCoId, String printTag) {
        MyLog.task("====ParentCoId=000=====" + parentCoId + " / " + printTag);
        List<MpListEntity> listBack = null;
        try {
            List<MpListEntity> mpList = LitePal.where("cpId=?", cpId).find(MpListEntity.class);
            if (mpList == null || mpList.size() < 1) {
                return null;
            }
            listBack = new ArrayList<MpListEntity>();
            for (int i = 0; i < mpList.size(); i++) {
                MpListEntity mpListEntity = mpList.get(i);
                if (mpListEntity == null) {
                    continue;
                }
                String ParentCoId = mpListEntity.getParentCoId();
                if (ParentCoId == null || ParentCoId.contains("null") || TextUtils.isEmpty(ParentCoId)) {
                    continue;
                }
                int perId = Integer.parseInt(ParentCoId);
                if (perId == parentCoId) {
                    listBack.add(mpListEntity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (listBack == null || listBack.size() < 1) {
            return null;
        }
        return listBack;
    }


    /**
     * 获取所有得需要下载得素材
     *
     * @return
     */
    public static List<MpListEntity> getMpListInfoAll() {
        List<MpListEntity> mpList = null;
        try {
            mpList = LitePal.findAll(MpListEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mpList == null) {
            mpList = new ArrayList<MpListEntity>();
        }
        //添加背景素材
        List<SceneEntity> sceneEntities = getSencenListInfoAll();
        if (sceneEntities != null && sceneEntities.size() > 0) {
            for (int i = 0; i < sceneEntities.size(); i++) {
                SceneEntity sceneEntity = sceneEntities.get(i);
                String bggImageSize = sceneEntity.getScBackimgSize();
                String imagePath = sceneEntity.getScBackImg();
                String pmType = sceneEntity.getPmType();
                //场景截图
                if (imagePath != null && imagePath.length() > 5) {
                    mpList.add(new MpListEntity(sceneEntity.getTaskid(), sceneEntity.getProgramId(), "-1",
                            imagePath, "-1", "-1", bggImageSize, "-1", pmType, "-1", AppInfo.VIEW_IMAGE));
                }
            }
        }
        return mpList;
    }


    /***
     * 这里用来比对过滤删除文件的
     * @return
     */
    public static List<MpListEntity> getTaskListInfoAll() {
        List<MpListEntity> mpList = getMpListInfoAll();
        return mpList;
    }


    //=================================================================================================

    /***
     * 保存控件数据库
     * @param entity
     * @return
     */
    public static boolean saveCpEntity(CpListEntity entity) {
        if (entity == null) {
            return false;
        }
        String coScId = entity.getCoScId();
        String cpId = entity.getCpidId();
        List<CpListEntity> cpList = LitePal.where("coScId=? and cpId=?", coScId, cpId).find(CpListEntity.class);
        if (cpList == null || cpList.size() < 1) {
            return addCoPoInfo(entity);
        } else { //有数据，去修改数据
            return modifyCoPoInfo(entity);
        }
    }

    /***
     * 修改控件信息
     * @param entity
     * @return
     */
    private static boolean modifyCoPoInfo(CpListEntity entity) {
        if (entity == null) {
            return false;
        }
        String coType = entity.getCoType();             //控件类型
        String coLeftPosition = entity.getCoLeftPosition();      //左边坐标
        String coRightPosition = entity.getCoRightPosition();    //顶端坐标
        String coWidth = entity.getCoWidth();                    //控件的宽度
        String coHeight = entity.getCoHeight();                  //控件的高度
        String cpid = entity.getCpidId();                        //控件编号
        String coScId = entity.getCoScId();                      //场景编号
        String coActionType = entity.getCoActionType();       //互动类型
        String coLinkAction = entity.getCoLinkAction();            //互动行为
        String coScreenProtectTime = entity.getCoScreenProtectTime();
        int pmResolutionType = entity.getPmResolutionType();          // 屏幕类型  1：分辨率  2：自适应  3：4K
        int pmFixedScreen = entity.getPmFixedScreen();             // 1横屏 2 竖屏
        String coLinkId = entity.getCoLinkId();
        ContentValues values = new ContentValues();
        values.put("coType", coType);
        values.put("pmResolutionType", pmResolutionType);
        values.put("pmFixedScreen", pmFixedScreen);
        values.put("coLeftPosition", coLeftPosition);
        values.put("coRightPosition", coRightPosition);
        values.put("coWidth", coWidth);
        values.put("coHeight", coHeight);
        values.put("coActionType", coActionType);
        values.put("coLinkAction", coLinkAction);
        values.put("coScreenProtectTime", coScreenProtectTime);
        values.put("coLinkId", coLinkId);
        int modifyNum = LitePal.updateAll(CpListEntity.class, values, "cpid=? and coScId=?", cpid, coScId);
        if (modifyNum > 0) {
            return true;
        } else {
            return false;
        }
    }

    /***
     * 增加控件属性
     * @param entity
     * @return
     */
    private static boolean addCoPoInfo(CpListEntity entity) {
        if (entity == null) {
            return false;
        }
        boolean isSave = entity.save();
        return isSave;
    }

    /***
     *从场景中获取控件的数据
     * @param coScId
     * @return
     */
    public static List<CpListEntity> getCoPInfoListByProId(String coScId) {
        List<CpListEntity> cpList = null;
        try {
            cpList = LitePal.where("coScId=?", coScId + "").find(CpListEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cpList;
    }


    //===============节目分割线=================================================================================================

    /***
     * 保存节目数据
     * @param entity
     * @return
     */
    public static boolean saveProjectorEntity(PmListEntity entity) {
        if (entity == null) {
            return false;
        }
        boolean isSave = entity.save();
        return isSave;

    }

    /***
     * 查询节目信息根据任务ID
     * @return
     * @param taskid
     * 如果》0 表示查询任务ID的数据
     */
    public static List<PmListEntity> getPeojectorInfoListByTaskId(String taskid) {
        if (TextUtils.isEmpty(taskid)) {
            return null;
        }
        List<PmListEntity> pmListEntities = null;
        try {
            pmListEntities = LitePal.where("taskid=?", taskid + "").find(PmListEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pmListEntities;
    }

    //================================================================================================================

    /**
     * 保存场景
     *
     * @param sceneEntity
     */
    public static boolean saveSenceEntity(SceneEntity sceneEntity) {
        if (sceneEntity == null) {
            return false;
        }
        String senceid = sceneEntity.getSenceId();     //场景ID
        String programid = sceneEntity.getProgramId(); //节目Id
        List<SceneEntity> scenList = LitePal.where("senceid=? and programid=?", senceid, programid).find(SceneEntity.class);
        if (scenList == null || scenList.size() < 1) {
            return addScenToDb(sceneEntity);
        } else {
            return modifySencenEntity(sceneEntity);
        }
    }

    /**
     * 增加场景到数据据
     *
     * @param sceneEntity
     * @return
     */
    private static boolean addScenToDb(SceneEntity sceneEntity) {
        boolean isSave = false;
        if (sceneEntity == null) {
            return isSave;
        }
        try {
            isSave = sceneEntity.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSave;
    }

    /**
     * 修改场景数据
     *
     * @param sceneEntity
     * @return
     */
    private static boolean modifySencenEntity(SceneEntity sceneEntity) {
        if (sceneEntity == null) {
            return false;
        }
        MyLog.db("====修改场景===" + sceneEntity.toString());
        String senceid = sceneEntity.getSenceId();      //场景ID
        String programid = sceneEntity.getProgramId();  //节目ID
        String taskid = sceneEntity.getTaskid();
        String scBackImg = sceneEntity.getScBackImg();    //背景图地址
        String scBackimgSize = sceneEntity.getScBackimgSize(); //背景图大小
        String pmType = sceneEntity.getPmType();                //节目类型  普通，互动
        String displayPos = sceneEntity.getDisplayPos();             //展示的位置
        String etLevel = sceneEntity.getEtLevel();
        String scTime = sceneEntity.getScTime();
        ContentValues values = new ContentValues();
        values.put("pmType", pmType);
        values.put("displayPos", displayPos);
        values.put("scBackImg", scBackImg);
        values.put("scBackimgSize", scBackimgSize);
        values.put("etLevel", etLevel);
        values.put("taskid", taskid);
        values.put("scTime", scTime);
        int modifyNum = LitePal.updateAll(SceneEntity.class, values, "senceid=? and programid=?", senceid, programid);
        if (modifyNum > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据节目ID来查询场景
     *
     * @param programid
     * @return
     */
    public static List<SceneEntity> getSencenByProGramId(String programid) {
        List<SceneEntity> sencenList = null;
        try {
            sencenList = LitePal.where("programid=?", programid).find(SceneEntity.class);
            if (sencenList == null) {
                MyLog.db("====获取的场景weinull");
            } else {
                MyLog.db("====获取的场景====" + sencenList.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sencenList;
    }

    /***
     * 根据场景ID获取场景信息
     * @param senceid
     * @return
     */
    public static SceneEntity getSencenBySenceid(String senceid) {
        List<SceneEntity> sencenList = null;
        try {
            sencenList = LitePal.where("senceid=?", senceid).find(SceneEntity.class);
            if (sencenList == null) {
                MyLog.db("====获取的场景weinull");
            } else {
                MyLog.db("====获取的场景====" + sencenList.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (sencenList == null || sencenList.size() < 1) {
            return null;
        }
        return sencenList.get(0);

    }


    /**
     * 获取插播消息
     * 插播消息只有一个
     *
     * @param taskid
     * @return
     */
    public static SceneEntity getTextInsertByTaskId(String taskid) {
        SceneEntity sceneEntityBack = null;
        try {
            List<SceneEntity> sencenList = LitePal.where("taskid=?", taskid).find(SceneEntity.class);
            if (sencenList == null) {
                return null;
            }
            sceneEntityBack = sencenList.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sceneEntityBack;
    }


    /**
     * 获取所有得场景
     *
     * @return
     */
    public static List<SceneEntity> getSencenListInfoAll() {
        List<SceneEntity> sencenList = null;
        try {
            sencenList = LitePal.findAll(SceneEntity.class);
            if (sencenList == null) {
                MyLog.db("====获取的场景wein==ull");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sencenList;
    }

    public static boolean delSenceByProId(String senceid) {
        try {
            int delSenNum = LitePal.deleteAll(SceneEntity.class, "senceid= ? ", senceid);
            if (delSenNum > 0) {
                MyLog.db("删除场景成功");
                return true;
            } else {
                MyLog.db("删除场景失败");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /***
     * 保存数据到数据库===================================================================================================
     * @param entity
     * @return
     */
    public static boolean saveTaskEntity(TaskWorkEntity entity) {
        if (entity == null) {
            return false;
        }
        boolean isSave = entity.save();
        return isSave;
    }


    /***
     * 删除任务
     * @param taskid
     * @return
     */
    public static boolean delTaskById(String taskid) {
        try {
            int delTask = LitePal.deleteAll(TaskWorkEntity.class, "taskid=?", taskid + "");
            if (delTask < 1) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    /***
     * 删除节目
     * @param proid
     * @return
     */
    public static boolean delProById(String proid) {
        try {
            List<PmListEntity> taskList = LitePal.where("proid=?", proid + "").find(PmListEntity.class);
            if (taskList == null || taskList.size() < 1) {
                return true;
            }
            int delNum = LitePal.deleteAll(PmListEntity.class, "proid = ?", proid + "");
            if (delNum < 1) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /***
     * 查询任务列表
     * @return
     */
    public static List<TaskWorkEntity> getTaskInfoList() {
        List<TaskWorkEntity> taskList = null;
        try {
            taskList = LitePal.findAll(TaskWorkEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return taskList;
    }

    /***
     * 清理所有的数据库信息
     */
    public static void clearAllDbInfo(String tag) {
        MyLog.db("========清理任务数据路===" + tag, true);
        try {
            LitePal.deleteAll(TaskWorkEntity.class);
            LitePal.deleteAll(PmListEntity.class);
            LitePal.deleteAll(SceneEntity.class);
            LitePal.deleteAll(CpListEntity.class);
            LitePal.deleteAll(MpListEntity.class);
            LitePal.deleteAll(TextInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 根据TaskId 获取 素材信息
     * @param taskId
     * @return
     */
    public static List<MpListEntity> getMpListByTaskId(String taskId) {
        List<MpListEntity> listBack = LitePal.where("taskId=?", taskId).find(MpListEntity.class);
        if (listBack == null || listBack.size() < 1) {
            return null;
        }
        return listBack;
    }
}
