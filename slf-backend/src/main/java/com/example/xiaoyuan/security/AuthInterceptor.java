package com.example.xiaoyuan.security;

import com.example.xiaoyuan.common.BusinessException;
import com.example.xiaoyuan.common.Constants;
import com.example.xiaoyuan.common.ErrorCode;
import com.example.xiaoyuan.common.UserContext;
import com.example.xiaoyuan.module.user.service.UserService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.Instant;

/**
 * 认证拦截器：拦截除登录/注册外的所有 /api/** 请求。
 *
 * 校验流程：
 * - 从 Authorization 头获取 Bearer Token
 * - 校验是否在 Redis 黑名单（退出登录后记入黑名单）
 * - 解析 JWT 获取用户ID，写入 UserContext
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    /**
     * JWT 解析与校验工具。
     */
    private final JwtUtils jwtUtils;
    /**
     * Redis 用于维护退出登录后的黑名单 token。
     */
    private final StringRedisTemplate stringRedisTemplate;
    /**
     * 用户服务，用于校验管理员权限。
     */
    private final UserService userService;
    /**
     * 路径匹配器，用于白名单路径匹配。
     */
    private final AntPathMatcher matcher = new AntPathMatcher();

    public AuthInterceptor(JwtUtils jwtUtils, StringRedisTemplate stringRedisTemplate, UserService userService) {
        this.jwtUtils = jwtUtils;
        this.stringRedisTemplate = stringRedisTemplate;
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 1. 白名单直接放行
        String uri = request.getRequestURI();
        for (String p : Constants.AUTH_WHITELIST) {
            if (matcher.match(p, uri)) {
                return true;
            }
        }

        // 2. 读取 Authorization 头并解析 Bearer Token
        String auth = request.getHeader(Constants.HEADER_AUTH);
        if (!StringUtils.hasText(auth) || !auth.startsWith(Constants.BEARER_PREFIX)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录或令牌缺失");
        }
        String token = auth.substring(Constants.BEARER_PREFIX.length());

        // 3. 检查 Redis 黑名单（退出登录后 token 会被加入黑名单，直到过期）
        String key = "jwt:blacklist:" + token;
        String black = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.hasText(black)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "令牌已失效");
        }

        // 4. 解析 token 获取用户ID，写入线程上下文
        Long uid = jwtUtils.getUserId(token);
        UserContext.set(uid);
        // 5. 管理端接口统一要求管理员权限
        if (uri.startsWith("/api/admin/")) {
            userService.requireAdmin(uid);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求结束后清理线程上下文，避免 ThreadLocal 泄漏
        UserContext.clear();
    }
}
