package com.example.xiaoyuan.module.favorite.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.xiaoyuan.common.BusinessException;
import com.example.xiaoyuan.common.ErrorCode;
import com.example.xiaoyuan.common.PageResp;
import com.example.xiaoyuan.module.favorite.entity.Favorite;
import com.example.xiaoyuan.module.favorite.mapper.FavoriteMapper;
import com.example.xiaoyuan.module.favorite.service.FavoriteService;
import com.example.xiaoyuan.module.item.dto.ItemVO;
import com.example.xiaoyuan.module.item.entity.Item;
import com.example.xiaoyuan.module.item.service.ItemService;
import com.example.xiaoyuan.module.notification.service.NotificationService;
import com.example.xiaoyuan.module.user.entity.User;
import com.example.xiaoyuan.module.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 收藏业务实现。
 */
@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    private final ItemService itemService;
    private final UserService userService;
    private final NotificationService notificationService;

    public FavoriteServiceImpl(ItemService itemService, UserService userService, NotificationService notificationService) {
        this.itemService = itemService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    /**
     * 新增收藏并校验物品是否存在。
     */
    @Override
    public Long addFavorite(Long userId, Long itemId) {
        Item item = itemService.getById(itemId);
        if (item == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "物品不存在");
        }
        Favorite exist = this.getOne(new QueryWrapper<Favorite>()
                .eq("user_id", userId)
                .eq("item_id", itemId));
        if (exist != null) {
            return exist.getId();
        }
        Favorite fav = new Favorite();
        fav.setUserId(userId);
        fav.setItemId(itemId);
        this.save(fav);

        // 通知物品发布者被收藏
        notificationService.createAndPush(
                item.getUserId(),
                "favorite",
                "你的物品被收藏了",
                item.getDescription() != null && item.getDescription().length() > 50
                        ? item.getDescription().substring(0, 50) : item.getDescription(),
                itemId,
                userId
        );

        return fav.getId();
    }

    /**
     * 取消收藏记录。
     */
    @Override
    public void removeFavorite(Long userId, Long itemId) {
        this.remove(new QueryWrapper<Favorite>()
                .eq("user_id", userId)
                .eq("item_id", itemId));
    }

    /**
     * 判断用户是否收藏过该物品。
     */
    @Override
    public boolean checkFavorite(Long userId, Long itemId) {
        Long count = this.count(new QueryWrapper<Favorite>()
                .eq("user_id", userId)
                .eq("item_id", itemId));
        return count != null && count > 0;
    }

    /**
     * 分页获取收藏列表，并补全物品与发布者信息。
     */
    @Override
    public PageResp<ItemVO> pageFavorites(Long userId, int pageNo, int pageSize) {
        Page<Favorite> page = this.page(Page.of(pageNo, pageSize),
                new QueryWrapper<Favorite>()
                        .eq("user_id", userId)
                        .orderByDesc("create_time"));

        List<Favorite> records = page.getRecords();
        if (records == null || records.isEmpty()) {
            return PageResp.of(page.getTotal(), Collections.emptyList());
        }

        List<Long> itemIds = records.stream()
                .map(Favorite::getItemId)
                .filter(id -> id != null)
                .collect(Collectors.toList());
        if (itemIds.isEmpty()) {
            return PageResp.of(page.getTotal(), Collections.emptyList());
        }

        List<Item> items = itemService.list(new QueryWrapper<Item>()
                .in("id", itemIds)
                .eq("status", 1));
        if (items == null || items.isEmpty()) {
            return PageResp.of(page.getTotal(), Collections.emptyList());
        }

        Map<Long, Item> itemMap = items.stream()
                .filter(i -> i.getId() != null)
                .collect(Collectors.toMap(Item::getId, Function.identity()));

        Set<Long> userIds = new HashSet<>();
        for (Item it : items) {
            if (it.getUserId() != null) userIds.add(it.getUserId());
        }
        Map<Long, User> userMap = userService.mapByIds(userIds, true);

        List<ItemVO> list = new ArrayList<>();
        for (Favorite fav : records) {
            if (fav == null) continue;
            Item it = fav.getItemId() == null ? null : itemMap.get(fav.getItemId());
            if (it == null) continue;
            ItemVO vo = new ItemVO();
            vo.setItem(it);
            User u = it.getUserId() == null ? null : userMap.get(it.getUserId());
            vo.setUser(u);
            list.add(vo);
        }

        return PageResp.of(page.getTotal(), list);
    }
}
