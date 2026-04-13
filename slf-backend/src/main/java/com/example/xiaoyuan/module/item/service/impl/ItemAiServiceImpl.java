package com.example.xiaoyuan.module.item.service.impl;

import ai.z.openapi.service.model.ChatMessage;
import com.example.xiaoyuan.module.ai.service.AiModelClient;
import com.example.xiaoyuan.module.category.entity.Category;
import com.example.xiaoyuan.module.category.service.CategoryService;
import com.example.xiaoyuan.module.item.entity.Item;
import com.example.xiaoyuan.module.item.mapper.ItemMapper;
import com.example.xiaoyuan.module.item.service.ItemAiService;
import com.example.xiaoyuan.module.location.entity.Location;
import com.example.xiaoyuan.module.location.service.LocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemAiServiceImpl implements ItemAiService {
    private static final String MODEL_TEXT = "glm-4-flash";
    private static final String MODEL_VISION = "glm-4v-flash";

    @jakarta.annotation.Resource
    private ItemMapper itemMapper;
    @jakarta.annotation.Resource
    private CategoryService categoryService;
    @jakarta.annotation.Resource
    private LocationService locationService;
    @jakarta.annotation.Resource
    private AiModelClient aiModelClient;

    //生成并回写物品 AI 标签。
    @Override
    public void generateAndUpdateAiTags(Item item) {
        if (item == null || item.getId() == null || !aiModelClient.configured()) {
            return;
        }
        try {
            boolean hasImage = StringUtils.hasText(item.getImageUrl());
            String model = hasImage ? MODEL_VISION : MODEL_TEXT;
            
            // 调用模型获取标签
            String tags = aiModelClient.callModel(model, buildMessages(item, hasImage), false);
            
            if (StringUtils.hasText(tags)) {
                tags = sanitizeTags(tags);
                updateItemTags(item.getId(), tags);
            } else {
                throw new RuntimeException("AI返回标签为空");
            }
        } catch (Exception e) {
            log.error("为物品ID: {} 生成AI标签失败: {}", item.getId(), e.getMessage());
            // 兜底回写，防止前端无限轮询
            updateItemTags(item.getId(), "暂无智能标签");
        }
    }

    //更新指定物品的 ai_tags 字段。
    private void updateItemTags(Long id, String tags) {
        Item toUpdate = new Item();
        toUpdate.setId(id);
        toUpdate.setAiTags(tags);
        itemMapper.updateById(toUpdate);
    }

    //提取标签
    private List<ChatMessage> buildMessages(Item item, boolean hasImage) {
        List<ChatMessage> messages = new ArrayList<>();
        
        String systemPrompt = "你是一个物品特征提取助手。请根据提供的文本描述和图片（如果有），提取关键特征：\n" +
                "1. 核心品类（如：充电宝、雨伞、钥匙）\n" +
                "2. 颜色与材质\n" +
                "3. 品牌或Logo（如：华为、膳魔师）\n" +
                "4. 独有特征（如：挂坠、破损、贴纸）\n" +
                "输出：仅返回由中文逗号分隔的标签字符串，控制在10个词以内。";

        messages.add(ChatMessage.builder().role("system").content(systemPrompt).build());

        String contextText = buildTagText(item);

        if (!hasImage) {
            messages.add(ChatMessage.builder().role("user").content(contextText).build());
        } else {
            List<Map<String, Object>> parts = new ArrayList<>();
            parts.add(Map.of("type", "text", "text", contextText));
            parts.add(Map.of("type", "image_url", "image_url", Map.of("url", normalizeImageUrl(item.getImageUrl()))));
            messages.add(ChatMessage.builder().role("user").content(parts).build());
        }
        return messages;
    }

    //构造用于标签提取的文本上下文。
    private String buildTagText(Item item) {
        StringBuilder sb = new StringBuilder();
        sb.append("【待提取信息】\n");
        sb.append("描述：").append(StringUtils.hasText(item.getDescription()) ? item.getDescription() : "无").append("\n");
        
        String catName = resolveCategoryName(item);
        if (StringUtils.hasText(catName)) sb.append("参考分类：").append(catName).append("\n");
        
        String locName = resolveLocationName(item);
        if (StringUtils.hasText(locName)) sb.append("发现地点：").append(locName).append("\n");
        
        return sb.toString();
    }

    //读取分类名称，作为模型提示词上下文补充。
    private String resolveCategoryName(Item item) {
        if (item.getCategoryId() == null) return null;
        Category c = categoryService.getById(item.getCategoryId());
        return c == null ? null : c.getName();
    }

    //读取地点名称，作为模型提示词上下文补充。
    private String resolveLocationName(Item item) {
        if (item.getLocationId() == null) return null;
        Location l = locationService.getById(item.getLocationId());
        return l == null ? null : l.getName();
    }

    //规范化图片地址，避免多余空白导致模型读取失败。
    private String normalizeImageUrl(String imageUrl) {
        if (!StringUtils.hasText(imageUrl)) return imageUrl;
        return imageUrl.trim();
    }

    //标签清洗：统一使用中文逗号，去重，限制数量
    private String sanitizeTags(String s) {
        if (!StringUtils.hasText(s)) return null;
        // 移除换行，统一分隔符
        s = s.replaceAll("[\r\n]", " ").replace(',', '，');
        
        String[] arr = s.split("，");
        return Arrays.stream(arr)
                .map(String::trim)
                .filter(StringUtils::hasText)
                .distinct()
                .limit(10)
                .collect(Collectors.joining("，"));
    }
}
