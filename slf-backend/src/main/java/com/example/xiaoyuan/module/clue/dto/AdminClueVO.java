package com.example.xiaoyuan.module.clue.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理端线索列表返回视图。
 */
@Data
public class AdminClueVO {
    /**
     * 线索ID。
     */
    private Long id;
    /**
     * 物品ID。
     */
    private Long itemId;
    /**
     * 线索用户ID。
     */
    private Long userId;
    /**
     * 用户昵称。
     */
    private String userNickname;
    /**
     * 线索内容。
     */
    private String content;
    /**
     * 线索创建时间。
     */
    private LocalDateTime createTime;
}
