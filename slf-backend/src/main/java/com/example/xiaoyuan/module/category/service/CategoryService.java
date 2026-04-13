package com.example.xiaoyuan.module.category.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.xiaoyuan.common.PageResp;
import com.example.xiaoyuan.module.category.dto.CategorySaveReq;
import com.example.xiaoyuan.module.category.entity.Category;

import java.util.List;

/**
 * 分类业务接口。
 */
public interface CategoryService extends IService<Category> {
    /**
     * 获取启用状态分类列表。
     */
    List<Category> listEnabled();

    /**
     * 管理端分页查询分类。
     */
    PageResp<Category> adminPage(int pageNo, int pageSize, String keyword, Integer status);

    /**
     * 管理端查询全部分类列表（可选状态筛选）。
     */
    List<Category> adminListAll(Integer status);

    /**
     * 管理端创建分类。
     */
    Category adminCreate(CategorySaveReq req);

    /**
     * 管理端更新分类。
     */
    void adminUpdate(Long id, CategorySaveReq req);

    /**
     * 管理端删除分类。
     */
    void adminDelete(Long id);
}
