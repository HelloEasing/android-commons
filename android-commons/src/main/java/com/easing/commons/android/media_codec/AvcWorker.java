package com.easing.commons.android.media_codec;

import com.easing.commons.android.thread.Threads;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//处理编码出的H264数据
//在work方法中定义自己的业务代码，比如拿来播放，写文件，推流等
public class AvcWorker {

    Queue<ByteBuffer> queue = new LinkedList();

    Lock lock = new ReentrantLock();
    boolean working = false;

    public AvcWorker() {
        Threads.post(() -> {
            onInit();
            while (true) {
                lock.lock();
                if (working) work();
                else Threads.sleep(500);
                lock.unlock();
            }
        });
    }

    //NALU入列
    public void enqueue(byte[] nalu, int len) {
        lock.lock();
        ByteBuffer buffer = ByteBuffer.allocate(len);
        buffer.put(nalu, 0, len);
        queue.offer(buffer);
        lock.unlock();
    }

    //NALU出列
    public ByteBuffer dequeue() {
        lock.lock();
        ByteBuffer buffer = queue.poll();
        lock.unlock();
        return buffer;
    }

    public AvcWorker start() {
        lock.lock();
        onStart();
        working = true;
        lock.unlock();
        return this;
    }

    public AvcWorker stop() {
        lock.lock();
        queue.clear();
        onStop();
        working = false;
        lock.unlock();
        return this;
    }

    protected void work() {}

    protected void onInit() {}

    protected void onStart() {}

    protected void onStop() {}
}
