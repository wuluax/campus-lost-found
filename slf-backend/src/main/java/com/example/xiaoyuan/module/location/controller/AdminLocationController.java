package com.example.xiaoyuan.module.location.controller;

import com.example.xiaoyuan.common.ApiResp;
import com.example.xiaoyuan.common.PageResp;
import com.example.xiaoyuan.module.location.dto.LocationSaveReq;
import com.example.xiaoyuan.module.location.entity.Location;
import com.example.xiaoyuan.module.location.service.LocationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端地点管理接口：分页、列表、新增、修改、删除。
 */
@RestController
@RequestMapping("/api/admin/locations")
public class AdminLocationController {
    /**
     * 地点业务服务。
     */
    private final LocationService locationService;

    public AdminLocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    /**
     * 地点分页查询接口，支持关键字与状态筛选。
     */
    @GetMapping
    public ApiResp<PageResp<Location>> page(@RequestParam(defaultValue = "1") int pageNo,
                                            @RequestParam(defaultValue = "15") int pageSize,
                                            @RequestParam(required = false) String keyword,
                                            @RequestParam(required = false) Integer status) {
        return ApiResp.ok(locationService.adminPage(pageNo, pageSize, keyword, status));
    }

    /**
     * 地点列表接口（全量或按状态过滤）。
     */
    @GetMapping("/all")
    public ApiResp<List<Location>> listAll(@RequestParam(required = false) Integer status) {
        return ApiResp.ok(locationService.adminListAll(status));
    }

    /**
     * 新建地点接口。
     */
    @PostMapping
    public ApiResp<Location> create(@RequestBody LocationSaveReq req) {
        return ApiResp.ok(locationService.adminCreate(req));
    }

    /**
     * 修改地点接口。
     */
    @PutMapping("/{id}")
    public ApiResp<Void> update(@PathVariable Long id, @RequestBody LocationSaveReq req) {
        locationService.adminUpdate(id, req);
        return ApiResp.ok();
    }

    /**
     * 删除地点接口。
     */
    @DeleteMapping("/{id}")
    public ApiResp<Void> delete(@PathVariable Long id) {
        locationService.adminDelete(id);
        return ApiResp.ok();
    }
}
