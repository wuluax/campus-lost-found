package com.example.xiaoyuan.module.location.service.impl;

import com.example.xiaoyuan.common.BaseNameStatusServiceImpl;
import com.example.xiaoyuan.module.location.dto.LocationSaveReq;
import com.example.xiaoyuan.module.location.entity.Location;
import com.example.xiaoyuan.module.location.mapper.LocationMapper;
import com.example.xiaoyuan.module.location.service.LocationService;
import org.springframework.stereotype.Service;

/**
 * 地点业务实现：复用通用名称+状态字典能力。
 */
@Service
public class LocationServiceImpl extends BaseNameStatusServiceImpl<LocationMapper, Location, LocationSaveReq> implements LocationService {
    /**
     * 创建地点实体。
     */
    @Override
    protected Location createEntity() {
        return new Location();
    }

    /**
     * 设置地点主键ID。
     */
    @Override
    protected void setId(Location entity, Long id) {
        entity.setId(id);
    }

    /**
     * 设置地点名称。
     */
    @Override
    protected void setName(Location entity, String name) {
        entity.setName(name);
    }

    /**
     * 设置地点状态。
     */
    @Override
    protected void setStatus(Location entity, Integer status) {
        entity.setStatus(status);
    }

    /**
     * 地点中文标签，用于提示信息。
     */
    @Override
    protected String getEntityLabel() {
        return "地点";
    }
}
