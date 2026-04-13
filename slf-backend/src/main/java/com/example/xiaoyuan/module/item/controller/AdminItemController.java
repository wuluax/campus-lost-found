package com.example.xiaoyuan.module.item.controller;

import com.example.xiaoyuan.common.ApiResp;
import com.example.xiaoyuan.common.PageResp;
import com.example.xiaoyuan.module.item.dto.AdminItemDetailVO;
import com.example.xiaoyuan.module.item.dto.AdminItemListVO;
import com.example.xiaoyuan.module.item.dto.ItemStatusUpdateReq;
import com.example.xiaoyuan.module.item.service.ItemService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 管理端物品接口：分页、详情、状态更新与删除。
 */
@RestController
@RequestMapping("/api/admin/items")
public class AdminItemController {
    /**
     * 物品业务服务。
     */
    private final ItemService itemService;
    public AdminItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * 管理端物品列表接口：按多条件筛选并分页查询物品列表。
     */
    @GetMapping
    public ApiResp<PageResp<AdminItemListVO>> list(@RequestParam(defaultValue = "1") int pageNo,
                                                   @RequestParam(defaultValue = "15") int pageSize,
                                                   @RequestParam(required = false) Integer type,
                                                   @RequestParam(required = false) Long categoryId,
                                                   @RequestParam(required = false) Long locationId,
                                                   @RequestParam(required = false) Integer status,
                                                   @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime createStartTime,
                                                   @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime createEndTime,
                                                   @RequestParam(required = false) Long publisherId,
                                                   @RequestParam(required = false) String publisherNickname,
                                                   @RequestParam(required = false) String description,
                                                   @RequestParam(required = false) String keyword) {
        return ApiResp.ok(itemService.adminPage(pageNo, pageSize, type, categoryId, locationId, status, createStartTime,
                createEndTime, publisherId, publisherNickname, description, keyword));
    }

    /**
     * 管理端物品详情接口：获取物品详情及发布者、分类、地点信息。
     */
    @GetMapping("/{id}")
    public ApiResp<AdminItemDetailVO> detail(@PathVariable Long id) {
        return ApiResp.ok(itemService.adminDetail(id));
    }

    /**
     * 管理端物品状态更新接口：管理员修改物品状态。
     */
    @PutMapping("/{id}/status")
    public ApiResp<Void> updateStatus(@PathVariable Long id, @RequestBody ItemStatusUpdateReq req) {
        itemService.adminUpdateStatus(id, req);
        return ApiResp.ok();
    }

    /**
     * 管理端删除物品接口：管理员删除指定物品记录。
     */
    @DeleteMapping("/{id}")
    public ApiResp<Void> delete(@PathVariable Long id) {
        itemService.adminDelete(id);
        return ApiResp.ok();
    }
}
