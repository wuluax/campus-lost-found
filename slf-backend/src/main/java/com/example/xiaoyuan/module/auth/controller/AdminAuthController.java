package com.example.xiaoyuan.module.auth.controller;

import com.example.xiaoyuan.common.ApiResp;
import com.example.xiaoyuan.module.auth.dto.LoginReq;
import com.example.xiaoyuan.module.auth.dto.LoginResp;
import com.example.xiaoyuan.module.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端认证接口：管理员登录。
 */
@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {
    /**
     * 认证业务服务。
     */
    private final AuthService authService;

    public AdminAuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 管理端登录接口：校验管理员账号并返回管理端登录令牌。
     */
    @PostMapping("/login")
    public ApiResp<LoginResp> login(@Valid @RequestBody LoginReq req) {
        return ApiResp.ok(authService.adminLogin(req));
    }
}
