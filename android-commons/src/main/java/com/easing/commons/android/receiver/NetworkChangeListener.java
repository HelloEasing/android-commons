package com.easing.commons.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.easing.commons.android.app.CommonApplication;

public class NetworkChangeListener extends BroadcastReceiver {

    private Context ctx;
    private Callback callback;

    public static NetworkChangeListener register(Context ctx, Callback callback) {
        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        NetworkChangeListener listener = new NetworkChangeListener();
        ctx.registerReceiver(listener, filter);
        listener.ctx = ctx;
        listener.callback = callback;
        return listener;
    }

    public void unregister() {
        ctx.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (callback != null) {
            //部分机子网络状态生效会有延时，收到广播后不能立刻执行
            CommonApplication.handler.postDelayed(() -> callback.callback(context, intent), 2000);
            CommonApplication.handler.postDelayed(() -> callback.callback(context, intent), 4000);
            CommonApplication.handler.postDelayed(() -> callback.callback(context, intent), 6000);
            CommonApplication.handler.postDelayed(() -> callback.callback(context, intent), 8000);
        }
    }

    public interface Callback {
        void callback(Context context, Intent intent);
    }
}
