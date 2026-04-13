package com.example.xiaoyuan.module.category.service.impl;

import com.example.xiaoyuan.common.BaseNameStatusServiceImpl;
import com.example.xiaoyuan.module.category.dto.CategorySaveReq;
import com.example.xiaoyuan.module.category.entity.Category;
import com.example.xiaoyuan.module.category.mapper.CategoryMapper;
import com.example.xiaoyuan.module.category.service.CategoryService;
import org.springframework.stereotype.Service;

/**
 * 分类业务实现：复用通用名称+状态字典能力。
 */
@Service
public class CategoryServiceImpl extends BaseNameStatusServiceImpl<CategoryMapper, Category, CategorySaveReq> implements CategoryService {
    /**
     * 创建分类实体。
     */
    @Override
    protected Category createEntity() {
        return new Category();
    }

    /**
     * 设置分类主键ID。
     */
    @Override
    protected void setId(Category entity, Long id) {
        entity.setId(id);
    }

    /**
     * 设置分类名称。
     */
    @Override
    protected void setName(Category entity, String name) {
        entity.setName(name);
    }

    /**
     * 设置分类状态。
     */
    @Override
    protected void setStatus(Category entity, Integer status) {
        entity.setStatus(status);
    }

    /**
     * 分类中文标签，用于提示信息。
     */
    @Override
    protected String getEntityLabel() {
        return "分类";
    }
}
