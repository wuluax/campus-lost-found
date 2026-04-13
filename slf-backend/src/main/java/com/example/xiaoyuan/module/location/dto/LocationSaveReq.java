package com.example.xiaoyuan.module.location.dto;

import com.example.xiaoyuan.common.NameStatusReq;
import lombok.Data;

/**
 * 地点新增/修改请求参数。
 */
@Data
public class LocationSaveReq implements NameStatusReq {
    /**
     * 地点名称。
     */
    private String name;
    /**
     * 状态：0 禁用，1 启用。
     */
    private Integer status;
}
