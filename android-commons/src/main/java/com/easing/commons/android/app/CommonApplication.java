package com.easing.commons.android.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;

import android.content.Context;
import android.os.Handler;
import android.os.Process;

import com.easing.commons.android.code.Logger;
import com.easing.commons.android.helper.callback.Action;
import com.easing.commons.android.helper.thread.AliveState;
import com.easing.commons.android.io.Files;
import com.easing.commons.android.manager.GlobalHandler;
import com.easing.commons.android.manager.Uris;
import com.easing.commons.android.preference.Preference;
import com.easing.commons.android.thread.Threads;
import com.easing.commons.android.time.Times;
import com.easing.commons.android.ui.dialog.TipBox;

import org.greenrobot.eventbus.EventBus;

import java.util.LinkedList;

public class CommonApplication extends Application {

    //项目名称
    public static String projectName;
    //项目版本
    public static String localDataVersion;
    //全局Context
    public static CommonApplication ctx;
    //全局Handler
    public static Handler handler;
    //主线程id
    public static long mainPid;
    //EventBus
    public static EventBus eventBus;

    //任务栈管理
    private static LinkedList<Activity> activityStack = new LinkedList();

    //全局线程标志位，其它线程根据这个判断是否要继续执行
    //APP退出前将标志位置为false，相关联的子线程就会自动结束
    public static AliveState aliveState = AliveState.create();

    //出现异常时是否退出
    public static boolean exitOnException = true;
    //退出前的等待时长
    public static int waitTimeOnException = 500;

    @Override
    public void onCreate() {
        //绑定全局异常处理
        Thread.setDefaultUncaughtExceptionHandler(new CommonExceptionHandler());
        super.onCreate();

        //初始化全局环境
        initContext();
    }

    //初始化全局环境
    private void initContext() {
        CommonApplication.ctx = this;
        CommonApplication.handler = new Handler();
        CommonApplication.mainPid = Thread.currentThread().getId();
        CommonApplication.eventBus = EventBus.getDefault();
        GlobalHandler.init();
        TipBox.init(this);
        Uris.init(this);
    }

    //注册EventBus
    public static void regEventBus(Object ctx) {
        CommonApplication.eventBus.register(ctx);
    }

    //解注册EventBus
    public static void unregEventBus(Object ctx) {
        CommonApplication.eventBus.unregister(ctx);
    }

    //发送EventBus事件
    public static void emitEvent(Object event) {
        CommonApplication.eventBus.post(event);
    }

    public LinkedList<Activity> getTaskStack() {
        return activityStack;
    }

    public void addToStack(Activity activity) {
        activityStack.addLast(activity);
        CommonApplication.aliveState.resume();
    }

    public void removeFromStack(Activity activity) {
        activityStack.remove(activity);
        handler.postDelayed(() -> {
            if (activityStack.isEmpty())
                CommonApplication.aliveState.kill();
        }, 2000);
    }

    public void finishAll() {
        CommonApplication.aliveState.kill();
        for (Activity activity : activityStack)
            activity.finish();
    }

    //获取当前进程名
    public static String getCurrentProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses())
            if (appProcess.pid == pid)
                return appProcess.processName;
        return null;
    }

    //结束进程
    public void finishProcess() {
        finishAll();
        Process.killProcess(Process.myPid());
    }

    //延时结束进程
    public void finishProcessLater() {
        finishAll();
        handler.postDelayed(() -> Process.killProcess(Process.myPid()), 200);
    }

    //延时结束进程
    public void finishProcessLater(long ms) {
        finishAll();
        handler.postDelayed(() -> Process.killProcess(Process.myPid()), ms);
    }

    //判断APP是否完全退出
    //如果Activity数量为0，则表示APP退出
    public static boolean isAppAlive() {
        return aliveState.isAlive();
    }

    //全局异常特殊处理
    //建议子类覆写这个方法
    public void handleGlobalException(Throwable e) {
    }

    //设置项目名称
    public static void setProjectName(String name) {
        CommonApplication.projectName = name;
    }

    //设置本地数据版本
    public static void setLocalDataVersion(String version) {
        CommonApplication.localDataVersion = version;
    }

    //获取数据存储路径
    public static String getFilePath(String path) {
        String file = Files.getAndroidExternalFile("app/" + projectName + "/" + path);
        Files.createFile(file);
        return file;
    }

    //获取数据存储路径
    public static String getFolderPath(String path) {
        String file = Files.getAndroidExternalFolder("app/" + projectName + "/" + path);
        Files.createFolder(file);
        return file;
    }

    //生成存储路径的人性化描述
    public static String getPathDescription(String path) {
        return "存储卡/app/" + projectName + "/" + path;
    }

    //首次使用当前版本
    public static void checkLocalDataVersion(Action onLocalDataVersionChange) {
        String version = Preference.get("local_data_version", String.class, null);
        if (version == null || !version.equals(localDataVersion)) {
            onLocalDataVersionChange.act();
            Preference.set("local_data_version", localDataVersion);
        }
    }


}
