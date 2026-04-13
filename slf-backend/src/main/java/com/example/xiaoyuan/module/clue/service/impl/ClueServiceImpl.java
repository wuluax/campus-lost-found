package com.example.xiaoyuan.module.clue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.xiaoyuan.common.BusinessException;
import com.example.xiaoyuan.common.ErrorCode;
import com.example.xiaoyuan.common.PageResp;
import com.example.xiaoyuan.module.clue.dto.AdminClueVO;
import com.example.xiaoyuan.module.clue.dto.ClueVO;
import com.example.xiaoyuan.module.clue.entity.Clue;
import com.example.xiaoyuan.module.clue.mapper.ClueMapper;
import com.example.xiaoyuan.module.clue.service.ClueService;
import com.example.xiaoyuan.module.item.entity.Item;
import com.example.xiaoyuan.module.item.service.ItemService;
import com.example.xiaoyuan.module.notification.service.NotificationService;
import com.example.xiaoyuan.module.user.entity.User;
import com.example.xiaoyuan.module.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
 * 线索业务实现。
 */
@Service
public class ClueServiceImpl extends ServiceImpl<ClueMapper, Clue> implements ClueService {

    /**
     * 物品业务服务，用于校验物品存在性。
     */
    private final ItemService itemService;
    /**
     * 用户业务服务，用于补全线索发布者信息。
     */
    private final UserService userService;
    /**
     * 通知服务，用于在新增线索时通知物品发布者。
     */
    private final NotificationService notificationService;

    public ClueServiceImpl(ItemService itemService, UserService userService, NotificationService notificationService) {
        this.itemService = itemService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    /**
     * 按物品ID查询线索并补全用户信息。
     */
    @Override
    public List<ClueVO> listByItemId(Long itemId) {
        // 查询指定物品的线索列表
        List<Clue> records = this.list(new QueryWrapper<Clue>()
                .eq("item_id", itemId)
                .orderByDesc("create_time"));
        if (records == null || records.isEmpty()) {
            return Collections.emptyList();
        }

        // 收集线索发布者 ID 并批量查询用户信息
        Set<Long> userIds = new HashSet<>();
        for (Clue c : records) {
            if (c != null && c.getUserId() != null) {
                userIds.add(c.getUserId());
            }
        }
        Map<Long, User> userMap = userService.mapByIds(userIds, true);

        // 组装返回视图
        List<ClueVO> list = new ArrayList<>();
        for (Clue c : records) {
            if (c == null) continue;
            ClueVO vo = new ClueVO();
            vo.setId(c.getId());
            vo.setItemId(c.getItemId());
            vo.setUserId(c.getUserId());
            vo.setContent(c.getContent());
            vo.setCreateTime(c.getCreateTime());
            if (c.getUserId() != null) {
                vo.setUser(userMap.get(c.getUserId()));
            }
            list.add(vo);
        }
        return list;
    }

    /**
     * 创建线索并校验物品有效性。
     */
    @Override
    public Long createClue(Long userId, Long itemId, String content) {
        // 校验物品是否存在
        Item item = itemService.getById(itemId);
        if (item == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "物品不存在");
        }
        // 校验线索内容
        String c = content == null ? "" : content.trim();
        if (!StringUtils.hasText(c)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "线索内容不能为空");
        }
        // 保存线索并返回 ID
        Clue clue = new Clue();
        clue.setItemId(itemId);
        clue.setUserId(userId);
        clue.setContent(c);
        this.save(clue);

        // 通知物品发布者有新线索
        notificationService.createAndPush(
                item.getUserId(),
                "clue",
                "你的物品收到了新线索",
                c,
                itemId,
                userId
        );

        return clue.getId();
    }

    /**
     * 删除当前用户自己的线索。
     */
    @Override
    public void deleteByUser(Long userId, Long clueId) {
        // 校验线索归属
        Clue clue = this.getById(clueId);
        if (clue == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "线索不存在");
        }
        if (clue.getUserId() == null || !clue.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权删除他人线索");
        }
        this.removeById(clueId);
    }

    /**
     * 管理端分页筛选线索。
     */
    @Override
    public PageResp<AdminClueVO> adminPage(int pageNo, int pageSize, Long itemId, Long userId, String content) {
        // 构建筛选条件并分页查询
        QueryWrapper<Clue> qw = new QueryWrapper<Clue>().orderByDesc("create_time");
        if (itemId != null) qw.eq("item_id", itemId);
        if (userId != null) qw.eq("user_id", userId);
        if (StringUtils.hasText(content)) qw.like("content", content);

        Page<Clue> page = this.page(Page.of(pageNo, pageSize), qw);
        List<Clue> records = page.getRecords();
        if (records == null || records.isEmpty()) {
            return PageResp.of(page.getTotal(), Collections.emptyList());
        }

        // 批量查询用户昵称用于管理端展示
        Set<Long> userIds = records.stream()
                .map(Clue::getUserId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
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

        // 组装管理端视图
        List<AdminClueVO> list = new ArrayList<>();
        for (Clue c : records) {
            if (c == null) continue;
            AdminClueVO vo = new AdminClueVO();
            vo.setId(c.getId());
            vo.setItemId(c.getItemId());
            vo.setUserId(c.getUserId());
            vo.setUserNickname(c.getUserId() == null ? null : nicknameByUserId.get(c.getUserId()));
            vo.setContent(c.getContent());
            vo.setCreateTime(c.getCreateTime());
            list.add(vo);
        }
        return PageResp.of(page.getTotal(), list);
    }

    /**
     * 管理端删除线索。
     */
    @Override
    public void adminDelete(Long clueId) {
        // 管理端直接删除线索
        Clue clue = this.getById(clueId);
        if (clue == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "线索不存在");
        }
        this.removeById(clueId);
    }
}
