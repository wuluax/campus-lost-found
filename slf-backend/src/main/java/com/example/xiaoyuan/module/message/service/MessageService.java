package com.example.xiaoyuan.module.message.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.xiaoyuan.common.PageResp;
import com.example.xiaoyuan.module.message.dto.AdminMessageVO;
import com.example.xiaoyuan.module.message.dto.ConversationVO;
import com.example.xiaoyuan.module.message.dto.MessageVO;
import com.example.xiaoyuan.module.message.entity.PrivateMessage;

import java.util.List;

public interface MessageService extends IService<PrivateMessage> {

    /**
     * 发送私信，返回消息VO（含发送者信息）。
     */
    MessageVO sendMessage(Long senderId, Long receiverId, String content);

    /**
     * 获取当前用户的会话列表。
     */
    List<ConversationVO> getConversations(Long userId);

    /**
     * 获取与某个用户的聊天记录（分页）。
     */
    PageResp<MessageVO> getChatHistory(Long userId, Long otherUserId, int pageNo, int pageSize);

    /**
     * 将某个会话中自己的未读消息标记为已读。
     */
    void markAsRead(Long userId, Long otherUserId);

    /**
     * 获取当前用户未读消息总数。
     */
    int getUnreadCount(Long userId);

    /**
     * 管理端分页查询私信。
     */
    PageResp<AdminMessageVO> adminPage(int pageNo, int pageSize, Long senderId, Long receiverId, String content);

    /**
     * 管理端删除私信。
     */
    void adminDelete(Long messageId);
}
