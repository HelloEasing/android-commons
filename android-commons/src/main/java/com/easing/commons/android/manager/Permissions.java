package com.easing.commons.android.manager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

import com.easing.commons.android.code.Console;
import com.easing.commons.android.format.Texts;
import com.easing.commons.android.struct.Collections;
import com.easing.commons.android.app.CommonApplication;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import java.util.LinkedHashSet;
import java.util.Set;

import lombok.SneakyThrows;

public class Permissions {

    public enum PermissionGroup {
        STORAGE, LOCATION, PHONE, SMS, CONTACTS, CALENDAR, CAMERA, MICROPHONE, SENSORS;

        private String[] permissions;

        static {
            STORAGE.permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            LOCATION.permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS};
            PHONE.permissions = new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.USE_SIP, Manifest.permission.PROCESS_OUTGOING_CALLS};
            SMS.permissions = new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_WAP_PUSH, Manifest.permission.RECEIVE_MMS};
            CONTACTS.permissions = new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.GET_ACCOUNTS};
            CALENDAR.permissions = new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR};
            CAMERA.permissions = new String[]{Manifest.permission.CAMERA};
            MICROPHONE.permissions = new String[]{Manifest.permission.RECORD_AUDIO};
            SENSORS.permissions = new String[]{Manifest.permission.BODY_SENSORS};
        }
    }

    public static boolean checkPermissions(PermissionGroup... groups) {
        for (PermissionGroup group : groups)
            for (String permission : group.permissions)
                if (ContextCompat.checkSelfPermission(CommonApplication.ctx, permission) != PackageManager.PERMISSION_GRANTED)
                    return false;
        return true;
    }

    public static void requestPermissionGroup(Context ctx, PermissionGroup... groups) {
        Set<String> permissionSet = new LinkedHashSet();
        for (PermissionGroup group : groups)
            for (String permission : group.permissions)
                if (ContextCompat.checkSelfPermission(ctx, permission) != PackageManager.PERMISSION_GRANTED)
                    permissionSet.add(permission);
        String[] permissionAarray = Collections.toStringArray(permissionSet);
        if (permissionAarray.length == 0)
            return;

        Action action = (permissions) -> {
            String msg = "获取权限失败：\n" + Texts.listToString(permissions, "\n");
            Console.error(msg);
        };

        AndPermission.with(ctx).permission(permissionAarray).onDenied(action).start();
    }

    @SneakyThrows
    public static void requestPermissionGroup(Context ctx, com.easing.commons.android.helper.callback.Action okAction, com.easing.commons.android.helper.callback.Action failAction, PermissionGroup... groups) {
        if (Permissions.checkPermissions(groups)) {
            if (okAction != null)
                okAction.act();
            return;
        }

        Set<String> permissionSet = new LinkedHashSet();
        for (PermissionGroup group : groups)
            for (String permission : group.permissions)
                if (ContextCompat.checkSelfPermission(ctx, permission) != PackageManager.PERMISSION_GRANTED)
                    permissionSet.add(permission);
        String[] permissionArray = Collections.toStringArray(permissionSet);
        if (permissionArray.length == 0)
            return;

        Action grantAction = permissions -> {
            if (okAction != null)
                okAction.act();
        };

        Action denyAction = permissions -> {
            GlobalHandler.postLater(() -> {
                String msg = "获取权限失败：\n" + Texts.listToString(permissions, "\n");
                Console.error(msg);
            }, 500);
            if (failAction != null)
                failAction.act();
        };

        AndPermission.with(ctx).permission(permissionArray).onGranted(grantAction).onDenied(denyAction).start();
    }
}
