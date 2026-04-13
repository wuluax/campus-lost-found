package com.example.xiaoyuan.module.location.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.xiaoyuan.common.PageResp;
import com.example.xiaoyuan.module.location.dto.LocationSaveReq;
import com.example.xiaoyuan.module.location.entity.Location;

import java.util.List;

/**
 * 地点业务接口。
 */
public interface LocationService extends IService<Location> {
    /**
     * 获取启用状态地点列表。
     */
    List<Location> listEnabled();

    /**
     * 管理端分页查询地点。
     */
    PageResp<Location> adminPage(int pageNo, int pageSize, String keyword, Integer status);

    /**
     * 管理端查询全部地点列表（可选状态筛选）。
     */
    List<Location> adminListAll(Integer status);

    /**
     * 管理端创建地点。
     */
    Location adminCreate(LocationSaveReq req);

    /**
     * 管理端更新地点。
     */
    void adminUpdate(Long id, LocationSaveReq req);

    /**
     * 管理端删除地点。
     */
    void adminDelete(Long id);
}
