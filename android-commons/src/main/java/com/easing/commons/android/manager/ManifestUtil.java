package com.easing.commons.android.manager;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;

import lombok.SneakyThrows;

public class ManifestUtil {

    @SneakyThrows
    public static Bundle getApplicationMetaData(Context ctx) {
        PackageManager packageManager = ctx.getPackageManager();
        ApplicationInfo info = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
        return info.metaData;
    }

    @SneakyThrows
    public static Bundle getActivityMetaData(Activity ctx) {
        PackageManager packageManager = ctx.getPackageManager();
        ActivityInfo info = packageManager.getActivityInfo(ctx.getComponentName(), PackageManager.GET_META_DATA);
        return info.metaData;
    }

    @SneakyThrows
    public static Bundle getServiceMetaData(Context ctx, Class<? extends Service> clazz) {
        PackageManager packageManager = ctx.getPackageManager();
        ServiceInfo info = packageManager.getServiceInfo(new ComponentName(ctx, clazz), PackageManager.GET_META_DATA);
        return info.metaData;
    }

    @SneakyThrows
    public static Bundle getReceiverMetaData(Context ctx, Class<? extends BroadcastReceiver> clazz) {
        PackageManager packageManager = ctx.getPackageManager();
        ActivityInfo info = packageManager.getReceiverInfo(new ComponentName(ctx, clazz), PackageManager.GET_META_DATA);
        return info.metaData;
    }

    @SneakyThrows
    public static Bundle getProviderMetaData(Context ctx, Class<? extends ContentProvider> clazz) {
        PackageManager packageManager = ctx.getPackageManager();
        ProviderInfo info = packageManager.getProviderInfo(new ComponentName(ctx, clazz), PackageManager.GET_META_DATA);
        return info.metaData;
    }
}
