package com.example.xiaoyuan.module.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 用户手机号修改请求参数。
 */
@Data
public class UserPhoneUpdateReq {
    /**
     * 新手机号。
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^\\d{11}$", message = "手机号必须为11位数字")
    private String phone;

    /**
     * 当前登录密码，用于校验。
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}
