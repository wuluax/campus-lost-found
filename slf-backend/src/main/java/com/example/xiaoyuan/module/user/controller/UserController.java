package com.example.xiaoyuan.module.user.controller;

import com.example.xiaoyuan.common.ApiResp;
import com.example.xiaoyuan.common.UserContext;
import com.example.xiaoyuan.module.user.dto.UserPasswordUpdateReq;
import com.example.xiaoyuan.module.user.dto.UserPhoneUpdateReq;
import com.example.xiaoyuan.module.user.dto.UserUpdateReq;
import com.example.xiaoyuan.module.user.entity.User;
import com.example.xiaoyuan.module.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端个人资料接口。
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    /**
     * 用户业务服务。
     */
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取当前用户信息。
     */
    @GetMapping("/me")
    public ApiResp<User> me() {
        Long userId = UserContext.get();
        User u = userService.getById(userId);
        userService.maskPassword(u);
        return ApiResp.ok(u);
    }

    /**
     * 修改当前用户资料（昵称/学号/真名/头像）。
     */
    @PutMapping("/me")
    public ApiResp<User> updateProfile(@Valid @RequestBody UserUpdateReq req) {
        User u = userService.updateProfile(UserContext.get(), req);
        return ApiResp.ok(u);
    }

    /**
     * 修改当前用户手机号。
     */
    @PutMapping("/phone")
    public ApiResp<User> updatePhone(@Valid @RequestBody UserPhoneUpdateReq req) {
        User u = userService.updatePhone(UserContext.get(), req);
        return ApiResp.ok(u);
    }

    /**
     * 修改当前用户密码。
     */
    @PutMapping("/password")
    public ApiResp<Void> updatePassword(@Valid @RequestBody UserPasswordUpdateReq req) {
        userService.updatePassword(UserContext.get(), req);
        return ApiResp.ok();
    }
}
