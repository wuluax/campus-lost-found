package com.example.xiaoyuan.module.ai.service;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ResponseFormat; // 确认引入
import com.example.xiaoyuan.common.BusinessException;
import com.example.xiaoyuan.common.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

//封装智谱 SDK 的鉴权、请求构建与响应内容提取，供业务服务复用。
@Component
public class AiModelClient {

    @Value("${zhipu.api.key:}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private volatile ZhipuAiClient client;

    //判断是否已配置 API Key）
    public boolean configured() {
        return StringUtils.hasText(apiKey);
    }

    //懒加载并复用智谱客户端实例，避免重复创建连接对象。
    private ZhipuAiClient getClient() {
        if (client != null) {
            return client;
        }
        synchronized (this) {
            if (client == null) {
                client = ZhipuAiClient.builder().ofZHIPU().apiKey(apiKey).build();
            }
        }
        return client;
    }

    //调用指定模型并返回文本内容。当 useJsonMode 为 true 时，会要求模型输出标准 JSON 对象。
    public String callModel(String model, List<ChatMessage> messages, boolean useJsonMode) {
        if (!configured()) {
            throw new BusinessException(ErrorCode.SERVER_ERROR, "未配置智谱 API Key");
        }
        if (messages == null || messages.isEmpty()) {
            return null;
        }

        ChatCompletionCreateParams.ChatCompletionCreateParamsBuilder builder = ChatCompletionCreateParams.builder()
                .model(model)
                .messages(messages);

        if (useJsonMode) {
            builder.responseFormat(ResponseFormat.builder()
                    .type("json_object")
                    .build());
        }

        ChatCompletionResponse response = getClient().chat().createChatCompletion(builder.build());

        if (response == null || !response.isSuccess()) {
            String msg = response == null ? "AI 服务无响应" : response.getMsg();
            throw new BusinessException(ErrorCode.SERVER_ERROR, "大模型调用失败: " + msg);
        }

        Map<?, ?> data = objectMapper.convertValue(response.getData(), Map.class);
        return extractContent(data);
    }

    //从智谱返回结构中提取主内容文本。
    @SuppressWarnings("unchecked")
    private String extractContent(Map<?, ?> data) {
        if (data == null) return null;
        Object choicesObj = data.get("choices");
        if (!(choicesObj instanceof List)) return null;
        List<?> choices = (List<?>) choicesObj;
        if (choices.isEmpty()) return null;
        Object first = choices.get(0);
        if (!(first instanceof Map)) return null;
        
        Map<String, Object> choice = (Map<String, Object>) first;
        Map<String, Object> message = (Map<String, Object>) choice.get("message");
        Object contentObj = message.get("content");
        
        if (contentObj instanceof String) {
            return (String) contentObj;
        }
        if (contentObj instanceof List) {
            StringBuilder sb = new StringBuilder();
            for (Object part : (List<?>) contentObj) {
                if (part instanceof Map && "text".equals(((Map<?, ?>) part).get("type"))) {
                    Object text = ((Map<?, ?>) part).get("text");
                    if (text instanceof String) sb.append((String) text);
                }
            }
            return sb.toString();
        }
        return null;
    }
}
