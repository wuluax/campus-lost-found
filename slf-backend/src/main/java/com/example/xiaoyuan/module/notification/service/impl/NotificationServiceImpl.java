package com.example.xiaoyuan.module.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.xiaoyuan.common.BusinessException;
import com.example.xiaoyuan.common.ErrorCode;
import com.example.xiaoyuan.common.PageResp;
import com.example.xiaoyuan.module.message.ws.WsSessionManager;
import com.example.xiaoyuan.module.notification.dto.AdminNotificationVO;
import com.example.xiaoyuan.module.notification.dto.NotificationVO;
import com.example.xiaoyuan.module.notification.entity.Notification;
import com.example.xiaoyuan.module.notification.mapper.NotificationMapper;
import com.example.xiaoyuan.module.notification.service.NotificationService;
import com.example.xiaoyuan.module.user.entity.User;
import com.example.xiaoyuan.module.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final UserService userService;
    private final WsSessionManager wsSessionManager;

    public NotificationServiceImpl(UserService userService, WsSessionManager wsSessionManager) {
        this.userService = userService;
        this.wsSessionManager = wsSessionManager;
    }

    @Override
    public void createAndPush(Long userId, String type, String title, String content, Long relatedId, Long senderId) {
        // 不给自己发通知
        if (userId.equals(senderId)) return;

        Notification n = new Notification();
        n.setUserId(userId);
        n.setType(type);
        n.setTitle(title);
        n.setContent(content != null && content.length() > 100 ? content.substring(0, 100) : content);
        n.setRelatedId(relatedId);
        n.setSenderId(senderId);
        n.setIsRead(0);
        this.save(n);

        // 通过 WebSocket 实时推送通知
        try {
            NotificationVO vo = toVO(n);
            if (senderId != null) {
                User sender = userService.getById(senderId);
                if (sender != null) {
                    vo.setSenderNickname(sender.getNickname());
                    vo.setSenderAvatar(sender.getAvatar());
                }
            }
            // 包装为通知类型的推送，前端通过 type 字段区分
            Map<String, Object> push = new HashMap<>();
            push.put("pushType", "notification");
            push.put("notification", vo);
            wsSessionManager.pushToUser(userId, push);
        } catch (Exception e) {
            log.warn("通知推送失败，userId={}", userId, e);
        }
    }

    @Override
    public PageResp<NotificationVO> pageByUser(Long userId, int pageNo, int pageSize) {
        Page<Notification> page = this.page(
                new Page<>(pageNo, pageSize),
                new QueryWrapper<Notification>()
                        .eq("user_id", userId)
                        .orderByDesc("create_time"));

        List<Notification> records = page.getRecords();
        if (records.isEmpty()) return PageResp.of(0, Collections.emptyList());

        Set<Long> senderIds = records.stream()
                .map(Notification::getSenderId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, User> userMap = senderIds.isEmpty() ? Collections.emptyMap() : userService.mapByIds(senderIds, true);

        List<NotificationVO> voList = new ArrayList<>();
        for (Notification n : records) {
            NotificationVO vo = toVO(n);
            if (n.getSenderId() != null) {
                User sender = userMap.get(n.getSenderId());
                if (sender != null) {
                    vo.setSenderNickname(sender.getNickname());
                    vo.setSenderAvatar(sender.getAvatar());
                }
            }
            voList.add(vo);
        }
        return PageResp.of(page.getTotal(), voList);
    }

    @Override
    public int getUnreadCount(Long userId) {
        return (int) this.count(new QueryWrapper<Notification>()
                .eq("user_id", userId)
                .eq("is_read", 0));
    }

    @Override
    public void markAsRead(Long userId, Long notificationId) {
        Notification n = this.getById(notificationId);
        if (n == null) return;
        if (!n.getUserId().equals(userId)) return;
        n.setIsRead(1);
        this.updateById(n);
    }

    @Override
    public void markAllAsRead(Long userId) {
        this.update(new UpdateWrapper<Notification>()
                .eq("user_id", userId)
                .eq("is_read", 0)
                .set("is_read", 1));
    }

    @Override
    public PageResp<AdminNotificationVO> adminPage(int pageNo, int pageSize, Long userId, String type, String content) {
        QueryWrapper<Notification> qw = new QueryWrapper<Notification>()
                .eq(userId != null, "user_id", userId)
                .eq(StringUtils.hasText(type), "type", type)
                .like(StringUtils.hasText(content), "content", content)
                .orderByDesc("create_time");

        Page<Notification> page = this.page(new Page<>(pageNo, pageSize), qw);
        List<Notification> records = page.getRecords();
        if (records.isEmpty()) return PageResp.of(0, Collections.emptyList());

        Set<Long> userIds = new HashSet<>();
        for (Notification n : records) {
            userIds.add(n.getUserId());
            if (n.getSenderId() != null) userIds.add(n.getSenderId());
        }
        Map<Long, User> userMap = userService.mapByIds(userIds, true);

        List<AdminNotificationVO> voList = new ArrayList<>();
        for (Notification n : records) {
            AdminNotificationVO vo = new AdminNotificationVO();
            vo.setId(n.getId());
            vo.setUserId(n.getUserId());
            vo.setType(n.getType());
            vo.setTitle(n.getTitle());
            vo.setContent(n.getContent());
            vo.setRelatedId(n.getRelatedId());
            vo.setSenderId(n.getSenderId());
            vo.setIsRead(n.getIsRead());
            vo.setCreateTime(n.getCreateTime());
            User user = userMap.get(n.getUserId());
            vo.setUserNickname(user != null ? user.getNickname() : "未知");
            if (n.getSenderId() != null) {
                User sender = userMap.get(n.getSenderId());
                vo.setSenderNickname(sender != null ? sender.getNickname() : "未知");
            }
            voList.add(vo);
        }
        return PageResp.of(page.getTotal(), voList);
    }

    @Override
    public void adminDelete(Long notificationId) {
        Notification n = this.getById(notificationId);
        if (n == null) throw new BusinessException(ErrorCode.NOT_FOUND, "通知不存在");
        this.removeById(notificationId);
    }

    private NotificationVO toVO(Notification n) {
        NotificationVO vo = new NotificationVO();
        vo.setId(n.getId());
        vo.setUserId(n.getUserId());
        vo.setType(n.getType());
        vo.setTitle(n.getTitle());
        vo.setContent(n.getContent());
        vo.setRelatedId(n.getRelatedId());
        vo.setSenderId(n.getSenderId());
        vo.setIsRead(n.getIsRead());
        vo.setCreateTime(n.getCreateTime());
        return vo;
    }
}
