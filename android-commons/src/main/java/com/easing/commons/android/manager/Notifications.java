package com.easing.commons.android.manager;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.easing.commons.android.R;
import com.easing.commons.android.format.Texts;
import com.easing.commons.android.app.CommonApplication;

@TargetApi(26)
public class Notifications {

    public static void showNotice(String title, String content, Intent action, int id) {
        String channelId = Texts.random(false, true);
        if (SystemUtil.getApiVersion() >= 26) {
            //创建Notification
            //Channel封装了一类通知的统一样式
            NotificationChannel channel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_HIGH);
            channel.setShowBadge(true);
            //设置文本图标等
            Notification.Builder builder = new Notification.Builder(CommonApplication.ctx, channelId);
            builder.setContentTitle(title);
            builder.setContentText(content);
            builder.setSmallIcon(R.drawable.icon_favor);
            //设置点击事件
            if (action != null) {
                PendingIntent pi = PendingIntent.getActivity(CommonApplication.ctx, id, action, PendingIntent.FLAG_CANCEL_CURRENT);
                builder.setContentIntent(pi);
            }
            builder.setShowWhen(true);
            builder.setAutoCancel(true);
            builder.setGroupSummary(false);
            builder.setGroup(channelId);
            //创建通知并显示
            Notification notice = builder.build();
            NotificationManager manager = Services.getNotificationManager(CommonApplication.ctx);
            manager.createNotificationChannel(channel);
            manager.notify(id, notice);
        } else {
            Notification.Builder builder = new Notification.Builder(CommonApplication.ctx);
            builder.setContentTitle(title);
            builder.setContentText(content);
            builder.setSmallIcon(R.drawable.icon_favor);
            if (action != null)
                builder.setContentIntent(PendingIntent.getActivity(CommonApplication.ctx, id, action, PendingIntent.FLAG_CANCEL_CURRENT));
            builder.setWhen(System.currentTimeMillis() + 500);
            builder.setShowWhen(true);
            builder.setAutoCancel(true);
            builder.setGroupSummary(false);
            builder.setGroup(channelId);
            Notification notice = builder.build();
            NotificationManager manager = Services.getNotificationManager(CommonApplication.ctx);
            manager.notify(id, notice);
        }
    }

    //取消全部通知
    public static void cancelAll(Context ctx) {
        Services.getNotificationManager(ctx).cancelAll();
    }
}
