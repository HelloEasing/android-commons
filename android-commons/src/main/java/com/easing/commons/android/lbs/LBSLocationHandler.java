package com.easing.commons.android.lbs;

import com.easing.commons.android.format.Maths;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LBSLocationHandler {


    //求两个坐标间的距离
    public static Double distance(double lat1, double lng1, double lat2, double lng2) {
        //地球半径
        Double R = 6370996.81;
        //转化为墨卡托坐标求距离
        Double x = (lng2 - lng1) * Math.PI * R * Math.cos(((lat1 + lat2) / 2) * Math.PI / 180) / 180;
        Double y = (lat2 - lat1) * Math.PI * R / 180;
        Double distance = Math.hypot(x, y);
        return distance;
    }

    //求两个坐标间的距离
    public static Double distance(LBSLocation p1, LBSLocation p2) {
        return LBSLocationHandler.distance(p1.latitude, p1.longitude, p2.latitude, p2.longitude);
    }

    //计算线段长度
    public static Double length(List<LBSLocation> points) {
        if (points.size() <= 1)
            return 0D;
        double sum = 0D;
        synchronized (points) {
            LBSLocation lastPoint = points.get(0);
            for (int i = 1; i < points.size(); i++) {
                LBSLocation currentPoint = points.get(i);
                Double distance = LBSLocationHandler.distance(lastPoint, currentPoint);
                sum += distance;
                lastPoint = currentPoint;
            }
        }
        return sum;
    }

    //计算线段长度
    public static String formatedLength(List<LBSLocation> locationList) {
        double sum = LBSLocationHandler.length(locationList);
        return Maths.keepFloat(sum, 2) + "米";
    }

    //根据period分割轨迹
    public static List<List<LBSLocation>> group(LinkedList<LBSLocation> trackPoints) {
        List<List<LBSLocation>> tracks = new LinkedList();
        synchronized (trackPoints) {
            List<LBSLocation> points = new ArrayList();
            String period = trackPoints.getFirst().period;
            for (LBSLocation location : trackPoints)
                if (location.period.equalsIgnoreCase(period)) {
                    LBSLocation point = LBSLocation.of(location.latitude, location.longitude);
                    points.add(point);
                    final List<LBSLocation> temp = new ArrayList();
                    temp.addAll(points);
                    if (points.size() >= 3)
                        tracks.add(temp);
                } else {
                    points.clear();
                    period = location.period;
                    LBSLocation point = LBSLocation.of(location.latitude, location.longitude);
                    points.add(point);
                }
        }
        return tracks;
    }


}

