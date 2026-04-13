package com.example.xiaoyuan.module.favorite.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.xiaoyuan.common.PageResp;
import com.example.xiaoyuan.module.favorite.entity.Favorite;
import com.example.xiaoyuan.module.item.dto.ItemVO;

/**
 * 收藏业务接口。
 */
public interface FavoriteService extends IService<Favorite> {
    /**
     * 新增收藏，若已收藏则直接返回已有记录ID。
     */
    Long addFavorite(Long userId, Long itemId);

    /**
     * 取消收藏。
     */
    void removeFavorite(Long userId, Long itemId);

    /**
     * 校验是否已收藏。
     */
    boolean checkFavorite(Long userId, Long itemId);

    /**
     * 分页获取用户收藏列表。
     */
    PageResp<ItemVO> pageFavorites(Long userId, int pageNo, int pageSize);
}
