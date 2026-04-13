package com.example.xiaoyuan.module.clue.controller;

import com.example.xiaoyuan.common.ApiResp;
import com.example.xiaoyuan.common.UserContext;
import com.example.xiaoyuan.module.clue.dto.ClueCreateReq;
import com.example.xiaoyuan.module.clue.dto.ClueVO;
import com.example.xiaoyuan.module.clue.service.ClueService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端线索接口。
 */
@RestController
@RequestMapping("/api")
public class ClueController {
    /**
     * 线索业务服务。
     */
    private final ClueService clueService;

    public ClueController(ClueService clueService) {
        this.clueService = clueService;
    }

    /**
     * 获取指定物品的线索列表。
     */
    @GetMapping("/items/{itemId}/clues")
    public ApiResp<List<ClueVO>> list(@PathVariable Long itemId) {
        return ApiResp.ok(clueService.listByItemId(itemId));
    }

    /**
     * 提交线索。
     */
    @PostMapping("/items/{itemId}/clues")
    public ApiResp<Long> create(@PathVariable Long itemId, @Valid @RequestBody ClueCreateReq req) {
        Long uid = UserContext.get();
        return ApiResp.ok(clueService.createClue(uid, itemId, req.getContent()));
    }

    /**
     * 删除自己的线索。
     */
    @DeleteMapping("/clues/{id}")
    public ApiResp<Void> delete(@PathVariable Long id) {
        Long uid = UserContext.get();
        clueService.deleteByUser(uid, id);
        return ApiResp.ok();
    }
}
