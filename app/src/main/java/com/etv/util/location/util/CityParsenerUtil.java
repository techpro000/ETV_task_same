package com.etv.util.location.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;

import com.etv.util.AccessUtil;
import com.etv.util.location.ProCityDialogActivity;
import com.etv.util.location.entity.AreaEntity;
import com.etv.util.location.entity.CityEntity;
import com.etv.util.location.entity.ProvinceEntity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CityParsenerUtil {

    Context context;

    public CityParsenerUtil(Context context) {
        this.context = context;
    }

    public ArrayList<ProvinceEntity> getProCityInfo(Handler handler) {
        ArrayList<ProvinceEntity> proList = new ArrayList<ProvinceEntity>();//省
        try {
            String desc = AccessUtil.getInfoFromAsset(context, "province.json");
            JSONArray json = new JSONArray(desc);
            int lenth = json.length();
            for (int i = 0; i < lenth; i++) {
                String province = json.get(i).toString();
                JSONObject jsonProvince = new JSONObject(province);
                String provinceName = jsonProvince.getString("name");
                String city = jsonProvince.getString("city");
                JSONArray cityArray = new JSONArray(city);
                int cityLength = cityArray.length();
                ArrayList<CityEntity> cityList = new ArrayList<>();
                for (int ci = 0; ci < cityLength; ci++) {
                    String cityInfo = cityArray.get(ci).toString();
                    JSONObject jsonCity = new JSONObject(cityInfo);
                    String cityName = jsonCity.getString("name");
                    String area = jsonCity.getString("area");
                    JSONArray jsonArray = new JSONArray(area);
                    int areaLength = jsonArray.length();
                    ArrayList<AreaEntity> areaList = new ArrayList<>();
                    for (int ar = 0; ar < areaLength; ar++) {
                        String areaName = jsonArray.getString(ar).toString();
                        areaList.add(new AreaEntity(areaName));
                    }
                    cityList.add(new CityEntity(cityName, areaList));
                }
                proList.add(new ProvinceEntity(provinceName, cityList));
            }
            handler.sendEmptyMessage(ProCityDialogActivity.MESSAGE_GET_PRO_OVER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return proList;
    }

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

}
