package com.example.xiaoyuan.module.message.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("slf_conversation")
public class Conversation {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_a_id")
    private Long userAId;

    @TableField("user_b_id")
    private Long userBId;

    @TableField("last_message")
    private String lastMessage;

    @TableField("last_message_time")
    private LocalDateTime lastMessageTime;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
