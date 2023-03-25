//package com.etv.service.parsener;
//
//import android.content.Context;
//import android.content.Intent;
//
//import com.etv.config.AppConfig;
//import com.etv.config.AppInfo;
//import com.etv.util.Biantai;
//import com.etv.util.MyLog;
//import com.etv.util.NetWorkUtils;
//import com.etv.util.SharedPerManager;
//
//public class MapLocationParsener {
//
//    public static MapLocationParsener instance;
//
//    public static MapLocationParsener getInstance(Context context) {
//        if (instance == null) {
//            synchronized (MapLocationParsener.class) {
//                if (instance == null) {
//                    instance = new MapLocationParsener(context);
//                }
//            }
//        }
//        return instance;
//    }
//
//    Context context;
//
//    public MapLocationParsener(Context context) {
//        this.context = context;
//    }
//
//    /**
//     * @param tag -1 如果是设置界面过来了，还是需要执行，
//     * 1 如果是其他界面得就拦截
//     */
//    int locationAdd = 0;
//
//    public void startLocationService(int tag) {
//        locationAdd++;
//        if (locationAdd > 10) {
//            MyLog.location("====执行定位方法超过十次，不继续定位了==");
//            return;
//        }
//        try {
//            if (tag < 0) {
//                startToLocationView();
//                return;
//            }
//            MyLog.location("====startLocationService==" + tag);
//            if (AppInfo.isLocationSuccess) {
//                //这里有个标记，如果本次开机有成功定位过，就不往下执行了
//                MyLog.location("本次开机已经定位成功了，不需要重复定位==");
//                return;
//            }
//            startToLocationView();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void startToLocationView() {
//        if (context == null) {
//            return;
//        }
//        boolean isAutoLocation = SharedPerManager.getAutoLocation();
//        if (isAutoLocation) {  //自动定位
//            startOrStopLocateSwitch(true);
//        } else {
//            MyLog.location("==手动模式，不定位===");
//        }
//    }
//
//    private LocationClient mLocationClient;
//    private MyLocationListener myLocationListener = new MyLocationListener();
//
//    /***
//     * 开始，停止定位
//     * @param isStart
//     */
//    private void startOrStopLocateSwitch(boolean isStart) {
//        MyLog.location("startOrStopLocate==" + isStart);
//        try {
//            if (isStart && Biantai.isTwoClick()) {
//                return;
//            }
//            boolean isAutoLocation = SharedPerManager.getAutoLocation();
//            MyLog.location("====定位是否自动==" + isAutoLocation);
//            if (!isAutoLocation) {  //手动定位
//                return;
//            }
//            if (!NetWorkUtils.isNetworkConnected(context)) {  //网络关闭
//                return;
//            }
//            if (!isStart) {
//                if (mLocationClient != null) {
//                    mLocationClient.stop();
//                }
//                return;
//            }
//            if (mLocationClient != null && mLocationClient.isStarted()) {
//                mLocationClient.stop();
//            }
//            //初始化LocationClient类
//            mLocationClient = new LocationClient(context);
//            mLocationClient.registerLocationListener(myLocationListener);
//            //配置定位SDK参数
//            LocationClientOption option = new LocationClientOption();
//            //设置精度
//            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//            option.setOpenGps(true); // 打开gps
//            option.setCoorType("bd09ll"); // 设置坐标类型
//            option.setIsNeedAddress(true);
//            option.setScanSpan(AppConfig.BAIDU_MAP_LOCATION);
//            mLocationClient.setLocOption(option);
//            if (isStart) {
//                mLocationClient.start();
//            } else {
//                MyLog.location("=====停止定位功能stop===");
//                mLocationClient.stop();
//            }
//        } catch (Exception e) {
//            MyLog.location("定位异常: " + e.toString());
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 定位SDK监听函数
//     */
//    private class MyLocationListener implements BDLocationListener {
//
//        @Override
//        public void onReceiveLocation(BDLocation location) {
//            try {
//                //销毁后不在处理新接收的位置
//                if (location == null) {
//                    return;
//                }
//                String coutry = location.getCountry();
//                if (coutry == null || coutry.length() < 1) {
//                    MyLog.location("==获取定位失败=");
//                    startOrStopLocateSwitch(false);
//                    return;
//                }
//                if (!coutry.contains("中国")) {
//                    MyLog.location("==获取定位失败=不再中国国土面积===");
//                    startOrStopLocateSwitch(false);
//                    return;
//                }
//                //更新经纬度
//                double mLongitude = location.getLongitude();
//                double mLatitude = location.getLatitude();
//                MyLog.location("==获取得经纬度===" + mLatitude + " / " + mLongitude);
//                SharedPerManager.setmLatitude(mLatitude + "");
//                SharedPerManager.setmLongitude(mLongitude + "");
//                String city = location.getCity();
//                String province = location.getProvince();
//                String area = location.getDistrict();
//                Address addressDetail = location.getAddress();
//                String streenAddress = addressDetail.street + addressDetail.streetNumber;
//                MyLog.location("==获取城市==" + province + " / " + area + " / " + streenAddress);
//                SharedPerManager.setProvince(province);
//                SharedPerManager.setArea(area);
//                SharedPerManager.setDetailAddress(streenAddress);
//
//                AppInfo.isLocationSuccess = true;
//
//                if (city.contains("市")) {
//                    String[] citys = city.split("市");
//                    city = citys[0];
//                    SharedPerManager.setLocalCity(city);
//                    if (city != null || city.length() > 1) {
//                        startOrStopLocateSwitch(false);
//                    }
//                    sendBroadCastToView(AppInfo.BAIDU_LOCATION_BROAD);
//                    MyLog.location("=====保存的定位城市===" + city);
//                }
//            } catch (Exception e) {
//                MyLog.location("定位异常" + e.toString());
//                e.printStackTrace();
//            }
//        }
//    }
//
//
//    /**
//     * 发送广播给
//     *
//     * @param action
//     */
//    private void sendBroadCastToView(String action) {
//        try {
//            Intent intent = new Intent();
//            intent.setAction(action);
//            context.sendBroadcast(intent);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//}
