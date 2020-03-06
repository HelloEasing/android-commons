package com.easing.commons.android.app;

import android.os.Looper;

import com.easing.commons.android.R;
import com.easing.commons.android.code.CodeUtil;
import com.easing.commons.android.code.Console;
import com.easing.commons.android.code.Logger;
import com.easing.commons.android.thread.Threads;
import com.easing.commons.android.ui.dialog.TipBox;

import lombok.SneakyThrows;

import java.lang.Thread.UncaughtExceptionHandler;

public class CommonExceptionHandler implements UncaughtExceptionHandler {

    @Override
    @SneakyThrows
    public void uncaughtException(Thread t, Throwable e) {
        Console.error(e);

        //开辟其它线程，显示报错信息
        Threads.post(() -> {
            Looper.prepare();
            TipBox.toast(CommonApplication.ctx, "程序发生未知异常", R.layout.layout_tip_box_1);
            Logger.error(CodeUtil.getExceptionDetail(e), "app/" + CommonApplication.projectName + "/error");
            CommonApplication.ctx.handleGlobalException(e);
            Looper.loop();
        });

        //结束进程
        if (CommonApplication.exitOnException) {
            Threads.sleep(CommonApplication.waitTimeOnException);
            CommonApplication.ctx.finishProcess();
        }
    }


}


