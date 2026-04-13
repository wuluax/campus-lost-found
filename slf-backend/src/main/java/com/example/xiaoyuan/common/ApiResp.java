package com.example.xiaoyuan.common;

import lombok.Data;

/**
 * 通用接口响应包装。
 *
 * code：业务状态码，0 表示成功；非 0 表示失败
 * msg：提示信息
 * data：业务数据载荷
 */
@Data
public class ApiResp<T> {

    //业务状态码，0 表示成功，非 0 表示失败。
    private int code;

    //提示信息，成功默认 success。
    private String msg;

    //业务数据载荷，失败时通常为 null。
    private T data;

    /**
     * 构造成功响应并携带数据。
     *
     * @param data 业务数据
     * @param <T>  数据类型
     * @return 通用响应
     */
    public static <T> ApiResp<T> ok(T data) {
        ApiResp<T> r = new ApiResp<>();
        r.code = ErrorCode.OK.getCode();
        r.msg = "success";
        r.data = data;
        return r;
    }

    /**
     * 构造成功响应且不携带数据。
     *
     * @param <T> 数据类型
     * @return 通用响应
     */
    public static <T> ApiResp<T> ok() {
        return ok(null);
    }

    /**
     * 构造失败响应，使用预置错误码。
     *
     * @param code 错误码枚举
     * @param msg  提示信息
     * @param <T>  数据类型
     * @return 通用响应
     */
    public static <T> ApiResp<T> error(ErrorCode code, String msg) {
        ApiResp<T> r = new ApiResp<>();
        r.code = code.getCode();
        r.msg = msg;
        return r;
    }

    /**
     * 构造失败响应，直接指定错误码数值。
     *
     * @param code 错误码
     * @param msg  提示信息
     * @param <T>  数据类型
     * @return 通用响应
     */
    public static <T> ApiResp<T> error(int code, String msg) {
        ApiResp<T> r = new ApiResp<>();
        r.code = code;
        r.msg = msg;
        return r;
    }
}
