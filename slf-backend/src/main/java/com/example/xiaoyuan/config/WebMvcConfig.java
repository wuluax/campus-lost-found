package com.example.xiaoyuan.config;

import com.example.xiaoyuan.security.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置：注册认证拦截器、CORS、静态资源映射。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 认证拦截器，用于校验登录态与管理员权限。
     */
    @Autowired
    private AuthInterceptor authInterceptor;

    /**
     * 注册接口拦截器，覆盖 /api/** 并排除白名单。
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/login", "/api/auth/register", "/api/admin/auth/login");
    }

    /**
     * 配置 CORS 跨域策略，允许前端调用后端接口。
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

}
