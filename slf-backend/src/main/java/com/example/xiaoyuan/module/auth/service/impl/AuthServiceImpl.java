package com.example.xiaoyuan.module.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.xiaoyuan.common.BusinessException;
import com.example.xiaoyuan.common.Constants;
import com.example.xiaoyuan.common.ErrorCode;
import com.example.xiaoyuan.module.auth.dto.LoginReq;
import com.example.xiaoyuan.module.auth.dto.LoginResp;
import com.example.xiaoyuan.module.auth.dto.RegisterReq;
import com.example.xiaoyuan.module.auth.service.AuthService;
import com.example.xiaoyuan.module.user.entity.User;
import com.example.xiaoyuan.module.user.service.UserService;
import com.example.xiaoyuan.security.JwtUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * 认证服务实现。
 */
@Service
public class AuthServiceImpl implements AuthService {
    /**
     * 用户业务服务，用于用户查询与更新。
     */
    private final UserService userService;
    /**
     * 密码加密与校验工具。
     */
    private final PasswordEncoder passwordEncoder;
    /**
     * JWT 生成与解析工具。
     */
    private final JwtUtils jwtUtils;
    /**
     * Redis 客户端，用于维护 token 黑名单。
     */
    private final StringRedisTemplate stringRedisTemplate;

    public AuthServiceImpl(UserService userService, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, StringRedisTemplate stringRedisTemplate) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void register(RegisterReq req) {
        // 校验手机号是否已注册
        User exist = userService.getOne(new QueryWrapper<User>().eq("phone", req.getPhone()));
        if (exist != null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "手机号已注册");
        }
        User u = new User();
        u.setPhone(req.getPhone());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setNickname(req.getNickname());
        u.setStatus(1);
        boolean ok = userService.save(u);
        if (!ok) {
            throw new BusinessException(ErrorCode.SERVER_ERROR, "注册失败");
        }
    }

    @Override
    public LoginResp login(LoginReq req) {
        // 校验账号是否存在
        User u = userService.getOne(new QueryWrapper<User>().eq("phone", req.getPhone()));
        if (u == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "手机号未注册");
        }
        // 状态检查：封禁用户禁止登录
        if (u.getStatus() != null && u.getStatus() == 0) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "已被封禁");
        }
        // 密码校验
        if (!passwordEncoder.matches(req.getPassword(), u.getPassword())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "密码错误");
        }

        // 更新最后更新时间
        LocalDateTime now = LocalDateTime.now();
        User toUpdate = new User();
        toUpdate.setId(u.getId());
        toUpdate.setUpdateTime(now);
        userService.updateById(toUpdate);
        u.setUpdateTime(now);

        // 生成 token 并返回登录响应
        String token = jwtUtils.createToken(u.getId());
        LoginResp resp = new LoginResp();
        resp.setToken(token);
        userService.maskPassword(u);
        resp.setUser(u);
        return resp;
    }

    @Override
    public LoginResp adminLogin(LoginReq req) {
        // 管理端账号校验
        User u = userService.getOne(new QueryWrapper<User>().eq("phone", req.getPhone()));
        if (u == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "手机号未注册");
        }
        if (u.getStatus() != null && u.getStatus() == 0) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "账号已禁用");
        }
        if (u.getRole() == null || u.getRole() != 1) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "非管理员账号，无法登录管理端");
        }
        // 密码校验
        if (!passwordEncoder.matches(req.getPassword(), u.getPassword())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "密码错误");
        }

        LocalDateTime now = LocalDateTime.now();
        User toUpdate = new User();
        toUpdate.setId(u.getId());
        toUpdate.setUpdateTime(now);
        userService.updateById(toUpdate);
        u.setUpdateTime(now);

        // 生成管理员 token 并返回响应
        String token = jwtUtils.createToken(u.getId());
        LoginResp resp = new LoginResp();
        resp.setToken(token);
        userService.maskPassword(u);
        resp.setUser(u);
        return resp;
    }

    @Override
    public void logout(String token) {
        // 将 token 放入黑名单，TTL 与 token 过期时间一致
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "令牌缺失");
        }
        long expEpoch = jwtUtils.getExpireEpochSeconds(token);
        long nowEpoch = Instant.now().getEpochSecond();
        long ttlSec = Math.max(0, expEpoch - nowEpoch);
        String key = "jwt:blacklist:" + token;
        stringRedisTemplate.opsForValue().set(key, "1", Duration.ofSeconds(ttlSec));
    }

    @Override
    public void logoutByAuthHeader(String authHeader) {
        // 从 Authorization 头解析 token 并执行登出
        String token = null;
        if (authHeader != null && authHeader.startsWith(Constants.BEARER_PREFIX)) {
            token = authHeader.substring(Constants.BEARER_PREFIX.length());
        }
        logout(token);
    }
}
