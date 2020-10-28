package com.panda.coupon.jsbridge;

/**
 * Created by congxiany on 18/3/5.
 */

public class JSCallbackRsp {
    public Object data;
    public int code;
    public String message;

    public JSCallbackRsp() {
    }

    public JSCallbackRsp(int code, Object data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public JSCallbackRsp(int code) {
        this(code, null, null);
    }
}
