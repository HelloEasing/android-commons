package com.easing.commons.android.thread;

import android.os.Looper;

import com.easing.commons.android.helper.thread.AliveState;
import com.easing.commons.android.app.CommonActivity;

import lombok.SneakyThrows;

public class Threads {

    @SneakyThrows
    public static void sleep(int ms) {
        Thread.currentThread().sleep(ms);
    }

    @SneakyThrows
    public static void wait(Object lock) {
        lock.wait();
    }

    @SneakyThrows
    public static void wait(Object lock, long timeout) {
        lock.wait(timeout);
    }

    @SneakyThrows
    public static void notify(Object lock) {
        lock.notify();
    }

    @SneakyThrows
    public static void notifyAll(Object lock) {
        lock.notifyAll();
    }

    @SneakyThrows
    public static void join(Thread thread) {
        thread.join();
    }

    @SneakyThrows
    public static void interrupt(Thread thread) {
        thread.interrupt();
    }

    @SneakyThrows
    public static void post(Action action) {
        new Thread(() -> {
            try {
                action.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @SneakyThrows
    public static void postLater(Action action, int ms) {
        new Thread(() -> {
            try {
                Threads.sleep(ms);
                action.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @SneakyThrows
    public static void postByInterval(Action action, int interval, AliveState state) {
        new Thread(() -> {
            try {
                if (state == null || state.isAlive()) {
                    action.run();
                    Threads.sleep(interval);
                }
                while (state == null || state.isAlive()) {
                    action.run();
                    Threads.sleep(interval);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @SneakyThrows
    public static void postByInterval(Action action, int interval, int firstDelay, CommonActivity ctx) {
        new Thread(() -> {
            try {
                Threads.sleep(firstDelay);
                if (!ctx.isFinishing()) {
                    action.run();
                    Threads.sleep(interval);
                }
                while (!ctx.isFinishing()) {
                    action.run();
                    Threads.sleep(interval);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public static long currentThreadId() {
        return Thread.currentThread().getId();
    }

    public static long mainThreadId() {
        return Looper.getMainLooper().getThread().getId();
    }

    public interface Action {

        void run() throws Exception;
    }
}
