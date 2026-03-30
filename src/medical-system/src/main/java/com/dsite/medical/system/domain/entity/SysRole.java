package com.dsite.medical.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dsite.medical.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    /** 角色ID */
    @TableId(value = "role_id", type = IdType.AUTO)
    private Long roleId;

    /** 角色名称 */
    @TableField("role_name")
    private String roleName;

    /** 角色权限字符串 */
    @TableField("role_key")
    private String roleKey;

    /** 显示顺序 */
    @TableField("role_sort")
    private Integer roleSort;

    /** 数据范围 */
    @TableField("data_scope")
    private String dataScope;

    /** 菜单树选择项是否关联 */
    @TableField("menu_check_strictly")
    private Boolean menuCheckStrictly;

    /** 部门树选择项是否关联 */
    @TableField("dept_check_strictly")
    private Boolean deptCheckStrictly;

    /** 角色状态（0正常 1停用） */
    @TableField("status")
    private String status;

    /** 菜单ID列表（非数据库字段） */
    @TableField(exist = false)
    private Long[] menuIds;
}
