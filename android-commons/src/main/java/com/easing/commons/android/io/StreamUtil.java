package com.easing.commons.android.io;

import java.io.InputStream;

import lombok.SneakyThrows;

public class StreamUtil {

    //输入流转字符串
    @SneakyThrows
    public static String streamToString(InputStream fis, String encode) {
        if (encode == null)
            encode = "UTF-8";
        byte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        Files.close(fis, null);
        return new String(buffer, encode);
    }
}
