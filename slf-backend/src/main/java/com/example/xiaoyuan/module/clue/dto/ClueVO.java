package com.example.xiaoyuan.module.clue.dto;

import com.example.xiaoyuan.module.user.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户端线索返回视图。
 */
@Data
public class ClueVO {
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
     * 线索内容。
     */
    private String content;
    /**
     * 线索创建时间。
     */
    private LocalDateTime createTime;
    /**
     * 线索发布用户信息。
     */
    private User user;
}
