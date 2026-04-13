package com.example.xiaoyuan.module.category.dto;

import com.example.xiaoyuan.common.NameStatusReq;
import lombok.Data;

/**
 * 分类新增/修改请求参数。
 */
@Data
public class CategorySaveReq implements NameStatusReq {
    /**
     * 分类名称。
     */
    private String name;
    /**
     * 状态：0 禁用，1 启用。
     */
    private Integer status;
}
