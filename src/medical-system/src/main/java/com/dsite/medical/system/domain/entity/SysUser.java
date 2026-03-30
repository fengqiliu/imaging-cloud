package com.dsite.medical.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dsite.medical.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 用户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    /** 用户ID */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /** 院区ID */
    @TableField("dept_id")
    private Long deptId;

    /** 用户名 */
    @TableField("user_name")
    private String userName;

    /** 用户昵称 */
    @TableField("nick_name")
    private String nickName;

    /** 用户类型 */
    @TableField("user_type")
    private String userType;

    /** 邮箱 */
    @TableField("email")
    private String email;

    /** 手机号 */
    @TableField("phone")
    private String phone;

    /** 性别 */
    @TableField("sex")
    private String sex;

    /** 头像 */
    @TableField("avatar")
    private String avatar;

    /** 密码 */
    @TableField("password")
    private String password;

    /** 状态（0正常 1停用） */
    @TableField("status")
    private String status;

    /** 最后登录IP */
    @TableField("login_ip")
    private String loginIp;

    /** 最后登录时间 */
    @TableField("login_date")
    private java.util.Date loginDate;

    /** 角色ID列表（非数据库字段） */
    @TableField(exist = false)
    private List<Long> roleIds;

    /** 院区名称（非数据库字段） */
    @TableField(exist = false)
    private String deptName;

    /** 角色列表（非数据库字段） */
    @TableField(exist = false)
    private List<SysRole> roles;
}
