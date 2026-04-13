package com.example.xiaoyuan.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.xiaoyuan.common.BusinessException;
import com.example.xiaoyuan.common.ErrorCode;
import com.example.xiaoyuan.common.PageResp;
import com.example.xiaoyuan.module.user.dto.UserPasswordUpdateReq;
import com.example.xiaoyuan.module.user.dto.UserPhoneUpdateReq;
import com.example.xiaoyuan.module.user.dto.UserUpdateReq;
import com.example.xiaoyuan.module.user.entity.User;
import com.example.xiaoyuan.module.user.mapper.UserMapper;
import com.example.xiaoyuan.module.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户业务实现。
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    /**
     * 密码加密与校验工具。
     */
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 管理端鉴权：要求当前用户为管理员。
     */
    @Override
    public void requireAdmin(Long userId) {
        // 校验当前用户是否为管理员
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录");
        }
        User u = this.getById(userId);
        if (u == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "账号不存在");
        }
        if (u.getStatus() != null && u.getStatus() == 0) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "账号已禁用");
        }
        if (u.getRole() == null || u.getRole() != 1) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "非管理员账号");
        }
    }

    /**
     * 修改当前用户资料。
     */
    @Override
    public User updateProfile(Long userId, UserUpdateReq req) {
        // 校验登录态与参数
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录");
        }
        User exist = this.getById(userId);
        if (exist == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        if (req == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "参数不能为空");
        }

        User toUpdate = new User();
        toUpdate.setId(userId);
        boolean hasUpdate = false;

        // 按需更新字段
        if (StringUtils.hasText(req.getNickname())) {
            toUpdate.setNickname(req.getNickname().trim());
            hasUpdate = true;
        }
        if (StringUtils.hasText(req.getStudentNo())) {
            toUpdate.setStudentNo(req.getStudentNo().trim());
            hasUpdate = true;
        }
        if (StringUtils.hasText(req.getRealName())) {
            toUpdate.setRealName(req.getRealName().trim());
            hasUpdate = true;
        }
        if (StringUtils.hasText(req.getAvatar())) {
            toUpdate.setAvatar(req.getAvatar().trim());
            hasUpdate = true;
        }

        if (!hasUpdate) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "未提供可修改字段");
        }

        // 更新并返回最新数据
        this.updateById(toUpdate);
        User updated = this.getById(userId);
        maskPassword(updated);
        return updated;
    }

    /**
     * 修改当前用户手机号。
     */
    @Override
    public User updatePhone(Long userId, UserPhoneUpdateReq req) {
        // 登录态与参数校验
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录");
        }
        if (req == null || !StringUtils.hasText(req.getPhone()) || !StringUtils.hasText(req.getPassword())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "参数不能为空");
        }
        User exist = this.getById(userId);
        if (exist == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        if (!passwordEncoder.matches(req.getPassword(), exist.getPassword())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "密码错误");
        }
        String phone = req.getPhone().trim();
        if (phone.equals(exist.getPhone())) {
            maskPassword(exist);
            return exist;
        }
        User dup = this.getOne(new QueryWrapper<User>().eq("phone", phone));
        if (dup != null && dup.getId() != null && !dup.getId().equals(userId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "手机号已注册");
        }
        User toUpdate = new User();
        toUpdate.setId(userId);
        toUpdate.setPhone(phone);
        this.updateById(toUpdate);
        User updated = this.getById(userId);
        maskPassword(updated);
        return updated;
    }

    /**
     * 修改当前用户密码。
     */
    @Override
    public void updatePassword(Long userId, UserPasswordUpdateReq req) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录");
        }
        if (req == null || !StringUtils.hasText(req.getOldPassword()) || !StringUtils.hasText(req.getNewPassword())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "参数不能为空");
        }
        User exist = this.getById(userId);
        if (exist == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        if (!passwordEncoder.matches(req.getOldPassword(), exist.getPassword())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "旧密码错误");
        }
        User toUpdate = new User();
        toUpdate.setId(userId);
        toUpdate.setPassword(passwordEncoder.encode(req.getNewPassword()));
        this.updateById(toUpdate);
    }

    /**
     * 管理端分页查询用户列表。
     */
    @Override
    public PageResp<User> adminPage(int pageNo, int pageSize, String keyword, Integer role, Integer status) {
        QueryWrapper<User> qw = new QueryWrapper<User>().orderByAsc("id");
        if (role != null) qw.eq("role", role);
        if (status != null) qw.eq("status", status);
        if (StringUtils.hasText(keyword)) {
            qw.and(w -> w.like("phone", keyword)
                    .or().like("nickname", keyword)
                    .or().like("student_no", keyword));
        }
        Page<User> page = this.page(Page.of(pageNo, pageSize), qw);
        List<User> records = page.getRecords();
        if (records == null || records.isEmpty()) {
            return PageResp.of(page.getTotal(), Collections.emptyList());
        }
        maskPasswords(records);
        return PageResp.of(page.getTotal(), records);
    }

    /**
     * 管理端更新用户状态。
     */
    @Override
    public void adminUpdateStatus(Long userId, Integer status) {
        User exist = this.getById(userId);
        if (exist == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "状态非法");
        }
        if (exist.getStatus() != null && exist.getStatus().equals(status)) {
            return;
        }
        User toUpdate = new User();
        toUpdate.setId(userId);
        toUpdate.setStatus(status);
        this.updateById(toUpdate);
    }

    /**
     * 管理端更新用户角色。
     */
    @Override
    public void adminUpdateRole(Long userId, Integer role) {
        User exist = this.getById(userId);
        if (exist == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        if (role == null || (role != 0 && role != 1)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "角色非法");
        }
        if (exist.getRole() != null && exist.getRole().equals(role)) {
            return;
        }
        User toUpdate = new User();
        toUpdate.setId(userId);
        toUpdate.setRole(role);
        this.updateById(toUpdate);
    }

    /**
     * 管理端删除用户。
     */
    @Override
    public void adminDelete(Long userId) {
        User exist = this.getById(userId);
        if (exist == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        this.removeById(userId);
    }

    @Override
    public void maskPassword(User user) {
        if (user != null) {
            user.setPassword(null);
        }
    }

    @Override
    public void maskPasswords(List<User> users) {
        if (users == null || users.isEmpty()) {
            return;
        }
        for (User u : users) {
            if (u != null) {
                u.setPassword(null);
            }
        }
    }

    @Override
    public Map<Long, User> mapByIds(Collection<Long> userIds, boolean maskPassword) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<User> users = this.listByIds(userIds);
        if (users == null || users.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, User> map = new HashMap<>();
        for (User u : users) {
            if (u != null && u.getId() != null) {
                if (maskPassword) {
                    u.setPassword(null);
                }
                map.put(u.getId(), u);
            }
        }
        return map;
    }
}
