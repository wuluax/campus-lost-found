package com.example.xiaoyuan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码加密配置：提供 BCryptPasswordEncoder Bean。
 */
@Configuration
public class PasswordConfig {
    /**
     * 提供 BCrypt 密码编码器，用于密码加密与校验。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
