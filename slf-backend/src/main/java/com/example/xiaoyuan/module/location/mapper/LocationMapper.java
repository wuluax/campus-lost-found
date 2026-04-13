package com.example.xiaoyuan.module.location.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.xiaoyuan.module.location.entity.Location;
import org.apache.ibatis.annotations.Mapper;

/**
 * 地点持久层。
 * 提供拾获/丢失地点字典数据的基础数据库操作。
 */
@Mapper
public interface LocationMapper extends BaseMapper<Location> {
}
