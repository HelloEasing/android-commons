package com.easing.commons.android.ui.dialog;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.easing.commons.android.format.Texts;
import com.easing.commons.android.value.apk.PackageVersion;
import com.easing.commons.android.view.Views;
import com.easing.commons.android.R;

//自定义Toast
public class TipBox {

    private static Context ctx;
    private static Handler handler;

    volatile private static Toast toast;

    //绑定应用上下文
    public static void init(Context ctx) {
        TipBox.ctx = ctx;
        TipBox.handler = new Handler();
    }

    //弹出Toast
    public static void toast(Context context, Object msg, int layout) {
        if (toast != null)
            toast.cancel();

        View root = LayoutInflater.from(context).inflate(layout, null);
        TextView tv = Views.findView(root, R.id.tv);
        tv.setText(msg == null ? "null" : msg.toString());
        toast = new Toast(ctx);
        toast.setView(root);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    //通过指定的Layout显示Toast
    public static void message(Object msg, int layout) {
        handler.post(() -> {
            TipBox.toast(ctx, msg, layout);
        });
    }

    //通过指定的Layout和Handler显示Toast
    public static void message(Handler handler, Object msg, int layout) {
        handler.post(() -> {
            TipBox.toast(ctx, msg, layout);
        });
    }

    //取消Toast
    public static void cancel() {
        handler.post(() -> {
            if (toast != null)
                toast.cancel();
        });
    }

    //M1样式toast
    public static void messageM1(Object msg) {
        TipBox.message(msg, R.layout.layout_tip_box_1);
    }

    //默认使用M1样式显示toast
    public static void tip(Object msg) {
        TipBox.messageM1(msg);
    }

    //默认使用M1样式显示toast
    public static void tip(Object msg, PackageVersion apkVersion) {
        if (apkVersion != PackageVersion.RELEASE)
            TipBox.messageM1(msg);
    }

    //默认使用M1样式显示toast
    public static void tip(Object... msg) {
        TipBox.messageM1(Texts.arrayToString(msg));
    }

    //默认使用M1样式显示toast
    public static void multiLineTip(Object... msg) {
        String message = String.valueOf(msg[0]);
        for (int i = 1; i < msg.length; i++)
            message = message + "\n" + String.valueOf(msg[i]);
        TipBox.tip(message);
    }

    //默认使用M1样式显示toast
    public static void tip(PackageVersion apkVersion, Object... msg) {
        if (apkVersion != PackageVersion.RELEASE)
            TipBox.messageM1(Texts.arrayToString(msg));
    }
}
