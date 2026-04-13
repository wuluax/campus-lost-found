package com.example.xiaoyuan.module.item.service;

import com.example.xiaoyuan.module.item.entity.Item;

/**
 * 物品 AI 增强服务。
 * 负责根据物品文本/图片信息生成结构化标签并回写。
 */
public interface ItemAiService {
    /**
     * 为物品生成并更新 AI 标签。
     */
    void generateAndUpdateAiTags(Item item);
}
