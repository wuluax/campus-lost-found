package com.example.xiaoyuan.module.clue.controller;

import com.example.xiaoyuan.common.ApiResp;
import com.example.xiaoyuan.common.PageResp;
import com.example.xiaoyuan.module.clue.dto.AdminClueVO;
import com.example.xiaoyuan.module.clue.service.ClueService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端线索管理接口。
 */
@RestController
@RequestMapping("/api/admin/clues")
public class AdminClueController {
    /**
     * 线索业务服务。
     */
    private final ClueService clueService;

    public AdminClueController(ClueService clueService) {
        this.clueService = clueService;
    }

    /**
     * 分页查询线索列表，可按物品与用户筛选。
     */
    @GetMapping
    public ApiResp<PageResp<AdminClueVO>> page(@RequestParam(defaultValue = "1") int pageNo,
                                               @RequestParam(defaultValue = "15") int pageSize,
                                               @RequestParam(required = false) Long itemId,
                                               @RequestParam(required = false) Long userId,
                                               @RequestParam(required = false) String content) {
        return ApiResp.ok(clueService.adminPage(pageNo, pageSize, itemId, userId, content));
    }

    /**
     * 管理员删除线索。
     */
    @DeleteMapping("/{id}")
    public ApiResp<Void> delete(@PathVariable Long id) {
        clueService.adminDelete(id);
        return ApiResp.ok();
    }
}
