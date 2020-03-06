package com.easing.commons.android.thread;

import com.easing.commons.android.helper.callback.Action;
import com.easing.commons.android.helper.data.Data;
import com.easing.commons.android.time.Times;

public class Tasks {

    //判断时间间隔，再决定是否执行任务
    synchronized public static void executeWithIntervalLimit(Runnable r, Data<Long> timer, long duration, Action onFail) {
        long now = Times.millisOfNow();
        if (now - timer.data > duration) {
            r.run();
            timer.data = now;
        } else if (onFail != null) onFail.act();
    }


}
