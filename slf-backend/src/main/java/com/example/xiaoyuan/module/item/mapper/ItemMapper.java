package com.example.xiaoyuan.module.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.xiaoyuan.module.item.entity.Item;
import org.apache.ibatis.annotations.Mapper;

/**
 * 失物/招领物品持久层。
 * 提供物品主表的基础 CRUD 能力。
 */
@Mapper
public interface ItemMapper extends BaseMapper<Item> {
}
