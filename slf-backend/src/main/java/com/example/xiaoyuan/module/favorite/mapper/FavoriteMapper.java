package com.example.xiaoyuan.module.favorite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.xiaoyuan.module.favorite.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收藏表 Mapper。
 */
@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
}
