package com.example.xiaoyuan.module.category.controller;

import com.example.xiaoyuan.common.ApiResp;
import com.example.xiaoyuan.common.PageResp;
import com.example.xiaoyuan.module.category.dto.CategorySaveReq;
import com.example.xiaoyuan.module.category.entity.Category;
import com.example.xiaoyuan.module.category.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端分类管理接口：分页、列表、新增、修改、删除。
 */
@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {
    /**
     * 分类业务服务。
     */
    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 分类分页查询接口，支持关键字与状态筛选。
     */
    @GetMapping
    public ApiResp<PageResp<Category>> page(@RequestParam(defaultValue = "1") int pageNo,
                                            @RequestParam(defaultValue = "15") int pageSize,
                                            @RequestParam(required = false) String keyword,
                                            @RequestParam(required = false) Integer status) {
        return ApiResp.ok(categoryService.adminPage(pageNo, pageSize, keyword, status));
    }

    /**
     * 分类列表接口（全量或按状态过滤）。
     */
    @GetMapping("/all")
    public ApiResp<List<Category>> listAll(@RequestParam(required = false) Integer status) {
        return ApiResp.ok(categoryService.adminListAll(status));
    }

    /**
     * 新建分类接口。
     */
    @PostMapping
    public ApiResp<Category> create(@RequestBody CategorySaveReq req) {
        return ApiResp.ok(categoryService.adminCreate(req));
    }

    /**
     * 修改分类接口。
     */
    @PutMapping("/{id}")
    public ApiResp<Void> update(@PathVariable Long id, @RequestBody CategorySaveReq req) {
        categoryService.adminUpdate(id, req);
        return ApiResp.ok();
    }

    /**
     * 删除分类接口。
     */
    @DeleteMapping("/{id}")
    public ApiResp<Void> delete(@PathVariable Long id) {
        categoryService.adminDelete(id);
        return ApiResp.ok();
    }
}
