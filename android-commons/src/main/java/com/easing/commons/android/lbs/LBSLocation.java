package com.easing.commons.android.lbs;

import com.easing.commons.android.format.Maths;
import com.easing.commons.android.time.Times;

import lombok.Data;

@Data
public class LBSLocation {

    public enum Accuracy {
        HIGH("HIGH"), MEDIUM("MEDIUM"), LOW("LOW"), NONE("NONE");

        String description;

        Accuracy(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }

        public boolean equalTo(String accuracy) {
            return description.equalsIgnoreCase(accuracy);
        }
    }

    public double latitude;
    public double longitude;
    public double altitude;
    public String address;
    public String time;
    public String period;

    public String country;
    public String province;
    public String city;
    public String district;
    public String street;
    public String streetNumber;

    public long timeMillis;
    public String accuracy;

    public static LBSLocation of(double latitude, double longitude) {
        LBSLocation location = new LBSLocation();
        location.latitude = latitude;
        location.longitude = longitude;
        location.time = Times.formatDate(location.timeMillis);
        return location;
    }

    public LBSLocation() {
        timeMillis = Times.millisOfNow();
    }

    @Override
    public String toString() {
        if (address == null) address = "无";
        return "经度：" + longitude + "\n纬度：" + latitude + "\n地址：" + address + "\n时间：" + time;
    }

    //返回度分秒式经经度
    public String longitudeToDegree() {
        String orientation = "";
        if (longitude < 0) orientation = "W";
        if (longitude > 0) orientation = "E";
        double value = longitude;
        int deg = (int) value;
        value = value - deg;
        int min = (int) (value * 60);
        value = value - min / 60D;
        String sec = Maths.keepFloat(value * 3600, 2);
        return deg + "°" + min + "′" + sec + "″" + orientation;
    }

    //返回度分秒式经纬度
    public String latitudeToDegree() {
        String orientation = "";
        if (latitude < 0) orientation = "S";
        if (latitude > 0) orientation = "N";
        double value = latitude;
        int deg = (int) value;
        value = value - deg;
        int min = (int) (value * 60);
        value = value - min / 60D;
        String sec = Maths.keepFloat(value * 3600, 2);
        return deg + "°" + min + "′" + sec + "″" + orientation;
    }

}

