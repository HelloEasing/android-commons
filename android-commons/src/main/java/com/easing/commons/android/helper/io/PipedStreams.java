package com.easing.commons.android.helper.io;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import lombok.SneakyThrows;

//这个类自动将OutputStream收到的数据写到InputStream里面
public class PipedStreams {

    private PipedInputStream is;
    private PipedOutputStream os;
    private ByteArrayOutputStream bos;

    public boolean alive;

    @SneakyThrows
    public PipedStreams() {
        alive = true;
        is = new PipedInputStream() {
            @SneakyThrows
            public void close() {
                alive = false;
                super.close();
            }
        };
        os = new PipedOutputStream();
        bos = new ByteArrayOutputStream() {
            @SneakyThrows
            public void close() {
                alive = false;
                super.close();
                os.close();
            }
        };
        os.connect(is);
        //将输出流数据写到输入流
        new Thread(() -> startRead()).start();
    }

    //将输出流数据写到输入流
    @SneakyThrows
    private void startRead() {
        while (alive) {
            if (bos.size() > 0) {
                byte[] buffer = null;
                synchronized (bos) {
                    buffer = bos.toByteArray();
                    bos.reset();
                }
                os.write(buffer, 0, buffer.length);
            } else
                Thread.sleep(500);
        }
    }

    public InputStream getInputStream() {
        return is;
    }

    public OutputStream getOutputStream() {
        return bos;
    }
}

