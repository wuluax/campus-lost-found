package com.example.xiaoyuan.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性，读取 application.properties 中的 jwt.*。
 */
@Component
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {
    /**
     * JWT 签名密钥，支持 Base64 或普通字符串。
     */
    private String secret;
    /**
     * Token 过期时间（分钟）。
     */
    private long expireMinutes;
}
