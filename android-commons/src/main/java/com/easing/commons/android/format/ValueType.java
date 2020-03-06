package com.easing.commons.android.format;

//数据类型互转
public class ValueType {

    public static Integer intValue(String value) {
        return Integer.valueOf(value);
    }

    public static Integer intValue(Object value, Integer defaultValue) {
        try {
            return Integer.valueOf(value.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static String stringValue(Object value) {
        return String.valueOf(value);
    }

    public static String stringValue(Object value, String defaultValue) {
        if (value == null) return defaultValue;
        return String.valueOf(value);
    }

}
