package com.example.xiaoyuan.module.item.controller;

import com.example.xiaoyuan.common.ApiResp;
import com.example.xiaoyuan.common.PageResp;
import com.example.xiaoyuan.common.UserContext;
import com.example.xiaoyuan.module.item.dto.ItemCreateReq;
import com.example.xiaoyuan.module.item.dto.ItemQueryReq;
import com.example.xiaoyuan.module.item.dto.ItemStatusUpdateReq;
import com.example.xiaoyuan.module.item.dto.ItemUpdateReq;
import com.example.xiaoyuan.module.item.dto.ItemVO;
import com.example.xiaoyuan.module.item.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 物品相关接口：发布、列表、详情、修改。
 */
@RestController
@RequestMapping("/api/items")
public class ItemController {
    /**
     * 物品业务服务。
     */
    private final ItemService itemService;
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * 发布物品接口：创建新的失物或招领信息并返回物品 ID。
     */
    @PostMapping
    public ApiResp<Long> create(@Valid @RequestBody ItemCreateReq req) {
        Long id = itemService.create(UserContext.get(), req);
        return ApiResp.ok(id);
    }

    /**
     * 修改物品接口：更新当前用户发布的物品信息。
     */
    @PutMapping("/{id}")
    public ApiResp<Void> update(@PathVariable Long id, @RequestBody ItemUpdateReq req) {
        itemService.updateByUser(UserContext.get(), id, req);
        return ApiResp.ok();
    }

    /**
     * 删除物品接口：仅允许物品发布者删除自己的物品。
     */
    @DeleteMapping("/{id}")
    public ApiResp<Void> delete(@PathVariable Long id) {
        itemService.deleteByUser(UserContext.get(), id);
        return ApiResp.ok();
    }

    /**
     * 物品列表接口：按条件分页查询公开物品列表。
     */
    @GetMapping
    public ApiResp<PageResp<ItemVO>> list(ItemQueryReq req) {
        return ApiResp.ok(itemService.pagePublic(req));
    }

    /**
     * 我的物品列表接口：分页获取当前用户发布的物品。
     */
    @GetMapping("/my")
    public ApiResp<PageResp<ItemVO>> myItems(@RequestParam(defaultValue = "1") int pageNo,
                                             @RequestParam(defaultValue = "10") int pageSize,
                                             @RequestParam(required = false) Integer type) {
        return ApiResp.ok(itemService.pageMy(UserContext.get(), pageNo, pageSize, type));
    }

    /**
     * 我的物品数量接口：按状态与类型统计当前用户物品数量。
     */
    @GetMapping("/my/count")
    public ApiResp<Long> myItemsCount(@RequestParam(required = false) Integer status,
                                      @RequestParam(required = false) Integer type) {
        return ApiResp.ok(itemService.countMy(UserContext.get(), status, type));
    }

    /**
     * 物品状态更新接口：由物品发布者调整物品状态。
     */
    @PutMapping("/{id}/status")
    public ApiResp<Void> updateStatus(@PathVariable Long id, @RequestBody ItemStatusUpdateReq req) {
        itemService.updateStatusByUser(UserContext.get(), id, req);
        return ApiResp.ok();
    }

    /**
     * 物品详情接口：获取指定物品的详细信息与发布者资料。
     */
    @GetMapping("/{id}")
    public ApiResp<ItemVO> detail(@PathVariable Long id) {
        return ApiResp.ok(itemService.detail(id));
    }
}
