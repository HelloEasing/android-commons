package com.easing.commons.android.format;

import java.text.DecimalFormat;
import java.util.Random;

public class Maths {

    public static String keepInt(Number num, int n) {
        String format = "0";
        for (int i = 1; i < n; i++)
            format = format + "0";
        return new DecimalFormat(format).format(num);
    }

    public static String keepFloat(Number num, int n) {
        String format = "";
        if (n == 0)
            format = "0";
        if (n > 0)
            format = "0.";
        for (int i = 0; i < n; i++)
            format = format + "0";
        return new DecimalFormat(format).format(num);
    }

    public static String formatInteger(int i, String format) {
        format = format == null ? "00" : format;
        return new DecimalFormat(format).format(i);
    }

    //随机生成一个整数
    public static int randomInt() {
        return new Random().nextInt(Integer.MAX_VALUE);
    }

    //生成[0,count)间的整数
    public static int randomInt(int count) {
        return new Random().nextInt(count);
    }

    /**
     * 生成[start,end]间的整数
     **/
    public static int randomInt(int start, int end) {
        return start + new Random().nextInt(end - start + 1);
    }

    /**
     * 生成一个N位的整数，并格式化成字符串
     **/
    public static String randomIntWithFixBit(int n) {
        int num = new Random().nextInt((int) Math.pow(10, n));
        return keepInt(num, n);
    }

    public static int upperInt(double num) {
        return (int) Math.ceil(num);
    }

    public static int floorInt(double num) {
        return (int) Math.floor(num);
    }

    /**
     * 格式化字节数
     **/
    public static String formatBytes(long bytes) {
        float num;
        String unit;
        if (bytes >= 1024 * 1024 * 1024) {
            num = bytes / 1024f / 1024f / 1024f;
            unit = "GB";
        } else if (bytes >= 1024 * 1024) {
            num = bytes / 1024f / 1024f;
            unit = "MB";
        } else if (bytes >= 1024) {
            num = bytes / 1024f;
            unit = "KB";
        } else {
            num = bytes;
            unit = "B";
        }
        return new DecimalFormat("0.#").format(num) + unit;
    }

    public static String formatBytePrgoress(long finish, long total) {
        return Maths.formatBytes(finish) + "/" + Maths.formatBytes(total);
    }

    public static String hexToString(int value) {
        return Integer.toHexString(value).toUpperCase();
    }

    public static int hexStringToInt(String value) {
        return Integer.parseInt(value, 16);
    }

    public static boolean isInteger(Object o) {
        try {
            Integer.parseInt(o.toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isNumber(Object o) {
        try {
            Double.parseDouble(o.toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
