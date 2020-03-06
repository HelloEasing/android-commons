package com.easing.commons.android.app;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;

import lombok.SneakyThrows;

public class ApplicationManager {

    @SneakyThrows
    public static String getPackageName(Context context) {
        PackageManager manager = context.getPackageManager();
        String packageName = context.getPackageName();
        return packageName;
    }

    @SneakyThrows
    public static int getApiVersion() {
        return Build.VERSION.SDK_INT;
    }

    @SneakyThrows
    public static int getVersionCode(Context context) {
        PackageManager manager = context.getPackageManager();
        String packageName = context.getPackageName();
        PackageInfo info = manager.getPackageInfo(packageName, 0);
        return info.versionCode;
    }

    @SneakyThrows
    public static String getVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        String packageName = context.getPackageName();
        PackageInfo info = manager.getPackageInfo(packageName, 0);
        return info.versionName;
    }

    @SneakyThrows
    public static String getMetaInfo(Context ctx, String key) {
        ApplicationInfo info = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
        if (info != null)
            if (info.metaData != null)
                return info.metaData.getString(key);
        return null;
    }

    public static void killProcess() {
        Process.killProcess(Process.myPid());
        System.exit(1);
    }
}
