package com.example.xiaoyuan.module.message.controller;

import com.example.xiaoyuan.common.ApiResp;
import com.example.xiaoyuan.common.PageResp;
import com.example.xiaoyuan.common.UserContext;
import com.example.xiaoyuan.module.message.dto.ConversationVO;
import com.example.xiaoyuan.module.message.dto.MessageSendReq;
import com.example.xiaoyuan.module.message.dto.MessageVO;
import com.example.xiaoyuan.module.message.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * 发送私信（HTTP 方式，适用于不通过 WebSocket 的场景）。
     */
    @PostMapping
    public ApiResp<MessageVO> send(@Valid @RequestBody MessageSendReq req) {
        Long uid = UserContext.get();
        return ApiResp.ok(messageService.sendMessage(uid, req.getReceiverId(), req.getContent()));
    }

    /**
     * 获取当前用户的会话列表。
     */
    @GetMapping("/conversations")
    public ApiResp<List<ConversationVO>> conversations() {
        Long uid = UserContext.get();
        return ApiResp.ok(messageService.getConversations(uid));
    }

    /**
     * 获取与某个用户的聊天记录。
     */
    @GetMapping("/chat/{otherUserId}")
    public ApiResp<PageResp<MessageVO>> chatHistory(
            @PathVariable Long otherUserId,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "30") int pageSize) {
        Long uid = UserContext.get();
        return ApiResp.ok(messageService.getChatHistory(uid, otherUserId, pageNo, pageSize));
    }

    /**
     * 标记与某个用户的消息为已读。
     */
    @PutMapping("/read/{otherUserId}")
    public ApiResp<Void> markAsRead(@PathVariable Long otherUserId) {
        Long uid = UserContext.get();
        messageService.markAsRead(uid, otherUserId);
        return ApiResp.ok();
    }

    /**
     * 获取当前用户未读消息总数。
     */
    @GetMapping("/unread-count")
    public ApiResp<Integer> unreadCount() {
        Long uid = UserContext.get();
        return ApiResp.ok(messageService.getUnreadCount(uid));
    }
}
