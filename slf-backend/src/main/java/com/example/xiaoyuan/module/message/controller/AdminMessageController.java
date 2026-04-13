package com.example.xiaoyuan.module.message.controller;

import com.example.xiaoyuan.common.ApiResp;
import com.example.xiaoyuan.common.PageResp;
import com.example.xiaoyuan.module.message.dto.AdminMessageVO;
import com.example.xiaoyuan.module.message.service.MessageService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/messages")
public class AdminMessageController {

    private final MessageService messageService;

    public AdminMessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public ApiResp<PageResp<AdminMessageVO>> page(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "15") int pageSize,
            @RequestParam(required = false) Long senderId,
            @RequestParam(required = false) Long receiverId,
            @RequestParam(required = false) String content) {
        return ApiResp.ok(messageService.adminPage(pageNo, pageSize, senderId, receiverId, content));
    }

    @DeleteMapping("/{id}")
    public ApiResp<Void> delete(@PathVariable Long id) {
        messageService.adminDelete(id);
        return ApiResp.ok();
    }
}
