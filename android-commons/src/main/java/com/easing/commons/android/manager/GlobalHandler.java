package com.easing.commons.android.manager;

import android.os.Handler;
import android.os.Looper;

import com.easing.commons.android.helper.exception.BizException;

//全局Handler
public class GlobalHandler {

    private static Handler handler;

    //初始化，必须在主线程中调用
    public static void init() {
        if (Looper.myLooper() != Looper.getMainLooper())
            throw BizException.of("GlobalHandler must be inited in main thread");
        handler = new Handler();
    }

    public static void post(Runnable r) {
        handler.postDelayed(r, 10);
    }

    public static void postLater(Runnable r, int ms) {
        handler.postDelayed(r, ms);
    }
}


