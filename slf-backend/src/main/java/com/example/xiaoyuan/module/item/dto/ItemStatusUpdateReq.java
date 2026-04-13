package com.example.xiaoyuan.module.item.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 物品状态修改请求：支持将状态设置为 0（下架）或 2（已找回/已被认领）。
 */
@Data
public class ItemStatusUpdateReq {
    /**
     * 目标状态：0 下架/禁用，1 正常，2 完成。
     */
    @NotNull
    private Integer status;

}
