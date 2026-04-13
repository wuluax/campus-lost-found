package com.example.xiaoyuan.module.category.controller;

import com.example.xiaoyuan.common.ApiResp;
import com.example.xiaoyuan.module.category.entity.Category;
import com.example.xiaoyuan.module.category.service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端分类接口：查询启用分类列表。
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    /**
     * 分类业务服务。
     */
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 获取启用状态分类列表。
     */
    @GetMapping
    public ApiResp<List<Category>> list() {
        return ApiResp.ok(categoryService.listEnabled());
    }
}
