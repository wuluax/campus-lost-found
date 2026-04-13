package com.example.xiaoyuan.module.item.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 物品实体，对应表 slf_item。
 */
@Data
@TableName("slf_item")
public class Item {
    /**
     * 物品主键ID。
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发布者用户ID。
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 物品类型：0 失物，1 招领。
     */
    @TableField("type")
    private Integer type;

    /**
     * 物品描述。
     */
    @TableField("description")
    private String description;

    /**
     * 分类ID。
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 地点ID。
     */
    @TableField("location_id")
    private Long locationId;

    /**
     * 丢失/拾取时间。
     */
    @TableField("item_time")
    private LocalDateTime itemTime;

    /**
     * 图片地址。
     */
    @TableField("image_url")
    private String imageUrl;

    /**
     * 联系方式。
     */
    @TableField("contact")
    private String contact;

    /**
     * 物品状态：0 下架/禁用，1 正常，2 已完成。
     */
    @TableField("status")
    private Integer status;

    /**
     * AI 标签，逗号分隔。
     */
    @TableField("ai_tags")
    private String aiTags;

    /**
     * 创建时间。
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间。
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
