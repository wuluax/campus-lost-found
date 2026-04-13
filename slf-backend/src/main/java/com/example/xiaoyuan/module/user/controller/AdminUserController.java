package com.example.xiaoyuan.module.user.controller;

import com.example.xiaoyuan.common.ApiResp;
import com.example.xiaoyuan.common.PageResp;
import com.example.xiaoyuan.module.user.dto.AdminUserRoleReq;
import com.example.xiaoyuan.module.user.dto.AdminUserStatusReq;
import com.example.xiaoyuan.module.user.entity.User;
import com.example.xiaoyuan.module.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端用户管理接口。
 */
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {
    /**
     * 用户业务服务。
     */
    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 分页查询用户列表。
     */
    @GetMapping
    public ApiResp<PageResp<User>> page(@RequestParam(defaultValue = "1") int pageNo,
                                        @RequestParam(defaultValue = "15") int pageSize,
                                        @RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) Integer role,
                                        @RequestParam(required = false) Integer status) {
        return ApiResp.ok(userService.adminPage(pageNo, pageSize, keyword, role, status));
    }

    /**
     * 更新用户状态（封禁/解封）。
     */
    @PutMapping("/{id}/status")
    public ApiResp<Void> updateStatus(@PathVariable Long id, @Valid @RequestBody AdminUserStatusReq req) {
        userService.adminUpdateStatus(id, req == null ? null : req.getStatus());
        return ApiResp.ok();
    }

    /**
     * 更新用户角色（普通/管理员）。
     */
    @PutMapping("/{id}/role")
    public ApiResp<Void> updateRole(@PathVariable Long id, @Valid @RequestBody AdminUserRoleReq req) {
        userService.adminUpdateRole(id, req == null ? null : req.getRole());
        return ApiResp.ok();
    }

    /**
     * 删除用户。
     */
    @DeleteMapping("/{id}")
    public ApiResp<Void> delete(@PathVariable Long id) {
        userService.adminDelete(id);
        return ApiResp.ok();
    }
}
