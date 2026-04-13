package com.example.xiaoyuan.module.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户资料修改请求参数。
 */
@Data
public class UserUpdateReq {
    /**
     * 昵称：允许中文、字母、数字、下划线，长度 2-30。
     */
    @Size(min = 2, max = 30, message = "昵称长度需在2-30位之间")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9_]+$", message = "昵称只能包含汉字、字母、数字和下划线")
    private String nickname;

    /**
     * 学号：10位数字。
     */
    @Pattern(regexp = "^\\d{10}$", message = "学号必须为10位数字")
    private String studentNo;

    /**
     * 真实姓名：仅中文。
     */
    @Pattern(regexp = "^[\\u4e00-\\u9fa5]+$", message = "真实姓名必须为中文")
    private String realName;

    /**
     * 头像地址。
     */
    @Size(max = 255, message = "头像地址长度不能超过255")
    private String avatar;
}
