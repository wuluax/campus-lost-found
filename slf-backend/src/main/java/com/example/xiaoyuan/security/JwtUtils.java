package com.example.xiaoyuan.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

/**
 * JWT 工具：生成与解析令牌。
 */
@Component
public class JwtUtils {
    /**
     * JWT 配置属性（密钥与过期时间）。
     */
    private final JwtProperties props;
    /**
     * HMAC 签名密钥。
     */
    private final SecretKey key;

    public JwtUtils(JwtProperties props) {
        this.props = props;
        // 允许直接使用字符串作为密钥（Base64 或普通字符串均可）
        byte[] secretBytes = props.getSecret() == null ? new byte[0] : props.getSecret().getBytes();
        this.key = Keys.hmacShaKeyFor(secretBytes.length >= 32 ? secretBytes : padTo32(secretBytes));
    }

    /**
     * 将密钥字节补齐到 32 字节，满足 HMAC-SHA256 的长度要求。
     *
     * @param src 原始字节数组
     * @return 补齐后的 32 字节数组
     */
    private byte[] padTo32(byte[] src) {
        byte[] dst = new byte[32];
        for (int i = 0; i < dst.length; i++) {
            dst[i] = i < src.length ? src[i] : (byte) (i * 13 + 7);
        }
        return dst;
    }

    /**
     * 创建 JWT 令牌，subject 保存用户ID。
     *
     * @param userId 用户ID
     * @return JWT 字符串
     */
    public String createToken(Long userId) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.getExpireMinutes() * 60);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 JWT 令牌并返回 Claims。
     *
     * @param token JWT 字符串
     * @return Claims
     */
    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从 JWT 中读取用户ID。
     *
     * @param token JWT 字符串
     * @return 用户ID
     */
    public Long getUserId(String token) {
        Claims claims = parse(token);
        return Long.valueOf(claims.getSubject());
    }

    /**
     * 获取 JWT 过期时间（秒级时间戳）。
     *
     * @param token JWT 字符串
     * @return 过期时间戳（秒）
     */
    public long getExpireEpochSeconds(String token) {
        Claims claims = parse(token);
        return claims.getExpiration().toInstant().getEpochSecond();
    }
}
