package com.easing.commons.android.code;

import android.util.Log;

import com.easing.commons.android.format.Texts;
import com.easing.commons.android.value.apk.PackageVersion;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

//控制台打印模块
public class Console {

    public static final String TAG = "com.easing.commons";

    public static PackageVersion apkVersion = PackageVersion.DEBUG;

    public static void info(Object obj) {
        Console.infoWithTag(TAG, obj);
    }

    public static void info(Object... objs) {
        Console.infoWithTag(TAG, objs);
    }

    public static void infoWithTag(String tag, Object obj) {
        if (apkVersion == PackageVersion.RELEASE)
            return;
        if (obj != null)
            Log.i(tag, obj.toString());
        else
            Log.i(tag, "null");
    }

    public static void infoWithTag(String tag, Object... objs) {
        String msg = Texts.arrayToString(objs);
        Console.infoWithTag(tag, msg);
    }

    public static void error(Throwable e) {
        //TODO => delete
        if (e instanceof SocketTimeoutException)
            return;
        if (e instanceof ConnectException)
            return;
        String info = CodeUtil.getExceptionDetail(e);
        Console.error(TAG, info);
    }

    public static void error(Throwable e, PackageVersion apkVersion) {
        if (apkVersion == PackageVersion.RELEASE)
            Console.error(e);
    }

    public static void error(Object obj) {
        Console.errorWithTag(TAG, obj);
    }

    public static void error(Object... objs) {
        Console.errorWithTag(TAG, objs);
    }

    public static void errorWithTag(String tag, Object obj) {
        if (apkVersion == PackageVersion.RELEASE)
            return;
        if (obj != null)
            Log.e(tag, obj.toString());
        else
            Log.e(tag, "null");
    }

    public static void errorWithTag(String tag, Object... objs) {
        String msg = Texts.arrayToString(objs);
        Console.errorWithTag(tag, msg);
    }
}
