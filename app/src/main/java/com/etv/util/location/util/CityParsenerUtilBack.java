//package com.etv.util.location.util;
//
//import android.content.Context;
//import android.content.res.AssetManager;
//
//import com.etv.util.location.DbProvince;
//import com.etv.util.location.entity.AreaEntity;
//import com.etv.util.location.entity.CityEntity;
//import com.etv.util.location.entity.ProvinceEntity;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//
//
//public class CityParsenerUtilBack {
//
//    Context context;
//
//    public CityParsenerUtilBack(Context context) {
//        this.context = context;
//    }
//
//    public void getProCityInfo() {
//        String desc = getInfoFromAsset();
//        try {
//            JSONArray json = new JSONArray(desc);
//            int lenth = json.length();
//            if (lenth < 1) {
//                return;
//            }
//            for (int i = 0; i < lenth; i++) {
//                String Info = json.get(i).toString();
//                parsenerProvince(Info);
//            }
//        } catch (Exception e) {
//        }
//    }
//
//    /***
//     * 从asset中获取字符串
//     * @return
//     */
//    private String getInfoFromAsset() {
//        StringBuilder stringBuilder = new StringBuilder();
//        try {
//            AssetManager assetManager = context.getAssets();
//            BufferedReader bf = new BufferedReader(new InputStreamReader(
//                    assetManager.open("province.json")));
//            String line;
//            while ((line = bf.readLine()) != null) {
//                stringBuilder.append(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return stringBuilder.toString();
//    }
//
//
//    private void parsenerProvince(String province) {
//        try {
//            JSONObject jsonProvince = new JSONObject(province);
//            String provinceName = jsonProvince.getString("name");
//            String city = jsonProvince.getString("city");
//            boolean isSave = DbProvince.saveProvince(new ProvinceEntity(provinceName));
//            if (isSave) {
//                parsenerCity(provinceName, city);
//            }
//        } catch (Exception e) {
//        }
//    }
//
//    private void parsenerCity(String provinceName, String city) {
//        try {
//            JSONArray cityArray = new JSONArray(city);
//            int cityLength = cityArray.length();
//            if (cityLength < 0) {
//                return;
//            }
//            for (int i = 0; i < cityLength; i++) {
//                String cityInfo = cityArray.get(i).toString();
//                JSONObject jsonCity = new JSONObject(cityInfo);
//                String cityName = jsonCity.getString("name");
//                String area = jsonCity.getString("area");
//                CityEntity entity = new CityEntity(cityName, provinceName);
//                boolean isSave = DbProvince.saveCityToDb(entity);
//                if (isSave) {
//                    parsenerArea(cityName, area);
//                }
//            }
//        } catch (Exception e) {
//        }
//    }
//
//    /***
//     * 解析区域
//     * @param area
//     */
//    private void parsenerArea(String cityName, String area) {
//        try {
//            JSONArray jsonArray = new JSONArray(area);
//            int areaLength = jsonArray.length();
//            if (areaLength < 1) {
//                return;
//            }
//            for (int i = 0; i < areaLength; i++) {
//                String areaName = jsonArray.getString(i).toString();
//                AreaEntity entity = new AreaEntity(areaName, cityName);
//                boolean isSave = DbProvince.saveAreaEntityToDb(entity);
//            }
//        } catch (Exception e) {
//        }
//    }
//}
