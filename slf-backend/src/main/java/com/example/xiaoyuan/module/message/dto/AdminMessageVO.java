package com.example.xiaoyuan.module.message.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminMessageVO {
    private Long id;
    private Long conversationId;
    private Long senderId;
    private String senderNickname;
    private Long receiverId;
    private String receiverNickname;
    private String content;
    private Integer isRead;
    private LocalDateTime createTime;
}
