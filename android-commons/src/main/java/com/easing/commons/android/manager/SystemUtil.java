package com.easing.commons.android.manager;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Looper;
import android.os.Process;

import java.lang.reflect.Field;

import lombok.SneakyThrows;

public class SystemUtil {

    @Deprecated
    public static void killProcess() {
        Process.killProcess(Process.myPid());
    }

    @Deprecated
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int res_id = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(res_id);
    }

    @Deprecated
    @SneakyThrows
    public static int getStatuBarHeight(Context context) {
        Class c = Class.forName("com.android.internal.R$dimen");
        Object obj = c.newInstance();
        Field field = c.getField("status_bar_height");
        int x = Integer.parseInt(field.get(obj).toString());
        return context.getResources().getDimensionPixelSize(x);
    }

    @Deprecated
    public static int getCurrentThreadId() {
        return (int) Looper.myLooper().getThread().getId();
    }

    @Deprecated
    public static int getMainThreadId() {
        return (int) Looper.getMainLooper().getThread().getId();
    }

    @Deprecated
    @SneakyThrows
    public static String getPackageName(Context context) {
        PackageManager manager = context.getPackageManager();
        String packageName = context.getPackageName();
        return packageName;
    }

    @Deprecated
    @SneakyThrows
    public static int getApiVersion() {
        return Build.VERSION.SDK_INT;
    }

    @Deprecated
    @SneakyThrows
    public static int getVersionCode(Context context) {
        PackageManager manager = context.getPackageManager();
        String packageName = context.getPackageName();
        PackageInfo info = manager.getPackageInfo(packageName, 0);
        return info.versionCode;
    }

    @Deprecated
    @SneakyThrows
    public static String getVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        String packageName = context.getPackageName();
        PackageInfo info = manager.getPackageInfo(packageName, 0);
        return info.versionName;
    }

    @Deprecated
    @SneakyThrows
    public static String getMetaInfo(Context ctx, String key) {
        ApplicationInfo info = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
        if (info != null)
            if (info.metaData != null)
                return info.metaData.getString(key);
        return null;
    }
}
