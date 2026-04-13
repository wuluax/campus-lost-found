package com.example.xiaoyuan.module.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("slf_ai_message")
public class AiMessage {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("role")
    private String role;

    @TableField("content")
    private String content;

    @TableField("item_id")
    private Long itemId;

    @TableField("image_url")
    private String imageUrl;

    @TableField("create_time")
    private LocalDateTime createTime;
}
