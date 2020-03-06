package com.easing.commons.android.lbs;

public class LBSLocationConvertor {

    private static final double PI = 3.1415926535897932384626;
    private static final double A = 6378245.0;
    private static final double EE = 0.00669342162296594323;

    public static LBSLocation WGS84_GCJ02(LBSLocation sourceLocation) {
        if (outOfChina(sourceLocation)) return sourceLocation;

        double latitude = sourceLocation.latitude;
        double longitude = sourceLocation.longitude;
        double dLat = transformLatitude(longitude - 105.0, latitude - 35.0);
        double dLng = transformLongitude(longitude - 105.0, latitude - 35.0);
        double radLat = latitude / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - EE * magic * magic;
        double sqrtmagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((A * (1 - EE)) / (magic * sqrtmagic) * PI);
        dLng = (dLng * 180.0) / (A / sqrtmagic * Math.cos(radLat) * PI);
        LBSLocation destLocation = new LBSLocation();
        destLocation.latitude = latitude + dLat;
        destLocation.longitude = longitude + dLng;
        destLocation.altitude = sourceLocation.altitude;
        destLocation.address = sourceLocation.address;
        destLocation.time = sourceLocation.time;
        return destLocation;
    }

    public static LBSLocation GCJ02_WGS84(LBSLocation sourceLocation) {
        if (outOfChina(sourceLocation))
            return sourceLocation;
        double latitude = sourceLocation.latitude;
        double longitude = sourceLocation.longitude;
        double dLat = transformLatitude(longitude - 105.0, latitude - 35.0);
        double dLng = transformLongitude(longitude - 105.0, latitude - 35.0);
        double radLat = latitude / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - EE * magic * magic;
        double sqrtmagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((A * (1 - EE)) / (magic * sqrtmagic) * PI);
        dLng = (dLng * 180.0) / (A / sqrtmagic * Math.cos(radLat) * PI);
        LBSLocation destLocation = new LBSLocation();
        destLocation.latitude = latitude - dLat;
        destLocation.longitude = longitude - dLng;
        destLocation.altitude = sourceLocation.altitude;
        destLocation.address = sourceLocation.address;
        destLocation.time = sourceLocation.time;
        return destLocation;
    }

    private static double transformLatitude(double longitude, double latitude) {
        double ret = -100.0 + 2.0 * longitude + 3.0 * latitude + 0.2 * latitude * latitude + 0.1 * longitude * latitude + 0.2 * Math.sqrt(Math.abs(longitude));
        ret += (20.0 * Math.sin(6.0 * longitude * PI) + 20.0 * Math.sin(2.0 * longitude * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(latitude * PI) + 40.0 * Math.sin(latitude / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(latitude / 12.0 * PI) + 320 * Math.sin(latitude * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLongitude(double longitude, double latitude) {
        double ret = 300.0 + longitude + 2.0 * latitude + 0.1 * longitude * longitude + 0.1 * longitude * latitude + 0.1 * Math.sqrt(Math.abs(longitude));
        ret += (20.0 * Math.sin(6.0 * longitude * PI) + 20.0 * Math.sin(2.0 * longitude * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(longitude * PI) + 40.0 * Math.sin(longitude / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(longitude / 12.0 * PI) + 300.0 * Math.sin(longitude / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }

    private static boolean outOfChina(LBSLocation sourceLocation) {
        double latitude = sourceLocation.latitude;
        double longitude = sourceLocation.longitude;
        return !(longitude > 73.66 && longitude < 135.05 && latitude > 3.86 && latitude < 53.55);
    }

}
