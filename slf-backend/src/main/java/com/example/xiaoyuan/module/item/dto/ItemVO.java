package com.example.xiaoyuan.module.item.dto;

import com.example.xiaoyuan.module.item.entity.Item;
import com.example.xiaoyuan.module.user.entity.User;
import lombok.Data;


/**
 * 物品详情/列表返回视图。
 */
@Data
public class ItemVO {
    /**
     * 物品基础信息。
     */
    private Item item;
    /**
     * 发布者信息（已脱敏）。
     */
    private User user;
}
