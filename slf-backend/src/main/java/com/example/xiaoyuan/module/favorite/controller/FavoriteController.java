package com.example.xiaoyuan.module.favorite.controller;

import com.example.xiaoyuan.common.ApiResp;
import com.example.xiaoyuan.common.PageResp;
import com.example.xiaoyuan.common.UserContext;
import com.example.xiaoyuan.module.favorite.service.FavoriteService;
import com.example.xiaoyuan.module.item.dto.ItemVO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 收藏相关接口：新增、取消、校验与分页列表。
 */
@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    /**
     * 收藏业务服务。
     */
    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    /**
     * 新增收藏接口：对指定物品进行收藏。
     */
    @PostMapping("/{itemId}")
    public ApiResp<Long> add(@PathVariable Long itemId) {
        Long uid = UserContext.get();
        return ApiResp.ok(favoriteService.addFavorite(uid, itemId));
    }

    /**
     * 取消收藏接口：移除当前用户对该物品的收藏。
     */
    @DeleteMapping("/{itemId}")
    public ApiResp<Void> remove(@PathVariable Long itemId) {
        Long uid = UserContext.get();
        favoriteService.removeFavorite(uid, itemId);
        return ApiResp.ok();
    }

    /**
     * 校验收藏接口：判断当前用户是否收藏该物品。
     */
    @GetMapping("/{itemId}/check")
    public ApiResp<Boolean> check(@PathVariable Long itemId) {
        Long uid = UserContext.get();
        return ApiResp.ok(favoriteService.checkFavorite(uid, itemId));
    }

    /**
     * 收藏列表接口：分页获取当前用户的收藏列表。
     */
    @GetMapping
    public ApiResp<PageResp<ItemVO>> page(@RequestParam(defaultValue = "1") int pageNo,
                                          @RequestParam(defaultValue = "10") int pageSize) {
        Long uid = UserContext.get();
        return ApiResp.ok(favoriteService.pageFavorites(uid, pageNo, pageSize));
    }
}
