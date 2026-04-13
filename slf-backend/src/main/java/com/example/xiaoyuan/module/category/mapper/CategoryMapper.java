package com.example.xiaoyuan.module.category.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.xiaoyuan.module.category.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 物品分类持久层。
 * 提供分类实体的基础数据库操作能力。
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
