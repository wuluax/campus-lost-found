package com.example.xiaoyuan.common;

/**
 * 业务异常，携带错误码与提示信息。
 */
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}