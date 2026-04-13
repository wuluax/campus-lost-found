package com.example.xiaoyuan.module.clue.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 线索实体，对应表 slf_clue。
 */
@Data
@TableName("slf_clue")
public class Clue {
    /**
     * 线索主键ID。
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联的物品ID。
     */
    @TableField("item_id")
    private Long itemId;

    /**
     * 发布线索的用户ID。
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 线索内容。
     */
    @TableField("content")
    private String content;

    /**
     * 线索创建时间。
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
