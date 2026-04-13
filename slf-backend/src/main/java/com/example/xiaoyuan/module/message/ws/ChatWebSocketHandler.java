package com.example.xiaoyuan.module.message.ws;

import com.example.xiaoyuan.module.message.dto.MessageVO;
import com.example.xiaoyuan.module.message.service.MessageService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * WebSocket 处理器：处理私信发送与实时推送。
 * 会话管理委托给 WsSessionManager，避免循环依赖。
 */
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(ChatWebSocketHandler.class);

    private final MessageService messageService;
    private final ObjectMapper objectMapper;
    private final WsSessionManager sessionManager;

    public ChatWebSocketHandler(MessageService messageService, ObjectMapper objectMapper, WsSessionManager sessionManager) {
        this.messageService = messageService;
        this.objectMapper = objectMapper;
        this.sessionManager = sessionManager;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long userId = getUserId(session);
        if (userId != null) {
            sessionManager.register(userId, session);
            log.info("WebSocket 连接建立，userId={}", userId);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long senderId = getUserId(session);
        if (senderId == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        JsonNode node = objectMapper.readTree(message.getPayload());
        Long receiverId = node.get("receiverId").asLong();
        String content = node.get("content").asText();

        // 通过 Service 持久化消息
        MessageVO vo = messageService.sendMessage(senderId, receiverId, content);
        String json = objectMapper.writeValueAsString(vo);

        // 推送给接收者（如果在线）
        sessionManager.sendText(receiverId, json);

        // 回送给发送者确认
        session.sendMessage(new TextMessage(json));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = getUserId(session);
        if (userId != null) {
            sessionManager.remove(userId);
            log.info("WebSocket 连接关闭，userId={}", userId);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        Long userId = getUserId(session);
        log.error("WebSocket 传输错误，userId={}", userId, exception);
        if (userId != null) {
            sessionManager.remove(userId);
        }
    }

    private Long getUserId(WebSocketSession session) {
        return (Long) session.getAttributes().get("userId");
    }
}
