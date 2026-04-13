package com.example.xiaoyuan.module.notification.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminNotificationVO {
    private Long id;
    private Long userId;
    private String userNickname;
    private String type;
    private String title;
    private String content;
    private Long relatedId;
    private Long senderId;
    private String senderNickname;
    private Integer isRead;
    private LocalDateTime createTime;
}
