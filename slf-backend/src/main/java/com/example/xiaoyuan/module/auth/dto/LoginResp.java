package com.example.xiaoyuan.module.auth.dto;

import com.example.xiaoyuan.module.user.entity.User;
import lombok.Data;

/**
 * 登录响应，包含 token 与用户信息。
 */
@Data
public class LoginResp {
    /**
     * 登录令牌。
     */
    private String token;
    /**
     * 用户信息（已脱敏）。
     */
    private User user;
}
