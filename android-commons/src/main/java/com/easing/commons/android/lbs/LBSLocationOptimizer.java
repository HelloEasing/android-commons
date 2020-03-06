package com.easing.commons.android.lbs;

import com.easing.commons.android.format.Texts;

import java.util.LinkedList;

//优化坐标，去除不连续的坐标
public class LBSLocationOptimizer {

    //校验坐标数量，网络切换时，信号波动，忽略前三个定位结果
    volatile public static int locationCount = 0;

    //坐标连续性校验，连续出现3个紧凑且不同的坐标，则视为正常坐标
    static final LinkedList<LBSLocation> validateArray = new LinkedList();

    //坐标所属轨迹段
    static String locationPeriod = "";

    static final double referDistance = 35;

    //重新计算坐标数量
    //在网络切换时，需要调用这个方法
    public static void revalidateLocationCount() {
        LBSLocationOptimizer.locationCount = 0;
        BDLocationManager.optimizedLocation = null;
    }

    //校验坐标数量是否充分
    public static boolean validateLocationCount() {
        ++LBSLocationOptimizer.locationCount;
        if (LBSLocationOptimizer.locationCount > 3)
            LBSLocationOptimizer.locationCount = 4;
        return LBSLocationOptimizer.locationCount > 3;
    }

    //校验坐标连续性
    //如果连续出现3个紧凑的坐标，则视为正常坐标
    //首次符合该条件的3个坐标，缓存在校验数组中，作为新坐标的参考值
    //5秒内如果没有正常坐标出现，则清空校验数组，重新计算参考值，并更新period
    //period表示坐标属于不连续的轨迹段，在绘制轨迹时，不同period应当分开绘制
    synchronized public static boolean validateLocationContinuity(LBSLocation location) {
        //数值很久未更新，则重新计算参考值
        if (validateArray.size() > 0 && location.timeMillis - validateArray.getLast().timeMillis > 5000) {
            LBSLocationOptimizer.locationPeriod = Texts.random(false, false);
            validateArray.clear();
            validateArray.add(location);
            return false;
        }

        //位置距离过大，则跳过
        //20米可有效防止坐标抖动，但可能舍弃过多坐标，信号不好时线段会断断续续
        if (validateArray.size() > 0)
            if (LBSLocationHandler.distance(location.latitude, location.longitude, validateArray.getLast().latitude, validateArray.getLast().longitude) > referDistance)
                return false;

        //坐标相同，不上传，只更新时间
        if (validateArray.size() > 0)
            if (LBSLocationHandler.distance(location.latitude, location.longitude, validateArray.getLast().latitude, validateArray.getLast().longitude) < 0.1) {
                validateArray.getLast().timeMillis = location.timeMillis;
                return false;
            }

        //已满，则更新校验数组
        if (validateArray.size() == 3) {
            validateArray.removeFirst();
            validateArray.add(location);
            location.period = LBSLocationOptimizer.locationPeriod;
            return true;
        }

        //未满，直接加入
        validateArray.add(location);
        return false;
    }

    //同时校验充分性和连续性
    public static LBSLocation validate(LBSLocation location) {
        if (!LBSLocationOptimizer.validateLocationCount())
            return null;
        if (!LBSLocationOptimizer.validateLocationContinuity(location))
            return null;
        return location;
    }


}

