package com.example.xiaoyuan.module.notification.controller;

import com.example.xiaoyuan.common.ApiResp;
import com.example.xiaoyuan.common.PageResp;
import com.example.xiaoyuan.module.notification.dto.AdminNotificationVO;
import com.example.xiaoyuan.module.notification.service.NotificationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/notifications")
public class AdminNotificationController {

    private final NotificationService notificationService;

    public AdminNotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ApiResp<PageResp<AdminNotificationVO>> page(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "15") int pageSize,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String content) {
        return ApiResp.ok(notificationService.adminPage(pageNo, pageSize, userId, type, content));
    }

    @DeleteMapping("/{id}")
    public ApiResp<Void> delete(@PathVariable Long id) {
        notificationService.adminDelete(id);
        return ApiResp.ok();
    }
}
