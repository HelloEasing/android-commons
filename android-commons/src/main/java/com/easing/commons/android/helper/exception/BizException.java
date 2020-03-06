package com.easing.commons.android.helper.exception;

import com.easing.commons.android.code.Console;

public class BizException extends RuntimeException {

    public static final BizException SERVER_ERROR = BizException.of("Server Error");
    public static final BizException NETWORK_ERROR = BizException.of("Network Error");

    public static BizException of(Exception e) {
        return new BizException(e);
    }

    public static BizException of(String message) {
        return new BizException(message);
    }

    private BizException(Exception e) {
        super(e);
    }

    private BizException(String message) {
        super(message);
    }

    public void print() {
        Console.error(this);
    }

}
