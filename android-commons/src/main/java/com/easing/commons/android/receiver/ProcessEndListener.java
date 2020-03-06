package com.easing.commons.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Process;

//发送广播来结束进程，通过外部广播可以实现跨进程效果
public class ProcessEndListener extends BroadcastReceiver {

    private Context ctx;

    public static ProcessEndListener register(Context ctx) {
        IntentFilter filter = new IntentFilter("com.easing.commons.android.END_PROCESS");
        ProcessEndListener listener = new ProcessEndListener();
        ctx.registerReceiver(listener, filter);
        listener.ctx = ctx;
        return listener;
    }

    public static void send(Context ctx) {
        Intent intent = new Intent();
        intent.setAction("com.easing.commons.android.END_PROCESS");
        ctx.sendBroadcast(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ctx.unregisterReceiver(this);
        ctx = null;
        Process.killProcess(Process.myPid());
    }
}
