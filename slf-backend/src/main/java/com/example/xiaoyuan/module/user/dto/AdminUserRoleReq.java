package com.example.xiaoyuan.module.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 管理端更新用户角色请求参数。
 */
@Data
public class AdminUserRoleReq {
    /**
     * 角色：0普通用户，1管理员。
     */
    @NotNull(message = "角色不能为空")
    private Integer role;
}
