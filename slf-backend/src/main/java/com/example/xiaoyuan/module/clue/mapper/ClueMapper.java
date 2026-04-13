package com.example.xiaoyuan.module.clue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.xiaoyuan.module.clue.entity.Clue;
import org.apache.ibatis.annotations.Mapper;

/**
 * 线索表 Mapper，提供基础 CRUD 能力。
 */
@Mapper
public interface ClueMapper extends BaseMapper<Clue> {
}
