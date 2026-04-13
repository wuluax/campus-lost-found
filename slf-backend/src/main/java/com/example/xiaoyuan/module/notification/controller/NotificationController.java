package com.example.xiaoyuan.module.notification.controller;

import com.example.xiaoyuan.common.ApiResp;
import com.example.xiaoyuan.common.PageResp;
import com.example.xiaoyuan.common.UserContext;
import com.example.xiaoyuan.module.notification.dto.NotificationVO;
import com.example.xiaoyuan.module.notification.service.NotificationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * 获取当前用户的通知列表。
     */
    @GetMapping
    public ApiResp<PageResp<NotificationVO>> list(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        Long uid = UserContext.get();
        return ApiResp.ok(notificationService.pageByUser(uid, pageNo, pageSize));
    }

    /**
     * 获取未读通知数。
     */
    @GetMapping("/unread-count")
    public ApiResp<Integer> unreadCount() {
        Long uid = UserContext.get();
        return ApiResp.ok(notificationService.getUnreadCount(uid));
    }

    /**
     * 将单条通知标记为已读。
     */
    @PutMapping("/{id}/read")
    public ApiResp<Void> markAsRead(@PathVariable Long id) {
        Long uid = UserContext.get();
        notificationService.markAsRead(uid, id);
        return ApiResp.ok();
    }

    /**
     * 全部标记为已读。
     */
    @PutMapping("/read-all")
    public ApiResp<Void> markAllAsRead() {
        Long uid = UserContext.get();
        notificationService.markAllAsRead(uid);
        return ApiResp.ok();
    }
}
