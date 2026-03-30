package com.dsite.medical.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dsite.medical.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单权限实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends BaseEntity {

    /** 菜单ID */
    @TableId(value = "menu_id", type = IdType.AUTO)
    private Long menuId;

    /** 菜单名称 */
    @TableField("menu_name")
    private String menuName;

    /** 父菜单ID */
    @TableField("parent_id")
    private Long parentId;

    /** 显示顺序 */
    @TableField("order_num")
    private Integer orderNum;

    /** 路由地址 */
    @TableField("path")
    private String path;

    /** 组件路径 */
    @TableField("component")
    private String component;

    /** 菜单类型（M目录 C菜单 F按钮） */
    @TableField("menu_type")
    private String menuType;

    /** 菜单状态（0显示 1隐藏） */
    @TableField("visible")
    private String visible;

    /** 菜单状态（0正常 1停用） */
    @TableField("status")
    private String status;

    /** 权限标识 */
    @TableField("perms")
    private String perms;

    /** 菜单图标 */
    @TableField("icon")
    private String icon;

    /** 子菜单列表（非数据库字段） */
    @TableField(exist = false)
    private List<SysMenu> children = new ArrayList<>();
}
