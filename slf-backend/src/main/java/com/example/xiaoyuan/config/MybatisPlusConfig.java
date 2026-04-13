package com.example.xiaoyuan.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.annotation.DbType;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 全局配置。
 *
 * 1) Mapper 扫描：扫描用户模块等包下的 mapper 接口。
 * 2) 分页拦截器：提供分页能力，适用于 MySQL。
 */
@Configuration
@MapperScan("com.example.xiaoyuan.module.**.mapper")
public class MybatisPlusConfig {

    /**
     * 注册 MyBatis-Plus 拦截器，启用分页能力。
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}