package com.example.xiaoyuan.module.location.controller;

import com.example.xiaoyuan.common.ApiResp;
import com.example.xiaoyuan.module.location.entity.Location;
import com.example.xiaoyuan.module.location.service.LocationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端地点接口：查询启用地点列表。
 */
@RestController
@RequestMapping("/api/locations")
public class LocationController {
    /**
     * 地点业务服务。
     */
    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    /**
     * 获取启用状态地点列表。
     */
    @GetMapping
    public ApiResp<List<Location>> list() {
        return ApiResp.ok(locationService.listEnabled());
    }
}
