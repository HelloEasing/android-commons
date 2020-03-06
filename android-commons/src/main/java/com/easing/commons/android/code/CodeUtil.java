package com.easing.commons.android.code;

import com.easing.commons.android.format.Texts;
import com.easing.commons.android.manager.GlobalHandler;
import com.easing.commons.android.ui.dialog.TipBox;
import com.easing.commons.android.value.apk.PackageVersion;

import java.io.PrintWriter;
import java.io.StringWriter;

//代码调试工具
public class CodeUtil {

    //打印并弹窗显示信息
    public static void tip(Object data) {
        if (Console.apkVersion == PackageVersion.RELEASE)
            return;
        Console.info(data);
        TipBox.tip(data);
    }

    //打印并弹窗显示信息
    public static void tip(Object... datas) {
        String array = Texts.arrayToString(datas);
        CodeUtil.tip(array);
    }

    //延时打印并弹窗显示信息
    public static void tipLater(int ms, Object... datas) {
        String array = Texts.arrayToString(datas);
        CodeUtil.tipLater(array, ms);
    }

    //延时打印并弹窗显示信息
    public static void tipLater(Object data, int ms) {
        GlobalHandler.postLater(() -> CodeUtil.tip(data), ms);
    }

    //获取异常详细信息
    public static String getExceptionDetail(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw, true));
        return sw.toString();
    }
}
