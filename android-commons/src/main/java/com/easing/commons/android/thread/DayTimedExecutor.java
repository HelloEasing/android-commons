package com.easing.commons.android.thread;

import com.easing.commons.android.time.Times;
import com.easing.commons.android.helper.thread.AliveState;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//这是一个按天执行的任务计划模块
//每天在指定时间段，自动执行设置好的任务
public class DayTimedExecutor {

    private final ExecutorService pool = Executors.newScheduledThreadPool(100);

    private AliveState aliveState = AliveState.create();

    private LinkedList<DayTimedTask> tasks = new LinkedList();

    private OnDayEnd onDayEnd;

    public boolean isCurrentTaskDone;
    public boolean isTaskInitialized;

    public boolean pausing;

    private DayTimedExecutor() {
    }

    //静态工厂方法
    public static DayTimedExecutor create(TaskInitializer taskInitializer, OnDayEnd onDayEnd) {
        DayTimedExecutor executor = new DayTimedExecutor();
        executor.onDayEnd = onDayEnd;
        executor.initialize(taskInitializer);
        return executor;
    }

    //初始化当天的任务内容
    public void initialize(TaskInitializer taskInitializer) {
        this.tasks = taskInitializer.init();
        this.isTaskInitialized = false;
        this.isCurrentTaskDone = true;
    }

    //开始当天任务计划
    public void start() {
        aliveState.resume();
        Threads.postByInterval(() -> {
            String hmTime = Times.now("HH:mm");
            int currentMin = Times.hmTimeToMin(hmTime);

            //新的一天，结束任务，开始新的任务
            //建议重新创建一个Executor来执行新一天的任务
            //这样不用考虑各种变量的重新初始化和同步问题
            if (currentMin < 10 && aliveState.isAlive()) {
                stop();
                Threads.sleep(10000);
                onDayEnd.onDayEnd();
                return;
            }

            //轮询查看是否存在可以执行的任务
            for (DayTimedTask task : tasks) {
                if (pausing)
                    return;
                if (task.executed)
                    continue;
                if (currentMin > Times.hmTimeToMin(task.endTime)) {
                    task.executed = true;
                    continue;
                }
                if (task.repeatCount == 0) {
                    task.executed = true;
                    continue;
                }
                if (!isCurrentTaskDone)
                    continue;
                if (currentMin > Times.hmTimeToMin(task.startTime)) {
                    pool.submit(task.r);
                    task.repeatCount--;
                    isCurrentTaskDone = false;
                }
            }
        }, 2000, aliveState);
    }

    //停止执行任务
    public void stop() {
        aliveState.kill();
        if (!pool.isShutdown())
            pool.shutdownNow();
    }

    //封装任务单元
    public static class DayTimedTask {

        public Runnable r;
        private boolean executed;

        public String startTime;
        public String endTime;

        public int repeatCount;

        //将用户指定的行为，转化为一个标准的Task对象
        public static DayTimedTask wrap(Runnable r, String startTime, String endTime, int repeatCount) {
            DayTimedTask task = new DayTimedTask();
            task.r = r;
            task.executed = false;
            task.startTime = startTime;
            task.endTime = endTime;
            task.repeatCount = repeatCount;
            return task;
        }
    }

    //用来在每天早餐初始化时，生成当天的任务列表
    public interface TaskInitializer {

        LinkedList<DayTimedTask> init();
    }

    //一天结束，通过回调通知用户，开始新一天的任务
    //建议重新创建一个Executor，重新获取新一天的任务
    //这样在逻辑上，和上一天的Executor没有任何偶尔，逻辑简单
    public interface OnDayEnd {

        void onDayEnd();
    }

}
