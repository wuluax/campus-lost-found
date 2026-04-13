package com.example.xiaoyuan.module.item.dto;

import com.example.xiaoyuan.module.category.entity.Category;
import com.example.xiaoyuan.module.item.entity.Item;
import com.example.xiaoyuan.module.location.entity.Location;
import com.example.xiaoyuan.module.user.entity.User;
import lombok.Data;

import java.util.List;

/**
 * 管理端物品详情视图。
 */
@Data
public class AdminItemDetailVO {
    /**
     * 物品基础信息。
     */
    private Item item;

    /**
     * 发布者信息（已脱敏）。
     */
    private User publisher;

    /**
     * 分类信息。
     */
    private Category category;

    /**
     * 地点信息。
     */
    private Location location;

    /**
     * 发布者展示名。
     */
    private String publisherDisplay;

    /**
     * 分类展示名。
     */
    private String categoryDisplay;

    /**
     * 地点展示名。
     */
    private String locationDisplay;

    /**
     * 完整地点描述。
     */
    private String fullLocation;

    /**
     * 地点路径名称列表。
     */
    private List<String> locationPathNames;
}

