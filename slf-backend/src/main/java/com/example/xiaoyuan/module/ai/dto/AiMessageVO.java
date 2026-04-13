package com.example.xiaoyuan.module.ai.dto;

import lombok.Data;

import java.time.LocalDateTime;

//AI 对话消息返回视图
@Data
public class AiMessageVO {

    private Long id;
    private String role; //角色：0=用户，"1"=AI
    private String content;
    private String imageUrl;
    private Long itemId;
    private LocalDateTime createTime;
}
