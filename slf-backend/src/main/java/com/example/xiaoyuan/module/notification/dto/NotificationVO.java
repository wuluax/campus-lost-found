package com.example.xiaoyuan.module.notification.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationVO {
    private Long id;
    private Long userId;
    private String type;
    private String title;
    private String content;
    private Long relatedId;
    private Long senderId;
    private String senderNickname;
    private String senderAvatar;
    private Integer isRead;
    private LocalDateTime createTime;
}
