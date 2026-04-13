package com.example.xiaoyuan.module.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 管理端更新用户状态请求参数。
 */
@Data
public class AdminUserStatusReq {
    /**
     * 状态：0封禁，1正常。
     */
    @NotNull(message = "状态不能为空")
    private Integer status;
}
