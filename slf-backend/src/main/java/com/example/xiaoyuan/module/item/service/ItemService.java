package com.example.xiaoyuan.module.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.xiaoyuan.common.PageResp;
import com.example.xiaoyuan.module.item.dto.AdminItemDetailVO;
import com.example.xiaoyuan.module.item.dto.AdminItemListVO;
import com.example.xiaoyuan.module.item.dto.ItemCreateReq;
import com.example.xiaoyuan.module.item.dto.ItemQueryReq;
import com.example.xiaoyuan.module.item.dto.ItemStatusUpdateReq;
import com.example.xiaoyuan.module.item.dto.ItemUpdateReq;
import com.example.xiaoyuan.module.item.dto.ItemVO;
import com.example.xiaoyuan.module.item.entity.Item;

public interface ItemService extends IService<Item> {
    Long create(Long userId, ItemCreateReq req);

    void updateByUser(Long userId, Long id, ItemUpdateReq req);

    void deleteByUser(Long userId, Long id);

    PageResp<ItemVO> pagePublic(ItemQueryReq req);

    PageResp<ItemVO> pageMy(Long userId, int pageNo, int pageSize, Integer type);

    long countMy(Long userId, Integer status, Integer type);

    void updateStatusByUser(Long userId, Long id, ItemStatusUpdateReq req);

    ItemVO detail(Long id);

    PageResp<AdminItemListVO> adminPage(int pageNo, int pageSize, Integer type, Long categoryId, Long locationId, Integer status,
                                        java.time.LocalDateTime createStartTime, java.time.LocalDateTime createEndTime, Long publisherId,
                                        String publisherNickname, String description, String keyword);

    AdminItemDetailVO adminDetail(Long id);

    void adminUpdateStatus(Long id, ItemStatusUpdateReq req);

    void adminDelete(Long id);

    Long publish(Item item);

    /**
     * 重新生成并覆盖物品的 ai_tags。
     *
     * 设计目的：
     * - 发布物品时会生成 ai_tags，用于后续 AI 语义匹配；
     * - 当用户修改已发布物品并重新保存时，需要基于“最新的描述/分类/地点/图片”等信息重新生成标签，
     *   以避免旧标签导致匹配不准确；
     * - 若后端未配置 AI Key，该方法会安全跳过，不影响主流程。
     */
    void refreshAiTags(Item item);
}
