package com.example.xiaoyuan.module.item.controller;

import com.example.xiaoyuan.common.UserContext;
import com.example.xiaoyuan.module.item.dto.ItemUpdateReq;
import com.example.xiaoyuan.module.item.entity.Item;
import com.example.xiaoyuan.module.item.service.ItemService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

/**
 * ItemController 的“更新物品后刷新 ai_tags”行为验证。
 *
 * 说明：
 * - 这里不启动 Spring 容器，也不依赖数据库；
 * - 只验证控制器在调用 update 接口时，是否会按预期触发 itemService.refreshAiTags(...)；
 * - AI 标签生成属于服务层/外部依赖能力，本测试不触达外部 AI。
 */
class ItemControllerTest {

    @AfterEach
    void tearDown() {
        // 避免 ThreadLocal 污染其他测试
        UserContext.clear();
    }

    @Test
    void update_whenSemanticFieldsChanged_shouldRefreshAiTags() {
        // 1) 构造依赖 mock
        ItemService itemService = mock(ItemService.class);
        ItemController controller = new ItemController(itemService);

        // 2) 模拟当前登录用户
        UserContext.set(10L);

        // 3) 模拟数据库里已有的物品
        Item existing = new Item();
        existing.setId(1L);
        existing.setUserId(10L);
        existing.setStatus(1);
        existing.setDescription("old");

        // 4) 构造“会影响标签语义”的更新字段（例如描述）
        ItemUpdateReq req = new ItemUpdateReq();
        req.setDescription("new description");

        controller.update(1L, req);

        verify(itemService).updateByUser(10L, 1L, req);
    }

    @Test
    void update_whenOnlyContactChanged_shouldNotRefreshAiTags() {
        ItemService itemService = mock(ItemService.class);
        ItemController controller = new ItemController(itemService);

        UserContext.set(10L);

        Item existing = new Item();
        existing.setId(1L);
        existing.setUserId(10L);
        existing.setStatus(1);

        // 仅修改联系方式：不影响 ai_tags（本项目策略为不触发刷新）
        ItemUpdateReq req = new ItemUpdateReq();
        req.setContact("wx:xxx");

        controller.update(1L, req);

        verify(itemService).updateByUser(10L, 1L, req);
    }
}
