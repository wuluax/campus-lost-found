package com.example.xiaoyuan.module.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 注册请求参数。
 */
@Data
public class RegisterReq {
    /**
     * 注册手机号。
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^\\d{11}$", message = "手机号必须为11位数字")
    private String phone;

    /**
     * 登录密码。
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码至少6位")
    private String password;

    /**
     * 用户昵称。
     */
    @NotBlank(message = "昵称不能为空")
    @Size(min = 2, max = 30, message = "昵称长度需在2-30位之间")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9_]+$", message = "昵称只能包含汉字、字母、数字和下划线")
    private String nickname;
}
