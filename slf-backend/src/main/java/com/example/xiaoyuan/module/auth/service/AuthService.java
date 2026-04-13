package com.example.xiaoyuan.module.auth.service;

import com.example.xiaoyuan.module.auth.dto.LoginReq;
import com.example.xiaoyuan.module.auth.dto.LoginResp;
import com.example.xiaoyuan.module.auth.dto.RegisterReq;

/**
 * 认证服务接口。
 */
public interface AuthService {
    void register(RegisterReq req);

    LoginResp login(LoginReq req);

    LoginResp adminLogin(LoginReq req);

    void logout(String token);

    void logoutByAuthHeader(String authHeader);
}
