package com.example.xiaoyuan.module.auth.controller;

import com.example.xiaoyuan.common.ApiResp;
import com.example.xiaoyuan.common.Constants;
import com.example.xiaoyuan.module.auth.dto.LoginReq;
import com.example.xiaoyuan.module.auth.dto.LoginResp;
import com.example.xiaoyuan.module.auth.dto.RegisterReq;
import com.example.xiaoyuan.module.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 认证相关接口：登录、注册、退出登录。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    /**
     * 认证业务服务。
     */
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户注册接口：接收注册信息并创建新用户账号。
     */
    @PostMapping("/register")
    public ApiResp<Void> register(@Valid @RequestBody RegisterReq req) {
        authService.register(req);
        return ApiResp.ok();
    }

    /**
     * 用户登录接口：校验账号密码并返回登录令牌与用户信息。
     */
    @PostMapping("/login")
    public ApiResp<LoginResp> login(@Valid @RequestBody LoginReq req) {
        return ApiResp.ok(authService.login(req));
    }

    /**
     * 用户退出登录接口：解析请求头中的 token 并执行登出逻辑。
     */
    @PostMapping("/logout")
    public ApiResp<Void> logout(@RequestHeader(name = Constants.HEADER_AUTH, required = false) String authHeader) {
        authService.logoutByAuthHeader(authHeader);
        return ApiResp.ok();
    }
}
