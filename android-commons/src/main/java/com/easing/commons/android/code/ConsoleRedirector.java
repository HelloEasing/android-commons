package com.easing.commons.android.code;

import android.widget.EditText;

import com.easing.commons.android.helper.io.PipedStreams;
import com.easing.commons.android.manager.GlobalHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

import lombok.SneakyThrows;

//将控制台数据重定向到控件上
//原理：控制台OutputStream -> InputStream -> 控件
//PipedStreams类用于将OutputStream数据实时写到InputStream里面
//ConsoleRedirector类用于将InputStream实时写到控件里面
public class ConsoleRedirector {

    //将控制台数据重定向到控件中
    @SneakyThrows
    public static PipedStreams redirectTo(EditText edit) {
        //将控制台重定位到PipedStreams中的输出流
        PipedStreams pipedStreams = new PipedStreams();
        PrintStream printer = new PrintStream(pipedStreams.getOutputStream());
        System.setOut(printer);
        System.setErr(printer);

        //从PipedStreams中的输入流读取数据，写入到控件中
        edit.setFocusable(false);
        new Thread(() -> readLines(edit, pipedStreams)).start();
        return pipedStreams;
    }

    //从PipedStreams中的输入流读取数据，写入到控件中
    @SneakyThrows
    private static void readLines(EditText edit, PipedStreams pipedStreams) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(pipedStreams.getInputStream()));
        while (pipedStreams.alive) {
            String line = reader.readLine();
            if (line != null) {
                GlobalHandler.post(() -> {
                    if (edit.getLineCount() > 20)
                        edit.setText("");
                    edit.append(line + "\n");
                    edit.setSelection(edit.getText().length());
                });
                Thread.sleep(500);
            }
        }
    }
}
