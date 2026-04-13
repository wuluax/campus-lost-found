package com.example.xiaoyuan.module.item.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理端物品列表视图。
 */
@Data
public class AdminItemListVO {
    /**
     * 物品ID。
     */
    private Long id;
    /**
     * 物品类型：0 失物，1 招领。
     */
    private Integer type;
    /**
     * 发布者用户ID。
     */
    private Long userId;
    /**
     * 发布者昵称。
     */
    private String publisherNickname;
    /**
     * 物品描述。
     */
    private String description;
    /**
     * 分类ID。
     */
    private Long categoryId;
    /**
     * 分类名称。
     */
    private String categoryName;
    /**
     * 地点ID。
     */
    private Long locationId;
    /**
     * 地点名称。
     */
    private String locationName;
    /**
     * 完整地点描述。
     */
    private String fullLocation;
    /**
     * 物品状态。
     */
    private Integer status;
    /**
     * 创建时间。
     */
    private LocalDateTime createTime;
}
