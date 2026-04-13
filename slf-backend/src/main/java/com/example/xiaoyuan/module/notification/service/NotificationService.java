package com.example.xiaoyuan.module.notification.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.xiaoyuan.common.PageResp;
import com.example.xiaoyuan.module.notification.dto.AdminNotificationVO;
import com.example.xiaoyuan.module.notification.dto.NotificationVO;
import com.example.xiaoyuan.module.notification.entity.Notification;

public interface NotificationService extends IService<Notification> {

    /**
     * 创建通知并通过 WebSocket 实时推送。
     *
     * @param userId    接收者
     * @param type      类型：clue / favorite / message / system
     * @param title     标题
     * @param content   内容摘要
     * @param relatedId 关联对象ID
     * @param senderId  触发者ID
     */
    void createAndPush(Long userId, String type, String title, String content, Long relatedId, Long senderId);

    /**
     * 获取当前用户的通知列表（分页）。
     */
    PageResp<NotificationVO> pageByUser(Long userId, int pageNo, int pageSize);

    /**
     * 获取当前用户未读通知数。
     */
    int getUnreadCount(Long userId);

    /**
     * 将单条通知标记为已读。
     */
    void markAsRead(Long userId, Long notificationId);

    /**
     * 将当前用户全部通知标记为已读。
     */
    void markAllAsRead(Long userId);

    /**
     * 管理端分页查询通知。
     */
    PageResp<AdminNotificationVO> adminPage(int pageNo, int pageSize, Long userId, String type, String content);

    /**
     * 管理端删除通知。
     */
    void adminDelete(Long notificationId);
}
