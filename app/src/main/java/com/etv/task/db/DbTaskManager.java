package com.etv.task.db;


import com.etv.task.entity.CpListEntity;
import com.etv.task.entity.MpListEntity;
import com.etv.task.entity.PmListEntity;
import com.etv.task.entity.SceneEntity;
import com.etv.task.entity.TaskWorkEntity;
import com.etv.task.entity.TextInfo;
import com.etv.task.util.TaskDealUtil;
import com.etv.util.MyLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 用来管理数据库操作的工具类
 */
public class DbTaskManager {

    /**
     * 根据节目获取该节目的场景集合
     *
     * @param taskWorkEntity
     * @return
     */
    public static List<SceneEntity> getSencenEntityFormDbByTask(TaskWorkEntity taskWorkEntity) {
        List<SceneEntity> sceneEntityList = new ArrayList<SceneEntity>();
        //获取任务ID
        String taskId = taskWorkEntity.getTaskId();
        List<PmListEntity> pmListEntities = getPmListFromDb(taskId);
        if (pmListEntities == null || pmListEntities.size() < 1) {
            return null;
        }
        for (int i = 0; i < pmListEntities.size(); i++) {
            List<SceneEntity> listSence = pmListEntities.get(i).getSceneEntityList();
            if (listSence == null || listSence.size() < 1) {
                break;
            }
            for (int j = 0; j < listSence.size(); j++) {
                SceneEntity sceneEntity = listSence.get(j);
                if (sceneEntity != null) {
                    sceneEntityList.add(sceneEntity);
                }
            }
        }
        return sceneEntityList;
    }

    /**
     * 从数据库获取完整的数据
     *
     * @param taskWork
     * @return
     */
    public static TaskWorkEntity getTaskEntityFormDb(TaskWorkEntity taskWork) {
        if (taskWork == null) {
            return null;
        }
        TaskWorkEntity taskWorkEntity = taskWork;
        //获取任务ID
        String taskId = taskWorkEntity.getTaskId();
        List<PmListEntity> pmListEntities = getPmListFromDb(taskId);
        taskWorkEntity.setPmListEntities(pmListEntities);
        return taskWorkEntity;
    }

    /**
     * 从数据库中获取节目数据
     * 目前一个任务只有一个节目
     *
     * @param taskId
     * @return
     */
    private static List<PmListEntity> getPmListFromDb(String taskId) {
        List<PmListEntity> pmListEntities = null;
        try {
            pmListEntities = DBTaskUtil.getPeojectorInfoListByTaskId(taskId);
            if (pmListEntities == null) {
                return null;
            }
            for (int i = 0; i < pmListEntities.size(); i++) {
                PmListEntity pmListEntity = pmListEntities.get(i);
                if (pmListEntity == null) {
                    break;
                }
                String pmId = pmListEntity.getProId();  //拿到节目ID
                List<SceneEntity> sencenList = getSenCeListFromDbByProId(pmId);
                pmListEntity.setSceneEntityList(sencenList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pmListEntities;
    }


    public static SceneEntity getSceneEntityBySeId(String senceid) {
        SceneEntity sceneEntity = DBTaskUtil.getSencenBySenceid(senceid);
        return sceneEntity;
    }


    /**
     * 获取场景信息从节目心里里面获取
     *
     * @param programId
     * @return
     */
    private static List<SceneEntity> getSenCeListFromDbByProId(String programId) {
        List<SceneEntity> listScen = null;
        try {
            listScen = DBTaskUtil.getSencenByProGramId(programId);
            //没有应用场景
            if (listScen == null || listScen.size() < 1) {
                return null;
            }
            for (int i = 0; i < listScen.size(); i++) {
                //遍历场景区获取控件的属性信息
                SceneEntity sceneEntity = listScen.get(i);
                String sencenId = sceneEntity.getSenceId();
                List<CpListEntity> listCp = getComptionFromDbBySenId(sencenId);
                sceneEntity.setListCp(listCp);
            }
            return listScen;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listScen;
    }

    /**
     * 根据taskId获取场景得所有信息
     *
     * @param taskid
     * @return
     */
    public static SceneEntity getTextInsertByTaskId(String taskid) {
        SceneEntity sceneEntityBack = null;
        try {
            sceneEntityBack = DBTaskUtil.getTextInsertByTaskId(taskid);
            //没有应用场景
            if (sceneEntityBack == null) {
                return null;
            }
            String sencenId = sceneEntityBack.getSenceId();
            MyLog.cdl("=========获取得控件信息===sencenId==" + sencenId);
            List<CpListEntity> listCp = getComptionFromDbBySenId(sencenId);
            if (listCp == null || listCp.size() < 1) {
                MyLog.cdl("=========获取得控件信息===null");
            } else {
                MyLog.cdl("=========获取得控件信息===" + listCp.size());
            }
            sceneEntityBack.setListCp(listCp);
            return sceneEntityBack;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sceneEntityBack;
    }

    /**
     * 获取控件信息
     *
     * @param sencenId
     * @return
     */
    public static List<CpListEntity> getComptionFromDbBySenId(String sencenId) {
        List<CpListEntity> cpList = null;
        try {
            cpList = DBTaskUtil.getCoPInfoListByProId(sencenId);
            if (cpList == null || cpList.size() < 1) {
                return null;
            }
            for (int i = 0; i < cpList.size(); i++) {
                CpListEntity cpListEntity = cpList.get(i);
                String cpId = cpListEntity.getCpidId();
                String coType = cpListEntity.getCoType();
                if (TaskDealUtil.isResourceType(coType)) {    //文档，图片，音频，视频
                    MyLog.task("=======解析素材信息==" + coType);
                    List<MpListEntity> mpList = DBTaskUtil.getMpListInfoById(cpId, DBTaskUtil.MP_DEFAULT, "DbTaskManager调用");
                    if (mpList != null && mpList.size() > 0) {
                        cpListEntity.setMpList(mpList);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cpList;
    }


}
