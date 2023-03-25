//package com.etv.task.db;
//
//import com.etv.task.entity.ProjectTimeEntity;
//import com.etv.util.MyLog;
//
//import org.litepal.crud.DataSupport;
//
//import java.util.List;
//
//public class ProjectTimeDb {
//
//    public static boolean addProTimeToDb(ProjectTimeEntity projectTimeEntity) {
//        if (projectTimeEntity == null) {
//            return false;
//        }
//        boolean isSave = false;
//        try {
//            isSave = projectTimeEntity.save();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return isSave;
//    }
//
//
//    public static ProjectTimeEntity getLastProTimeInfo() {
//        List<ProjectTimeEntity> list = queryProTimerList();
//        if (list == null || list.size() < 1) {
//            return null;
//        }
//        ProjectTimeEntity projectTimeEntity = list.get(list.size() - 1);
//        return projectTimeEntity;
//    }
//
//    /**
//     * 删除列表中最后的数据
//     *
//     * @return
//     */
//    public static boolean delLastProTimeInfo() {
//        List<ProjectTimeEntity> list = queryProTimerList();
//        if (list == null || list.size() < 1) {
//            return true;
//        }
//        try {
//            long id = list.get(list.size() - 1).getId();
//            MyLog.cdl("=======查询的数据===删除数据id ==" + id);
//            int delNum = DataSupport.delete(ProjectTimeEntity.class, id);
//            if (delNum > 0) {
//                return true;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    /***
//     * 查询时间数据库
//     * @return
//     */
//    public static List<ProjectTimeEntity> queryProTimerList() {
//        List<ProjectTimeEntity> lists = null;
//        try {
//            lists = DataSupport.findAll(ProjectTimeEntity.class);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return lists;
//    }
//
//    /***
//     * 清理所有的数据库信息
//     */
//    public static void clearAllDbInfo() {
//        try {
//            DataSupport.deleteAll(ProjectTimeEntity.class);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//}
