package com.example.xiaoyuan.module.favorite.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收藏实体，对应表 slf_favorite。
 */
@Data
@TableName("slf_favorite")
public class Favorite {
    /**
     * 主键ID。
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 收藏用户ID。
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 被收藏物品ID。
     */
    @TableField("item_id")
    private Long itemId;

    /**
     * 收藏时间。
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
