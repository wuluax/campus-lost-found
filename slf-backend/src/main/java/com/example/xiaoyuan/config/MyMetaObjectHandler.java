package com.example.xiaoyuan.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

    /**
 * MyBatis-Plus 元数据处理器：自动填充创建与更新时间。
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 插入时自动填充 createTime 与 updateTime。
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 统一在插入时填充 createTime / updateTime
        strictInsertFill(metaObject, "createTime", LocalDateTime::now, LocalDateTime.class);
        strictInsertFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
    }

    /**
     * 更新时自动填充 updateTime。
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 统一在更新时填充 updateTime
        strictUpdateFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
    }
}
