package com.example.xiaoyuan.module.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.xiaoyuan.common.BusinessException;
import com.example.xiaoyuan.common.ErrorCode;
import com.example.xiaoyuan.common.PageResp;
import com.example.xiaoyuan.module.message.dto.AdminMessageVO;
import com.example.xiaoyuan.module.message.dto.ConversationVO;
import com.example.xiaoyuan.module.message.dto.MessageVO;
import com.example.xiaoyuan.module.message.entity.Conversation;
import com.example.xiaoyuan.module.message.entity.PrivateMessage;
import com.example.xiaoyuan.module.message.mapper.ConversationMapper;
import com.example.xiaoyuan.module.message.mapper.PrivateMessageMapper;
import com.example.xiaoyuan.module.message.service.MessageService;
import com.example.xiaoyuan.module.notification.service.NotificationService;
import com.example.xiaoyuan.module.user.entity.User;
import com.example.xiaoyuan.module.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl extends ServiceImpl<PrivateMessageMapper, PrivateMessage> implements MessageService {

    private final ConversationMapper conversationMapper;
    private final UserService userService;
    private final NotificationService notificationService;

    public MessageServiceImpl(ConversationMapper conversationMapper, UserService userService, NotificationService notificationService) {
        this.conversationMapper = conversationMapper;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @Override
    public MessageVO sendMessage(Long senderId, Long receiverId, String content) {
        if (senderId.equals(receiverId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "不能给自己发私信");
        }
        // 校验接收者存在
        User receiver = userService.getById(receiverId);
        if (receiver == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }

        // 查找或创建会话（userAId 取较小值，保证唯一性）
        Long userAId = Math.min(senderId, receiverId);
        Long userBId = Math.max(senderId, receiverId);
        Conversation conv = conversationMapper.selectOne(
                new QueryWrapper<Conversation>()
                        .eq("user_a_id", userAId)
                        .eq("user_b_id", userBId));
        if (conv == null) {
            conv = new Conversation();
            conv.setUserAId(userAId);
            conv.setUserBId(userBId);
            conv.setLastMessage(content.length() > 100 ? content.substring(0, 100) : content);
            conv.setLastMessageTime(LocalDateTime.now());
            conversationMapper.insert(conv);
        } else {
            conv.setLastMessage(content.length() > 100 ? content.substring(0, 100) : content);
            conv.setLastMessageTime(LocalDateTime.now());
            conversationMapper.updateById(conv);
        }

        // 插入消息
        PrivateMessage msg = new PrivateMessage();
        msg.setConversationId(conv.getId());
        msg.setSenderId(senderId);
        msg.setReceiverId(receiverId);
        msg.setContent(content);
        msg.setIsRead(0);
        this.save(msg);

        // 构建 VO
        User sender = userService.getById(senderId);
        MessageVO vo = toMessageVO(msg);
        vo.setSenderNickname(sender.getNickname());
        vo.setSenderAvatar(sender.getAvatar());

        // 通知接收者有新私信
        notificationService.createAndPush(
                receiverId,
                "message",
                (sender.getNickname() != null ? sender.getNickname() : "用户") + " 给你发了一条私信",
                content.length() > 50 ? content.substring(0, 50) : content,
                conv.getId(),
                senderId
        );

        return vo;
    }

    @Override
    public List<ConversationVO> getConversations(Long userId) {
        List<Conversation> convs = conversationMapper.selectList(
                new QueryWrapper<Conversation>()
                        .eq("user_a_id", userId)
                        .or()
                        .eq("user_b_id", userId)
                        .orderByDesc("last_message_time"));

        if (convs.isEmpty()) return Collections.emptyList();

        // 收集对方用户ID
        Set<Long> otherUserIds = new HashSet<>();
        for (Conversation c : convs) {
            otherUserIds.add(c.getUserAId().equals(userId) ? c.getUserBId() : c.getUserAId());
        }
        Map<Long, User> userMap = userService.mapByIds(otherUserIds, true);

        List<ConversationVO> result = new ArrayList<>();
        for (Conversation c : convs) {
            Long otherUserId = c.getUserAId().equals(userId) ? c.getUserBId() : c.getUserAId();
            User otherUser = userMap.get(otherUserId);

            ConversationVO vo = new ConversationVO();
            vo.setId(c.getId());
            vo.setOtherUserId(otherUserId);
            vo.setOtherUserNickname(otherUser != null ? otherUser.getNickname() : "未知用户");
            vo.setOtherUserAvatar(otherUser != null ? otherUser.getAvatar() : null);
            vo.setLastMessage(c.getLastMessage());
            vo.setLastMessageTime(c.getLastMessageTime());

            // 统计未读数
            Long unread = this.count(new QueryWrapper<PrivateMessage>()
                    .eq("conversation_id", c.getId())
                    .eq("receiver_id", userId)
                    .eq("is_read", 0));
            vo.setUnreadCount(unread.intValue());
            result.add(vo);
        }
        return result;
    }

    @Override
    public PageResp<MessageVO> getChatHistory(Long userId, Long otherUserId, int pageNo, int pageSize) {
        Long userAId = Math.min(userId, otherUserId);
        Long userBId = Math.max(userId, otherUserId);
        Conversation conv = conversationMapper.selectOne(
                new QueryWrapper<Conversation>()
                        .eq("user_a_id", userAId)
                        .eq("user_b_id", userBId));
        if (conv == null) {
            return PageResp.of(0, Collections.emptyList());
        }

        Page<PrivateMessage> page = this.page(
                new Page<>(pageNo, pageSize),
                new QueryWrapper<PrivateMessage>()
                        .eq("conversation_id", conv.getId())
                        .orderByDesc("create_time"));

        // 批量获取用户信息
        Set<Long> userIds = page.getRecords().stream()
                .map(PrivateMessage::getSenderId)
                .collect(Collectors.toSet());
        Map<Long, User> userMap = userService.mapByIds(userIds, true);

        List<MessageVO> voList = new ArrayList<>();
        for (PrivateMessage m : page.getRecords()) {
            MessageVO vo = toMessageVO(m);
            User sender = userMap.get(m.getSenderId());
            if (sender != null) {
                vo.setSenderNickname(sender.getNickname());
                vo.setSenderAvatar(sender.getAvatar());
            }
            voList.add(vo);
        }
        return PageResp.of(page.getTotal(), voList);
    }

    @Override
    public void markAsRead(Long userId, Long otherUserId) {
        Long userAId = Math.min(userId, otherUserId);
        Long userBId = Math.max(userId, otherUserId);
        Conversation conv = conversationMapper.selectOne(
                new QueryWrapper<Conversation>()
                        .eq("user_a_id", userAId)
                        .eq("user_b_id", userBId));
        if (conv == null) return;

        this.update(new UpdateWrapper<PrivateMessage>()
                .eq("conversation_id", conv.getId())
                .eq("receiver_id", userId)
                .eq("is_read", 0)
                .set("is_read", 1));
    }

    @Override
    public int getUnreadCount(Long userId) {
        Long count = this.count(new QueryWrapper<PrivateMessage>()
                .eq("receiver_id", userId)
                .eq("is_read", 0));
        return count.intValue();
    }

    @Override
    public PageResp<AdminMessageVO> adminPage(int pageNo, int pageSize, Long senderId, Long receiverId, String content) {
        QueryWrapper<PrivateMessage> qw = new QueryWrapper<PrivateMessage>()
                .eq(senderId != null, "sender_id", senderId)
                .eq(receiverId != null, "receiver_id", receiverId)
                .like(StringUtils.hasText(content), "content", content)
                .orderByDesc("create_time");

        Page<PrivateMessage> page = this.page(new Page<>(pageNo, pageSize), qw);

        Set<Long> userIds = new HashSet<>();
        for (PrivateMessage m : page.getRecords()) {
            userIds.add(m.getSenderId());
            userIds.add(m.getReceiverId());
        }
        Map<Long, User> userMap = userService.mapByIds(userIds, true);

        List<AdminMessageVO> voList = new ArrayList<>();
        for (PrivateMessage m : page.getRecords()) {
            AdminMessageVO vo = new AdminMessageVO();
            vo.setId(m.getId());
            vo.setConversationId(m.getConversationId());
            vo.setSenderId(m.getSenderId());
            vo.setReceiverId(m.getReceiverId());
            vo.setContent(m.getContent());
            vo.setIsRead(m.getIsRead());
            vo.setCreateTime(m.getCreateTime());
            User sender = userMap.get(m.getSenderId());
            User receiver = userMap.get(m.getReceiverId());
            vo.setSenderNickname(sender != null ? sender.getNickname() : "未知");
            vo.setReceiverNickname(receiver != null ? receiver.getNickname() : "未知");
            voList.add(vo);
        }
        return PageResp.of(page.getTotal(), voList);
    }

    @Override
    public void adminDelete(Long messageId) {
        PrivateMessage msg = this.getById(messageId);
        if (msg == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "消息不存在");
        }
        this.removeById(messageId);
    }

    private MessageVO toMessageVO(PrivateMessage m) {
        MessageVO vo = new MessageVO();
        vo.setId(m.getId());
        vo.setConversationId(m.getConversationId());
        vo.setSenderId(m.getSenderId());
        vo.setReceiverId(m.getReceiverId());
        vo.setContent(m.getContent());
        vo.setIsRead(m.getIsRead());
        vo.setCreateTime(m.getCreateTime());
        return vo;
    }
}
