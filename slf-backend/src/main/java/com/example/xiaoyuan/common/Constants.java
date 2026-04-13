package com.example.xiaoyuan.common;

/**
 * 项目通用常量。
 */
public class Constants {
    public static final String HEADER_AUTH = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    public static final String[] AUTH_WHITELIST = new String[]{
            "/api/auth/login",
            "/api/auth/register",
            "/api/admin/auth/login"
    };
}
