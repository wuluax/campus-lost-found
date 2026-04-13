package com.example.xiaoyuan.module.message.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 会话管理器：维护在线用户连接，提供推送能力。
 * 独立于业务 Service，避免循环依赖。
 */
@Component
public class WsSessionManager {

    private static final Logger log = LoggerFactory.getLogger(WsSessionManager.class);
    private final Map<Long, WebSocketSession> onlineSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public WsSessionManager(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void register(Long userId, WebSocketSession session) {
        onlineSessions.put(userId, session);
    }

    public void remove(Long userId) {
        onlineSessions.remove(userId);
    }

    public boolean isOnline(Long userId) {
        WebSocketSession s = onlineSessions.get(userId);
        return s != null && s.isOpen();
    }

    /**
     * 向指定用户推送 JSON 数据。
     */
    public void pushToUser(Long userId, Object data) {
        WebSocketSession s = onlineSessions.get(userId);
        if (s != null && s.isOpen()) {
            try {
                s.sendMessage(new TextMessage(objectMapper.writeValueAsString(data)));
            } catch (IOException e) {
                log.warn("WebSocket 推送失败，userId={}", userId, e);
            }
        }
    }

    /**
     * 向指定用户发送已序列化的文本消息。
     */
    public void sendText(Long userId, String json) {
        WebSocketSession s = onlineSessions.get(userId);
        if (s != null && s.isOpen()) {
            try {
                s.sendMessage(new TextMessage(json));
            } catch (IOException e) {
                log.warn("WebSocket 发送失败，userId={}", userId, e);
            }
        }
    }
}
