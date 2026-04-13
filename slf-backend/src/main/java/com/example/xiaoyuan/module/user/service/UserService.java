package com.example.xiaoyuan.module.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.xiaoyuan.common.PageResp;
import com.example.xiaoyuan.module.user.dto.UserPasswordUpdateReq;
import com.example.xiaoyuan.module.user.dto.UserPhoneUpdateReq;
import com.example.xiaoyuan.module.user.dto.UserUpdateReq;
import com.example.xiaoyuan.module.user.entity.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 用户业务接口。
 */
public interface UserService extends IService<User> {
    /**
     * 管理端鉴权：要求当前用户是管理员。
     */
    void requireAdmin(Long userId);

    /**
     * 修改当前用户资料。
     */
    User updateProfile(Long userId, UserUpdateReq req);

    /**
     * 修改当前用户手机号。
     */
    User updatePhone(Long userId, UserPhoneUpdateReq req);

    /**
     * 修改当前用户密码。
     */
    void updatePassword(Long userId, UserPasswordUpdateReq req);

    /**
     * 管理端分页查询用户列表。
     */
    PageResp<User> adminPage(int pageNo, int pageSize, String keyword, Integer role, Integer status);

    /**
     * 管理端更新用户状态。
     */
    void adminUpdateStatus(Long userId, Integer status);

    /**
     * 管理端更新用户角色。
     */
    void adminUpdateRole(Long userId, Integer role);

    /**
     * 管理端删除用户。
     */
    void adminDelete(Long userId);

    void maskPassword(User user);

    void maskPasswords(List<User> users);

    Map<Long, User> mapByIds(Collection<Long> userIds, boolean maskPassword);
}
