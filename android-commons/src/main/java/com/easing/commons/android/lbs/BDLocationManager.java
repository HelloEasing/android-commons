package com.easing.commons.android.lbs;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.easing.commons.android.app.CommonApplication;
import com.easing.commons.android.time.Times;

public class BDLocationManager {

    //记录当前位置
    public static LBSLocation currentLocation = null;
    //优化过的坐标
    public static LBSLocation optimizedLocation = null;

    //初始化SDK
    static {
        SDKInitializer.initialize(CommonApplication.ctx);
        SDKInitializer.setCoordType(CoordType.GCJ02);
    }

    //通过百度SDK定时获取坐标
    public static void startLocationClient(OnOriginLocation onOriginLocation, OnOptimizedLocation onOptimizedLocation) {
        LocationClient locationClient = new LocationClient(CommonApplication.ctx);
        LocationClientOption option = new LocationClientOption();
        //设置定位方式
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //设置坐标系
        option.setCoorType("GCJ02");
        //设置定位间隔
        option.setScanSpan(1000);
        //开启地址信息
        option.setIsNeedAddress(true);
        //开启方向信息
        option.setNeedDeviceDirect(true);
        //开启GPS
        option.setOpenGps(true);
        //应用退出时结束进程
        option.setIgnoreKillProcess(false);
        //设置定位选项
        locationClient.setLocOption(option);
        //回调
        locationClient.registerLocationListener(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                //无定位
                if (bdLocation.getLatitude() < 1 && bdLocation.getLongitude() < 1) return;

                //定位错误
                int locateType = bdLocation.getLocType();
                boolean success = locateType == BDLocation.TypeGpsLocation || locateType == BDLocation.TypeNetWorkLocation || locateType == BDLocation.TypeOffLineLocation;
                if (!success) return;

                //定位精度不足
                int accuracy = bdLocation.getGpsAccuracyStatus();
                boolean accurate = accuracy == BDLocation.GPS_ACCURACY_GOOD || accuracy == BDLocation.GPS_ACCURACY_MID || accuracy == BDLocation.GPS_ACCURACY_BAD;

                //记录坐标信息
                LBSLocation location = new LBSLocation();
                location.latitude = bdLocation.getLatitude();
                location.longitude = bdLocation.getLongitude();
                location.altitude = bdLocation.getAltitude();
                location.country = bdLocation.getCountry();
                location.province = bdLocation.getProvince();
                location.city = bdLocation.getCity();
                location.district = bdLocation.getDistrict();
                location.street = bdLocation.getStreet();
                location.streetNumber = bdLocation.getStreetNumber();
                location.address = bdLocation.getAddrStr();
                location.time = Times.now();

                //记录定位精度
                location.accuracy = LBSLocation.Accuracy.NONE.toString();
                if (accuracy == BDLocation.GPS_ACCURACY_GOOD) location.accuracy = LBSLocation.Accuracy.HIGH.toString();
                if (accuracy == BDLocation.GPS_ACCURACY_MID) location.accuracy = LBSLocation.Accuracy.MEDIUM.toString();
                if (accuracy == BDLocation.GPS_ACCURACY_BAD) location.accuracy = LBSLocation.Accuracy.LOW.toString();

                //原始坐标回调
                BDLocationManager.currentLocation = location;
                if (onOriginLocation != null) onOriginLocation.onOriginLocation(location);

                //坐标优化，去除不合理的坐标
                if (accurate && onOptimizedLocation != null && LBSLocationOptimizer.validate(location) != null) {
                    //优化后的坐标回调
                    BDLocationManager.optimizedLocation = location;
                    onOptimizedLocation.onOptimizedLocation(location);
                }

                //应用结束后，自动结束线程
                if (!CommonApplication.isAppAlive()) {
                    locationClient.unRegisterLocationListener(this);
                    locationClient.stop();
                }
            }
        });
        //启动定位程序
        locationClient.start();
        //立刻请求一次坐标
        //解决BUG：APP在繁忙时，比如界面刚创建的时候，client.start请求可能被忽略
        CommonApplication.handler.postDelayed(() -> {
            locationClient.requestLocation();
        }, 4000);
    }

    public interface OnOriginLocation {
        void onOriginLocation(LBSLocation originLocation);
    }

    public interface OnOptimizedLocation {
        void onOptimizedLocation(LBSLocation optimizedLocation);
    }


}
