package com.etv.task.util;

import com.etv.task.entity.MediAddEntity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AbcSortUtil {

    /**
     * 根据命名排序
     *
     * @param fileList
     */
    public static List<MediAddEntity> sortFile(List<MediAddEntity> fileList) {
        if (fileList == null || fileList.size() < 1) {
            return null;
        }
        try {
            Collections.sort(fileList, new Comparator<MediAddEntity>() {
                @Override
                public int compare(MediAddEntity o1, MediAddEntity o2) {
                    String o1Path = o1.getUrl();
                    String o2Path = o2.getUrl();
                    String name1 = o1Path.substring(o1Path.lastIndexOf("/") + 1, o1Path.length());
                    String name2 = o2Path.substring(o2Path.lastIndexOf("/") + 1, o2Path.length());
                    return name1.compareTo(name2);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileList;
    }

}
