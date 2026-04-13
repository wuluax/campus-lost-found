package com.example.xiaoyuan.module.user.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体，对应表 slf_user。
 */
@Data
@TableName("slf_user")
public class User {
    /**
     * 用户主键ID。
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 登录手机号。
     */
    @TableField("phone")
    private String phone;

    /**
     * 登录密码（加密存储）。
     */
    @TableField("password")
    private String password;

    /**
     * 用户昵称。
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 学号。
     */
    @TableField("student_no")
    private String studentNo;

    /**
     * 真实姓名。
     */
    @TableField("real_name")
    private String realName;

    /**
     * 头像地址。
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 角色：0普通用户，1管理员。
     */
    @TableField("role")
    private Integer role;

    /**
     * 状态：0封禁，1正常。
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间。
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间。
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
