package com.example.xiaoyuan.module.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.xiaoyuan.common.BusinessException;
import com.example.xiaoyuan.common.ErrorCode;
import com.example.xiaoyuan.common.PageResp;
import com.example.xiaoyuan.module.item.dto.AdminItemDetailVO;
import com.example.xiaoyuan.module.item.dto.AdminItemListVO;
import com.example.xiaoyuan.module.item.dto.ItemCreateReq;
import com.example.xiaoyuan.module.item.dto.ItemQueryReq;
import com.example.xiaoyuan.module.item.dto.ItemStatusUpdateReq;
import com.example.xiaoyuan.module.item.dto.ItemUpdateReq;
import com.example.xiaoyuan.module.item.dto.ItemVO;
import com.example.xiaoyuan.module.item.entity.Item;
import com.example.xiaoyuan.module.item.mapper.ItemMapper;
import com.example.xiaoyuan.module.item.service.ItemAiService;
import com.example.xiaoyuan.module.item.service.ItemService;
import com.example.xiaoyuan.module.category.entity.Category;
import com.example.xiaoyuan.module.category.service.CategoryService;
import com.example.xiaoyuan.module.location.entity.Location;
import com.example.xiaoyuan.module.location.service.LocationService;
import com.example.xiaoyuan.module.user.entity.User;
import com.example.xiaoyuan.module.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements ItemService {

    @jakarta.annotation.Resource
    private CategoryService categoryService;

    @jakarta.annotation.Resource
    private LocationService locationService;

    @jakarta.annotation.Resource
    private UserService userService;

    @jakarta.annotation.Resource
    private ItemAiService itemAiService;

    //用户发布物品。
    @Override
    public Long create(Long userId, ItemCreateReq req) {
        // 构建物品实体并写入数据库
        Item item = new Item();
        item.setUserId(userId);
        item.setType(req.getType());
        item.setDescription(req.getDescription());
        item.setCategoryId(req.getCategoryId());
        item.setLocationId(req.getLocationId());
        item.setItemTime(req.getItemTime());
        item.setImageUrl(StringUtils.hasText(req.getImageUrl()) ? req.getImageUrl().trim() : null);
        item.setContact(req.getContact());
        item.setStatus(1);
        return publish(item);
    }

    //
    @Override
    public void updateByUser(Long userId, Long id, ItemUpdateReq req) {
        // 校验物品存在与归属
        Item item = this.getById(id);
        if (item == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "物品不存在");
        }
        if (item.getUserId() == null || !item.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权修改他人发布的物品");
        }
        Integer curStatus = item.getStatus();
        if (curStatus != null && (curStatus == 0 || curStatus == 2)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "当前状态不允许编辑");
        }
        // 根据变更内容决定是否刷新 AI 标签
        boolean needRefreshAiTags = false;

        if (req.getType() != null) {
            item.setType(req.getType());
            needRefreshAiTags = true;
        }
        if (StringUtils.hasText(req.getDescription())) {
            item.setDescription(req.getDescription());
            needRefreshAiTags = true;
        }
        if (req.getCategoryId() != null) {
            item.setCategoryId(req.getCategoryId());
            needRefreshAiTags = true;
        }
        if (req.getLocationId() != null) {
            item.setLocationId(req.getLocationId());
            needRefreshAiTags = true;
        }
        if (req.getItemTime() != null) item.setItemTime(req.getItemTime());
        if (StringUtils.hasText(req.getImageUrl())) {
            item.setImageUrl(req.getImageUrl());
            needRefreshAiTags = true;
        }
        if (StringUtils.hasText(req.getContact())) item.setContact(req.getContact());
        this.updateById(item);

        // 仅在关键信息变更时重新生成 AI 标签
        if (needRefreshAiTags) {
            refreshAiTags(item);
        }
    }

    //用户删除自己发布的物品。
    @Override
    public void deleteByUser(Long userId, Long id) {
        // 校验物品归属后删除
        Item item = this.getById(id);
        if (item == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "物品不存在");
        }
        if (item.getUserId() == null || !item.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权删除他人发布的物品");
        }
        this.removeById(id);
    }

    //共分页查询物品列表。
    @Override
    public PageResp<ItemVO> pagePublic(ItemQueryReq req) {
        QueryWrapper<Item> qw = new QueryWrapper<>();
        if (req.getUserId() != null) qw.eq("user_id", req.getUserId());
        if (req.getType() != null) qw.eq("type", req.getType());
        if (req.getCategoryId() != null) qw.eq("category_id", req.getCategoryId());
        if (req.getLocationId() != null) qw.eq("location_id", req.getLocationId());
        if (StringUtils.hasText(req.getKeyword())) {
            qw.and(w -> w.like("description", req.getKeyword()));
        }
        if (Boolean.TRUE.equals(req.getIncludeAllStatus()) && req.getUserId() != null) {
            qw.in("status", 0, 1, 2);
        } else {
            qw.eq("status", 1);
        }
        String field = req.getSortField();
        String column = "create_time";
        if ("itemTime".equalsIgnoreCase(field)) {
            column = "item_time";
        }
        boolean asc = "asc".equalsIgnoreCase(req.getSortOrder());
        qw.orderBy(true, asc, column);

        Page<Item> page = this.page(Page.of(req.getPageNo(), req.getPageSize()), qw);
        List<Item> records = page.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return PageResp.of(page.getTotal(), Collections.emptyList());
        }

        Set<Long> userIds = records.stream().map(Item::getUserId).collect(Collectors.toSet());
        Map<Long, User> userMap = userService.mapByIds(userIds, true);

        List<ItemVO> list = records.stream().map(it -> {
            ItemVO vo = new ItemVO();
            vo.setItem(it);
            User u = userMap.get(it.getUserId());
            vo.setUser(u);
            return vo;
        }).collect(Collectors.toList());
        return PageResp.of(page.getTotal(), list);
    }

    //查询当前用户“我的发布”分页列表。
    @Override
    public PageResp<ItemVO> pageMy(Long userId, int pageNo, int pageSize, Integer type) {
        QueryWrapper<Item> qw = new QueryWrapper<Item>()
                .eq("user_id", userId)
                .in("status", 0, 1, 2)
                .orderByDesc("create_time");
        if (type != null) {
            qw.eq("type", type);
        }
        Page<Item> page = this.page(Page.of(pageNo, pageSize), qw);
        List<Item> records = page.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return PageResp.of(page.getTotal(), Collections.emptyList());
        }
        Set<Long> userIds = records.stream().map(Item::getUserId).collect(Collectors.toSet());
        Map<Long, User> userMap = userService.mapByIds(userIds, true);
        List<ItemVO> list = records.stream().map(it -> {
            ItemVO vo = new ItemVO();
            vo.setItem(it);
            User u = userMap.get(it.getUserId());
            vo.setUser(u);
            return vo;
        }).collect(Collectors.toList());
        return PageResp.of(page.getTotal(), list);
    }

    //统计当前用户物品数量，可按状态与类型过滤。
    @Override
    public long countMy(Long userId, Integer status, Integer type) {
        QueryWrapper<Item> qw = new QueryWrapper<Item>().eq("user_id", userId);
        if (status != null) {
            qw.eq("status", status);
        } else {
            qw.in("status", 0, 1, 2);
        }
        if (type != null) {
            qw.eq("type", type);
        }
        return this.count(qw);
    }

    //用户修改自己物品状态。
    @Override
    public void updateStatusByUser(Long userId, Long id, ItemStatusUpdateReq req) {
        Item item = this.getById(id);
        if (item == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "物品不存在");
        }
        if (item.getUserId() == null || !item.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权修改他人发布的物品状态");
        }
        Integer st = req.getStatus();
        if (st == null || (st != 0 && st != 1 && st != 2)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "状态非法");
        }
        Integer curStatus = item.getStatus();
        if (curStatus == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "当前物品状态异常");
        }
        if (curStatus.equals(st)) {
            return;
        }
        boolean allowed = (curStatus == 1 && (st == 0 || st == 2)) || (curStatus == 0 && st == 1) || (curStatus == 2 && st == 1);
        if (!allowed) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "不允许的状态变更");
        }
        Item toUpdate = new Item();
        toUpdate.setId(id);
        toUpdate.setStatus(st);
        this.updateById(toUpdate);
    }

    //查询物品详情并补充发布人信息。
    @Override
    public ItemVO detail(Long id) {
        Item it = this.getById(id);
        if (it == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "物品不存在");
        }
        ItemVO vo = new ItemVO();
        vo.setItem(it);
        User u = userService.getById(it.getUserId());
        userService.maskPassword(u);
        vo.setUser(u);
        return vo;
    }

    //管理端分页查询物品，支持多维筛选与关键字检索。
    @Override
    public PageResp<AdminItemListVO> adminPage(int pageNo, int pageSize, Integer type, Long categoryId, Long locationId, Integer status,
                                               LocalDateTime createStartTime, LocalDateTime createEndTime, Long publisherId,
                                               String publisherNickname, String description, String keyword) {
        QueryWrapper<Item> qw = new QueryWrapper<Item>().orderByDesc("create_time");
        if (type != null) qw.eq("type", type);
        if (categoryId != null) qw.eq("category_id", categoryId);
        if (locationId != null) qw.eq("location_id", locationId);
        if (status != null) qw.eq("status", status);
        if (createStartTime != null) qw.ge("create_time", createStartTime);
        if (createEndTime != null) qw.le("create_time", createEndTime);

        if (StringUtils.hasText(description)) {
            qw.like("description", description);
        }

        if (publisherId != null) {
            qw.eq("user_id", publisherId);
        } else if (StringUtils.hasText(publisherNickname)) {
            List<User> users = userService.list(new QueryWrapper<User>()
                    .select("id")
                    .like("nickname", publisherNickname));
            if (users == null || users.isEmpty()) {
                return PageResp.of(0, Collections.emptyList());
            }
            Set<Long> ids = new HashSet<>();
            for (User u : users) {
                if (u != null && u.getId() != null) ids.add(u.getId());
            }
            if (ids.isEmpty()) {
                return PageResp.of(0, Collections.emptyList());
            }
            qw.in("user_id", ids);
        }

        Set<Long> keywordUserIds = Collections.emptySet();
        if (StringUtils.hasText(keyword)) {
            List<User> users = userService.list(new QueryWrapper<User>()
                    .select("id")
                    .like("nickname", keyword));
            if (users != null && !users.isEmpty()) {
                Set<Long> ids = new HashSet<>();
                for (User u : users) {
                    if (u != null && u.getId() != null) ids.add(u.getId());
                }
                keywordUserIds = ids;
            }

            Set<Long> finalKeywordUserIds = keywordUserIds;
            qw.and(w -> {
                w.like("description", keyword);
                if (finalKeywordUserIds != null && !finalKeywordUserIds.isEmpty()) {
                    w.or().in("user_id", finalKeywordUserIds);
                }
            });
        }

        Page<Item> page = this.page(Page.of(pageNo, pageSize), qw);
        List<Item> records = page.getRecords();

        Set<Long> userIds = new HashSet<>();
        Set<Long> categoryIds = new HashSet<>();
        Set<Long> locationIds = new HashSet<>();
        for (Item it : records) {
            if (it.getUserId() != null) userIds.add(it.getUserId());
            if (it.getCategoryId() != null) categoryIds.add(it.getCategoryId());
            if (it.getLocationId() != null) locationIds.add(it.getLocationId());
        }

        Map<Long, String> nicknameByUserId = new HashMap<>();
        if (!userIds.isEmpty()) {
            Map<Long, User> userMap = userService.mapByIds(userIds, false);
            for (Map.Entry<Long, User> entry : userMap.entrySet()) {
                User u = entry.getValue();
                if (u != null && u.getId() != null) {
                    nicknameByUserId.put(u.getId(), u.getNickname());
                }
            }
        }

        Map<Long, String> categoryNameById = new HashMap<>();
        if (!categoryIds.isEmpty()) {
            List<Category> categories = categoryService.listByIds(categoryIds);
            for (Category c : categories) {
                categoryNameById.put(c.getId(), c.getName());
            }
        }

        Map<Long, Location> locationById = new HashMap<>();
        if (!locationIds.isEmpty()) {
            List<Location> locations = locationService.listByIds(locationIds);
            for (Location l : locations) {
                locationById.put(l.getId(), l);
            }
        }

        List<AdminItemListVO> list = new ArrayList<>();
        for (Item it : records) {
            AdminItemListVO vo = new AdminItemListVO();
            vo.setId(it.getId());
            vo.setType(it.getType());
            vo.setUserId(it.getUserId());
            vo.setPublisherNickname(it.getUserId() == null ? null : nicknameByUserId.get(it.getUserId()));
            vo.setDescription(it.getDescription());
            vo.setCategoryId(it.getCategoryId());
            vo.setCategoryName(it.getCategoryId() == null ? null : categoryNameById.get(it.getCategoryId()));
            vo.setLocationId(it.getLocationId());
            Location loc = it.getLocationId() == null ? null : locationById.get(it.getLocationId());
            vo.setLocationName(loc == null ? null : loc.getName());
            vo.setFullLocation(loc == null ? null : loc.getName());
            vo.setStatus(it.getStatus());
            vo.setCreateTime(it.getCreateTime());
            list.add(vo);
        }

        return PageResp.of(page.getTotal(), list);
    }

    //管理端查看物品详情，返回展示层聚合字段。
    @Override
    public AdminItemDetailVO adminDetail(Long id) {
        Item it = this.getById(id);
        if (it == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "物品不存在");
        }

        User publisher = it.getUserId() == null ? null : userService.getById(it.getUserId());
        userService.maskPassword(publisher);

        Category category = it.getCategoryId() == null ? null : categoryService.getById(it.getCategoryId());
        Location location = it.getLocationId() == null ? null : locationService.getById(it.getLocationId());

        List<String> pathNames = new ArrayList<>();
        if (location != null && StringUtils.hasText(location.getName())) {
            pathNames.add(location.getName());
        }

        AdminItemDetailVO vo = new AdminItemDetailVO();
        vo.setItem(it);
        vo.setPublisher(publisher);
        vo.setCategory(category);
        vo.setLocation(location);
        vo.setPublisherDisplay(buildDisplay(publisher == null ? null : publisher.getNickname(), publisher == null ? null : publisher.getId()));
        vo.setCategoryDisplay(buildDisplay(category == null ? null : category.getName(), category == null ? null : category.getId()));
        vo.setLocationDisplay(buildDisplay(location == null ? null : location.getName(), location == null ? null : location.getId()));
        vo.setLocationPathNames(pathNames);
        vo.setFullLocation(pathNames.isEmpty() ? null : pathNames.get(0));
        return vo;
    }

    //管理端更新物品状态。
    @Override
    public void adminUpdateStatus(Long id, ItemStatusUpdateReq req) {
        Item item = this.getById(id);
        if (item == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "物品不存在");
        }

        Integer st = req == null ? null : req.getStatus();
        if (st == null || (st != 0 && st != 1 && st != 2)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "状态非法");
        }

        if (item.getStatus() != null && item.getStatus().equals(st)) {
            return;
        }

        Item toUpdate = new Item();
        toUpdate.setId(id);
        toUpdate.setStatus(st);
        this.updateById(toUpdate);
    }

    //管理端删除物品。
    @Override
    public void adminDelete(Long id) {
        Item item = this.getById(id);
        if (item == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "物品不存在");
        }
        this.removeById(id);
    }

    //发布物品并立即触发 AI 标签生成。
    @Override
    public Long publish(Item item) {
        this.save(item);
        itemAiService.generateAndUpdateAiTags(item);
        return item.getId();
    }

    //刷新物品 AI 标签。
    @Override
    public void refreshAiTags(Item item) {
        itemAiService.generateAndUpdateAiTags(item);
    }

    private String buildDisplay(String name, Long id) {
        String n = (name == null ? "" : name);
        return n + "[" + (id == null ? "" : id) + "]";
    }
}
