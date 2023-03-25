package com.etv.db;

import android.content.ContentValues;

import com.etv.config.AppConfig;
import com.etv.config.AppInfo;
import com.etv.entity.StatisticsEntity;
import com.etv.service.EtvService;
import com.etv.util.MyLog;
import com.etv.util.SharedPerManager;

import org.litepal.LitePal;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/***
 * 统计记录表
 */
public class DbStatiscs {

    /**
     * 保存控件数据库
     *
     * @param entity
     * @param tag
     * @return
     */
    public static boolean saveStatiseToLocal(StatisticsEntity entity, String tag) {
//        MyLog.db("=========添加统计到数据库=============");
        boolean playUpdate = SharedPerManager.getPlayTotalUpdate();
        if (!playUpdate) {
            clearDbStatiesAllData();
            return false;
        }
        boolean isAddBack = false;
        if (entity == null) {
            return isAddBack;
        }
        String stMtId = entity.getMtid().trim();
        if (stMtId == null || stMtId.contains("null") || stMtId.length() < 5) {
            return false;
        }
        try {
            List<StatisticsEntity> cpList = LitePal.where("mtid=?", stMtId + "").find(StatisticsEntity.class);
            if (cpList == null || cpList.size() < 1) {
                isAddBack = addStaInfoToDb(entity);
                return isAddBack;
            }
            StatisticsEntity statisticsEntity = cpList.get(0);
            isAddBack = modifyStatInfoDb(entity, statisticsEntity);
            return isAddBack;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isAddBack;
    }

    /**
     * 修改统计数据
     *
     * @return
     */
    private static boolean modifyStatInfoDb(StatisticsEntity newEntity, StatisticsEntity oldEntity) {
        if (newEntity == null) {
            return false;
        }
        String stMtId = oldEntity.getMtid();
        String addType = oldEntity.getAddtype();

        int oldpmTime = oldEntity.getPmtime();
        int oldcount = oldEntity.getCount();

        int newpmTime = newEntity.getPmtime();
        int newcount = newEntity.getCount();
        oldpmTime = oldpmTime + newpmTime;
        oldcount = oldcount + newcount;

        ContentValues values = new ContentValues();
        values.put("addType", addType);
        values.put("pmTime", oldpmTime);
        values.put("count", oldcount);
        int modifyNum = LitePal.updateAll(StatisticsEntity.class, values, "mtid=?", stMtId);
        if (modifyNum > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 保存数据到数据库
     *
     * @param entity
     * @return
     */
    private static boolean addStaInfoToDb(StatisticsEntity entity) {

        boolean isSave = false;
        if (entity == null) {
            return isSave;
        }
        try {
            isSave = entity.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSave;
    }

    /**
     * 获取统计列表
     *
     * @return
     */
    public static List<StatisticsEntity> getStaInfoList() {

        List<StatisticsEntity> txtList = null;
        try {
            txtList = LitePal.findAll(StatisticsEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return txtList;
    }

    /**
     * 获取最后一个数据，提交上传
     *
     * @return
     */
    public static StatisticsEntity getLastUpdateStaEntity() {

        List<StatisticsEntity> list = getStaInfoList();
        if (list == null || list.size() < 1) {
            return null;
        }
        Collections.sort(list, new Comparator<StatisticsEntity>() {
            @Override
            public int compare(StatisticsEntity statisticsEntity1, StatisticsEntity statisticsEntity2) {
                long createTime1 = statisticsEntity1.getCreatetime();
                long createTime2 = statisticsEntity2.getCreatetime();
                return (int) (createTime2 - createTime1);
            }
        });
        return list.get(list.size() - 1);
    }

    /**
     * 删除用户数据
     *
     * @param statisticsEntity
     * @return
     */
    public static boolean delInfoById(StatisticsEntity statisticsEntity) {
        if (statisticsEntity == null) {
            return true;
        }
        String mtId = statisticsEntity.getMtid();
        return delInfoById(mtId);
    }

    public static boolean delInfoById(String mtid) {
        try {
            int delNum = LitePal.deleteAll(StatisticsEntity.class, "mtid = ?", mtid);
            if (delNum < 1) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 清理null数据
     *
     * @return
     */
    public static boolean clearNullData() {
        List<StatisticsEntity> list = getStaInfoList();
        if (list == null || list.size() < 1) {
            return true;
        }
        for (StatisticsEntity statisticsEntity : list) {
            if (statisticsEntity == null) {
                clearDbStatiesAllData();
                break;
            }
            delInfoById("");
            delInfoById("null");
        }
        return false;
    }


    public static boolean clearDbStatiesAllData() {
        int statues = LitePal.deleteAll(StatisticsEntity.class);
        if (statues > 0) {
            return true;
        }
        return false;
    }

}
