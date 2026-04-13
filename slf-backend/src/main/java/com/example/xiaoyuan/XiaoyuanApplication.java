package com.example.xiaoyuan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 应用主启动类。
 *
 * 启用 Spring Cache（@EnableCaching），以便在用户模块中
 * 使用注解式缓存（@Cacheable/@CacheEvict）结合 Redis 加速数据读取。
 */
@SpringBootApplication
@EnableCaching
public class XiaoyuanApplication {

    public static void main(String[] args) {
        SpringApplication.run(XiaoyuanApplication.class, args);
    }

}
