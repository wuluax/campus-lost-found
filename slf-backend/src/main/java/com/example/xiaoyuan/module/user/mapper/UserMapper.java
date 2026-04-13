package com.example.xiaoyuan.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.xiaoyuan.module.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper：提供基础 CRUD 能力。
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
