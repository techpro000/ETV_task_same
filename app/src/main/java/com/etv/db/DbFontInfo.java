//package com.etv.db;
//
//import com.etv.entity.FontEntity;
//
//import org.litepal.LitePal;
//
//import java.util.List;
//
//
///***
// * 字体数据库
// */
//public class DbFontInfo {
//
//    /**
//     * 保存字体到数据库
//     *
//     * @param entity
//     * @return
//     */
//    public static boolean saveFontInfoToLocal(FontEntity entity) {
//        boolean isAddBack = false;
//        if (entity == null) {
//            return isAddBack;
//        }
//        List<FontEntity> fontEntityList = getFontInfoList();
//        if (fontEntityList == null || fontEntityList.size() < 1) {
//            return entity.save();
//        }
//        boolean hasSame = false;
//        String fontNameSane = entity.getFontName();
//        for (FontEntity fontEntity : fontEntityList) {
//            String fontName = fontEntity.getFontName();
//            if (fontNameSane.equals(fontName)) {
//                hasSame = true;
//                break;
//            }
//        }
//        if (hasSame) {
//            return true;
//        }
//        try {
//            isAddBack = entity.save();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return isAddBack;
//    }
//
//    /***
//     * 获取字体集合
//     * @return
//     */
//    public static List<FontEntity> getFontInfoList() {
//        List<FontEntity> fontEntities = null;
//        try {
//            fontEntities = LitePal.findAll(FontEntity.class);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return fontEntities;
//    }
//
//    /***
//     *根据ID获取数据
//     * @param fontId
//     * @return
//     */
//    public static FontEntity getFontInfoById(String fontId) {
//        FontEntity fontEntity = null;
//        try {
//            List<FontEntity> fontEntities = LitePal.where("fontId=?", fontId).find(FontEntity.class);
//            if (fontEntities == null || fontEntities.size() < 1) {
//                return null;
//            }
//            fontEntity = fontEntities.get(0);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return fontEntity;
//    }
//
//    /***
//     * 清库信息
//     */
//    public static void clearAllData() {
//        LitePal.deleteAll(FontEntity.class);
//    }
//
//}
