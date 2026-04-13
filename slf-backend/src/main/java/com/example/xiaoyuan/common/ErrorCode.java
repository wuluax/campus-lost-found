package com.example.xiaoyuan.common;

/**
 * 统一错误码枚举。
 */
public enum ErrorCode {
    OK(0),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    SERVER_ERROR(500),
    PARAM_ERROR(666);

    private final int code;

    ErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}