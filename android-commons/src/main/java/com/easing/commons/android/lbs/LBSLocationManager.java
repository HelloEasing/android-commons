package com.easing.commons.android.lbs;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.easing.commons.android.app.CommonApplication;
import com.easing.commons.android.code.Console;
import com.easing.commons.android.manager.Services;
import com.easing.commons.android.ui.dialog.TipBox;

import java.util.List;

import lombok.SneakyThrows;

@SuppressWarnings("all")
public class LBSLocationManager {

    public static final String MIX_PROVIDER = "fused";

    @SneakyThrows
    public static void requestLocation(Context ctx, OnLocationGet onLocationGet) {
        LocationManager manager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        LocationListener listener = new LocationListener() {
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }

            @Override
            public void onLocationChanged(Location location) {
                LBSLocation lbsLocation = LBSLocation.of(location.getLatitude(), location.getLongitude());
                double accuracy = location.getAccuracy();
                if (accuracy > 200) return;
                if (accuracy < 5) lbsLocation.accuracy = LBSLocation.Accuracy.HIGH.toString();
                if (accuracy < 20) lbsLocation.accuracy = LBSLocation.Accuracy.MEDIUM.toString();
                if (accuracy < 100) lbsLocation.accuracy = LBSLocation.Accuracy.LOW.toString();
                else lbsLocation.accuracy = LBSLocation.Accuracy.NONE.toString();
                if (onLocationGet != null) onLocationGet.onLocationGet(lbsLocation);
            }
        };
        //每隔一段时间请求一次坐标
        //定时请求接口运行效果不稳定
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Criteria criteria = new Criteria();
                criteria.setHorizontalAccuracy(Criteria.ACCURACY_MEDIUM);
                String bestProvider = manager.getBestProvider(criteria, true);
                manager.requestSingleUpdate(bestProvider, listener, CommonApplication.handler.getLooper());
                CommonApplication.handler.postDelayed(this, 1000);
            }
        };
        r.run();
    }

    public static Location getLocation(Context ctx) {
        LocationManager manager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = manager.getProviders(true);
        if (providers == null || providers.size() == 0) {
            TipBox.tip("请打开GPS和位置服务");
            return null;
        }

        try {
            Location location = manager.getLastKnownLocation(LBSLocationManager.MIX_PROVIDER);
            if (location != null)
                return location;
            location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null)
                return location;
            location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null)
                return location;
            location = manager.getLastKnownLocation(providers.get(0));
            if (location != null)
                return location;
        } catch (Exception e) {
            Console.error(e);
        }

        return null;
    }

    public static boolean isGpsLocateEnable(Context context) {
        LocationManager locationManager = Services.getLocationManager(context);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static boolean isNetLocateEnable(Context context) {
        LocationManager locationManager = Services.getLocationManager(context);
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public interface OnLocationGet {
        void onLocationGet(LBSLocation location);
    }


}
