package com.easing.commons.android.manager;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Vibrator;
import android.print.PrintManager;
import android.view.autofill.AutofillManager;

@SuppressWarnings("all")
public class Services {

    public static AudioManager getAudioManager(Context context) {
        return (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public static LocationManager getLocationManager(Context context) {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public static AlarmManager getAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public static Vibrator getVibratorManager(Context context) {
        return (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
    }

    public static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
    }

    public static ActivityManager getActivityManager(Context context) {
        return (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    public static PrintManager getPrintManager(Context context) {
        return (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
    }

    public static AutofillManager getAutofillManager(Context context) {
        return context.getSystemService(AutofillManager.class);
    }

    public static void vibrate(Context context) {
        Vibrator vibrator = Services.getVibratorManager(context);
        long[] intervals = new long[]{1, 1000, 300, 1000, 300, 1000, 300};
        vibrator.vibrate(intervals, -1);
    }

    public static void openApplicationInfoActivity(Context ctx) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("package:" + ctx.getPackageName()));
        ctx.startActivity(intent);
    }

    //复制文本到剪贴板
    public static void copyToClipboard(Context ctx, String text) {
        ClipboardManager clipboard = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("simple text", text);
        clipboard.setPrimaryClip(clip);
    }
}
