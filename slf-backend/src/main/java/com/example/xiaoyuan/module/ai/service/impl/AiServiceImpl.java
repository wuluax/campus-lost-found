package com.example.xiaoyuan.module.ai.service.impl;

import ai.z.openapi.service.model.ChatMessage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.xiaoyuan.common.BusinessException;
import com.example.xiaoyuan.common.ErrorCode;
import com.example.xiaoyuan.module.ai.dto.AiMessageVO;
import com.example.xiaoyuan.module.ai.dto.AiSendReq;
import com.example.xiaoyuan.module.ai.entity.AiMessage;
import com.example.xiaoyuan.module.ai.mapper.AiMessageMapper;
import com.example.xiaoyuan.module.ai.service.AiModelClient;
import com.example.xiaoyuan.module.ai.service.AiService;
import com.example.xiaoyuan.module.item.entity.Item;
import com.example.xiaoyuan.module.item.service.ItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AiServiceImpl extends ServiceImpl<AiMessageMapper, AiMessage> implements AiService {
    private static final String ROLE_USER = "0";
    private static final String ROLE_ASSISTANT = "1";
    private static final String MODEL_TEXT = "glm-4-flash";
    private static final String MODEL_VISION = "glm-4v-flash";

    @jakarta.annotation.Resource
    private ItemService itemService;
    @jakarta.annotation.Resource
    private ObjectMapper objectMapper;
    @jakarta.annotation.Resource
    private AiModelClient aiModelClient;

    //处理用户发送消息主流程
    @Override
    public List<AiMessageVO> send(Long userId, AiSendReq req) {
        if (!aiModelClient.configured()) {
            throw new BusinessException(ErrorCode.SERVER_ERROR, "未配置智谱 API Key");
        }
        boolean hasContent = StringUtils.hasText(req.getContent());
        boolean hasImage = StringUtils.hasText(req.getImageUrl());
        if (!hasContent && !hasImage && req.getItemId() == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "内容/图片/物品至少提供一个");
        }
        saveUserMessage(userId, req);
        
        // 1.物品关联匹配
        if (req.getItemId() != null) return replyByUserItem(userId, req.getItemId());
        
        // 2.图文消息匹配
        if (hasContent || hasImage) {
            String content = hasContent ? req.getContent() : "识别这张图片中的物品";
            Integer typeWanted = classifyTypeByAI(content);
            
            if (typeWanted == null) {
                return saveAssistantReply(userId, "你好！我是校园寻物助手。请问你是【丢了东西】想找回，还是【捡到了东西】想寻找失主？补充一下描述我能更精准地帮你。");
            }
            
            String tags = hasImage ? extractTagsFromMixed(content, req.getImageUrl()) : extractTagsFromText(content);
            if (!StringUtils.hasText(tags)) tags = content;
            
            List<Item> candidates = loadCandidates(typeWanted);
            return replyByMatchOnly(userId, "根据你提供的信息", typeWanted, tags, candidates);
        }
        
        return saveAssistantReply(userId, "你好，请提供更具体的描述或图片，以便我为你匹配。");
    }

    private void saveUserMessage(Long userId, AiSendReq req) {
        saveMessage(userId, ROLE_USER, req.getContent(), req.getImageUrl(), req.getItemId());
    }

    //根据用户物品ID回复匹配结果
    private List<AiMessageVO> replyByUserItem(Long userId, Long itemId) {
        Item a = itemService.getById(itemId);
        if (a == null) throw new BusinessException(ErrorCode.NOT_FOUND, "物品不存在");
        
        Integer oppositeType = a.getType() != null && a.getType() == 1 ? 0 : 1;
        List<Item> candidates = loadCandidates(oppositeType);
        
        String tagsText = StringUtils.hasText(a.getAiTags()) ? a.getAiTags() : a.getDescription();
        List<Map<String, Object>> top3 = matchByAiTags(tagsText, a.getDescription(), candidates);
        
        if (top3 != null && !top3.isEmpty()) {
            return buildReplyWithItems(userId, "根据你发布的物品", oppositeType, top3);
        }
        return saveAssistantReply(userId, composeMatchReply("根据你发布的物品", oppositeType, 0));
    }

    private List<AiMessageVO> replyByMatchOnly(Long userId, String scene, Integer typeWanted, String tags, List<Item> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return saveAssistantReply(userId, composeMatchReply(scene, typeWanted, 0));
        }
        List<Map<String, Object>> top3 = matchByAiTags(tags, null, candidates);
        if (top3 != null && !top3.isEmpty()) {
            return buildReplyWithItems(userId, scene, typeWanted, top3);
        }
        return saveAssistantReply(userId, composeMatchReply(scene, typeWanted, 0));
    }

    private List<Item> loadCandidates(Integer typeWanted) {
        return itemService.list(new QueryWrapper<Item>()
                .eq("status", 1)
                .eq("type", typeWanted)
                .isNotNull("ai_tags")
                .orderByDesc("create_time")
                .last("limit 80"));
    }

    //意图识别
    private Integer classifyTypeByAI(String content) {
        try {
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(ChatMessage.builder().role("system").content(
                "你是一个校园失物招领意图分析专家。\n" +
                "请分析用户输入，判断其意图属于以下哪一类：\n" +
                "1. lost: 用户丢失了东西，正在寻找（例如：‘我的钥匙丢了’、‘谁看到我的水杯了吗’）。\n" +
                "2. found: 用户捡到了东西，正在寻找失主（例如：‘食堂捡到一个钱包’、‘在操场看到一把伞’）。\n" +
                "3. none: 无法判断或只是普通打招呼。\n" +
                "严格输出JSON：{\"type\":\"lost|found|none\"}"
            ).build());
            messages.add(ChatMessage.builder().role("user").content(content).build());
            
            String c = aiModelClient.callModel(MODEL_TEXT, messages, true); 
            Map<?, ?> obj = objectMapper.readValue(extractJsonPayload(c), Map.class);
            String ts = String.valueOf(obj.get("type"));
            if ("lost".equalsIgnoreCase(ts)) return 1;
            if ("found".equalsIgnoreCase(ts)) return 0;
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    //纯文本标签提取
    private String extractTagsFromText(String text) {
        try {
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(ChatMessage.builder().role("system").content(
                "你是一个物品特征提取助手。请从用户描述中提取关键特征：\n" +
                "1. 核心品类（如：充电宝、雨伞、钥匙）\n" +
                "2. 颜色与材质\n" +
                "3. 品牌或Logo（如：华为、膳魔师）\n" +
                "4. 独有特征（如：挂坠、破损、贴纸）\n" +
                "输出：仅返回由中文逗号分隔的标签字符串，控制在10个词以内。"
            ).build());
            messages.add(ChatMessage.builder().role("user").content(text).build());
            return aiModelClient.callModel(MODEL_TEXT, messages, false);
        } catch (Exception e) {
            return null;
        }
    }

    //标签匹配
    private List<Map<String, Object>> matchByAiTags(String tagsOrText, String extraText, List<Item> candidates) {
        if (candidates == null || candidates.isEmpty()) return Collections.emptyList();
        try {
            List<Map<String, Object>> candidateList = candidates.stream()
                .map(it -> Map.<String, Object>of("id", it.getId(), "ai_tags", it.getAiTags()))
                .collect(Collectors.toList());
            
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(ChatMessage.builder()
            .role("system")
            .content("你是失物招领精准匹配助手。你的任务是对比【当前物品】与【候选列表】的相似度。\n" +
            "\n" +
            "评分严则（必须严格遵守）：\n" +
            "1. 数量硬性约束：候选列表只要有数据，你就返回最可能相似的物品（最多三个）并按相似度从高到低排序！\n" +
            "2. 品类一致性：如果一个是“杯子”，一个是“雨伞”，即便颜色、材质完全相同，相似度也不得超过 20 分。\n" +
            "3. 核心特征：优先匹配品牌（如膳魔师）、型号、特殊贴纸、破损位置等唯一性标识。\n" +
            "4. 基础属性：颜色、材质、大小仅作为辅助参考。\n" +
            "\n" +
            "等级标准：\n" +
            "- 90-100分：品类相同且品牌/特殊细节完全吻合。\n" +
            "- 70-89分：品类相同且颜色、外观高度吻合。\n" +
            "- 10-30分：品类不同，即便颜色相同也要打低分。\n" +
            "- 0分：完全不相关的物品。\n" +
            "\n" +
            "请严格输出JSON数组格式，例如：[{\"id\":1,\"score\":95},{\"id\":2,\"score\":30},{\"id\":3,\"score\":10}]。不要输出任何解释说明文字。")
            .build());
            
            String userContent = "【待匹配物品】：\n标签：" + tagsOrText + (extraText != null ? "\n描述：" + extraText : "") + 
                                 "\n\n【候选列表】：\n" + objectMapper.writeValueAsString(candidateList);
            messages.add(ChatMessage.builder().role("user").content(userContent).build());
            
            String c = aiModelClient.callModel(MODEL_TEXT, messages, true); 
            Object parsed = objectMapper.readValue(extractJsonPayload(c), Object.class);
            if (parsed instanceof List) return ((List<?>) parsed).stream().limit(3).map(e -> (Map<String, Object>) e).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("匹配失败", e);
        }
        return Collections.emptyList();
    }

    //图片标签提取
    private String extractTagsFromMixed(String text, String imageUrl) {
        try {
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(ChatMessage.builder().role("system").content(
                "你是一个视觉物品识别专家。请结合文字描述和图片，提取该物品的视觉标签。\n" +
                "特别关注：颜色、品牌、具体型号（如iPhone 15）、特殊的装饰物。\n" +
                "输出：仅返回中文逗号分隔的标签字符串。"
            ).build());
            messages.add(buildVisionUserMessage(text, imageUrl));
            return aiModelClient.callModel(MODEL_VISION, messages, false);
        } catch (Exception e) {
            return text;
        }
    }

    private ChatMessage buildVisionUserMessage(String text, String imageUrl) {
        List<Map<String, Object>> parts = new ArrayList<>();
        parts.add(Map.of("type", "text", "text", text));
        parts.add(Map.of("type", "image_url", "image_url", Map.of("url", imageUrl)));
        return ChatMessage.builder().role("user").content(parts).build();
    }

    private String extractJsonPayload(String raw) {
        if (!StringUtils.hasText(raw)) return "[]";
        String trimmed = raw.trim();
        int start = Math.min(
            trimmed.indexOf('[') == -1 ? Integer.MAX_VALUE : trimmed.indexOf('['),
            trimmed.indexOf('{') == -1 ? Integer.MAX_VALUE : trimmed.indexOf('{')
        );
        int end = Math.max(trimmed.lastIndexOf(']'), trimmed.lastIndexOf('}'));
        if (start != Integer.MAX_VALUE && end != -1 && end >= start) return trimmed.substring(start, end + 1);
        return trimmed.replace("```json", "").replace("```", "").trim();
    }

    private List<AiMessageVO> buildReplyWithItems(Long userId, String scene, Integer typeWanted, List<Map<String, Object>> top3) {
        String reply = composeMatchReply(scene, typeWanted, top3.size());
        saveMessage(userId, ROLE_ASSISTANT, reply, null, null);
        
        List<AiMessageVO> result = new ArrayList<>();
        result.add(toVO(this.list(new QueryWrapper<AiMessage>().eq("user_id", userId).orderByDesc("id").last("limit 1")).get(0)));
        
        for (Map<String, Object> it : top3) {
            try {
                Long itemId = Long.valueOf(String.valueOf(it.get("id")));
                String scoreJson = "{\"score\":" + Math.round(Double.parseDouble(String.valueOf(it.get("score")))) + "}";
                AiMessage m = saveMessage(userId, ROLE_ASSISTANT, scoreJson, null, itemId);
                result.add(toVO(m));
            } catch (Exception ignored) {}
        }
        return result;
    }

    private String composeMatchReply(String scene, Integer typeWanted, int count) {
        String typeStr = typeWanted == null ? "" : (typeWanted == 1 ? "招领列表" : "失物列表");
        if (count > 0) return "你好！" + scene + "，我在" + typeStr + "中为你找到了 " + count + " 件高度相关的物品，快看看是不是这些？";
        return "你好！" + scene + "，目前" + typeStr + "中还没有非常匹配的记录。我会持续为你关注，有消息第一时间通知你！";
    }

    private List<AiMessageVO> saveAssistantReply(Long userId, String content) {
        AiMessage aiMsg = saveMessage(userId, ROLE_ASSISTANT, content, null, null);
        return List.of(toVO(aiMsg));
    }

    private AiMessage saveMessage(Long userId, String role, String content, String imageUrl, Long itemId) {
        AiMessage msg = new AiMessage();
        msg.setUserId(userId);
        msg.setRole(role);
        msg.setContent(content);
        msg.setImageUrl(imageUrl);
        msg.setItemId(itemId);
        msg.setCreateTime(LocalDateTime.now());
        this.save(msg);
        return msg;
    }

    private AiMessageVO toVO(AiMessage m) {
        AiMessageVO vo = new AiMessageVO();
        vo.setId(m.getId());
        vo.setRole(m.getRole());
        vo.setContent(m.getContent());
        vo.setImageUrl(m.getImageUrl());
        vo.setItemId(m.getItemId());
        vo.setCreateTime(m.getCreateTime());
        return vo;
    }

    /**
     * 查询当前用户完整会话历史，按创建时间正序返回。
     */
    @Override
    public List<AiMessageVO> history(Long userId) {
        return this.list(new QueryWrapper<AiMessage>().eq("user_id", userId).orderByAsc("create_time")).stream().map(this::toVO).collect(Collectors.toList());
    }
}
