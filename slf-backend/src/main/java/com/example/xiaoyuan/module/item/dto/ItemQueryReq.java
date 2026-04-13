package com.example.xiaoyuan.module.item.dto;

import com.example.xiaoyuan.common.PageParam;
import lombok.Data;

/**
 * 物品列表筛选请求。
 *
 * 说明：
 * - 用户端已移除“发布日期范围/丢拾日期范围”的筛选入口，因此这里不再暴露对应查询字段
 * - 用户端分类/地点筛选已改为“严格单选”，因此后端也移除多地点筛选字段，避免接口冗余
 * - 保留高频筛选：类型、分类、地点、关键字，以及排序字段
 */
@Data
public class ItemQueryReq extends PageParam {
    /**
     * 发布者用户ID（可选）。
     */
    private Long userId;
    /**
     * 是否包含所有状态（用于本人查看）。
     */
    private Boolean includeAllStatus;
    /**
     * 物品类型：0 失物，1 招领。
     */
    private Integer type;
    /**
     * 分类ID。
     */
    private Long categoryId;
    /**
     * 地点ID。
     */
    private Long locationId;
    /**
     * 排序字段：createTime 或 itemTime。
     */
    private String sortField;
    /**
     * 排序方式：asc 或 desc。
     */
    private String sortOrder;
    /**
     * 描述关键字检索。
     */
    private String keyword;
}
