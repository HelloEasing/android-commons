package com.easing.commons.android.helper.data;

//封装运算结果和错误信息
public class Result<T> {

    public static final int CODE_OK = 0;
    public static final int CODE_FAIL = -1;

    public boolean ok;
    public int code;
    public String msg;
    public T data;

    public static Result ok() {
        Result rst = new Result();
        rst.ok = true;
        return rst;
    }

    public static Result fail() {
        Result rst = new Result();
        rst.ok = false;
        return rst;
    }

    public static Result ok(int code, String msg, Object data) {
        Result rst = new Result();
        rst.ok = true;
        rst.code = code;
        rst.msg = msg;
        rst.data = data;
        return rst;
    }

    public static Result fail(int code, String msg, Object data) {
        Result rst = new Result();
        rst.ok = false;
        rst.code = code;
        rst.msg = msg;
        rst.data = data;
        return rst;
    }
}
