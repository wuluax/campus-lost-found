package com.example.xiaoyuan.module.clue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.xiaoyuan.common.PageResp;
import com.example.xiaoyuan.module.clue.dto.AdminClueVO;
import com.example.xiaoyuan.module.clue.dto.ClueVO;
import com.example.xiaoyuan.module.clue.entity.Clue;

import java.util.List;

/**
 * 线索业务接口。
 */
public interface ClueService extends IService<Clue> {
    /**
     * 按物品ID获取线索列表。
     */
    List<ClueVO> listByItemId(Long itemId);

    /**
     * 创建线索并返回线索ID。
     */
    Long createClue(Long userId, Long itemId, String content);

    /**
     * 用户删除自己的线索。
     */
    void deleteByUser(Long userId, Long clueId);

    /**
     * 管理端分页查询线索列表。
     */
    PageResp<AdminClueVO> adminPage(int pageNo, int pageSize, Long itemId, Long userId, String content);

    /**
     * 管理端删除指定线索。
     */
    void adminDelete(Long clueId);
}
