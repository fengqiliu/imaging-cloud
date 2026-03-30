package com.dsite.medical.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dsite.medical.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 院区(部门)实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
public class SysDept extends BaseEntity {

    /** 院区ID */
    @TableId(value = "dept_id", type = IdType.AUTO)
    private Long deptId;

    /** 父院区ID */
    @TableField("parent_id")
    private Long parentId;

    /** 祖级列表 */
    @TableField("ancestors")
    private String ancestors;

    /** 院区名称 */
    @TableField("dept_name")
    private String deptName;

    /** 显示顺序 */
    @TableField("order_num")
    private Integer orderNum;

    /** 负责人 */
    @TableField("leader")
    private String leader;

    /** 联系电话 */
    @TableField("phone")
    private String phone;

    /** 邮箱 */
    @TableField("email")
    private String email;

    /** 院区状态（0正常 1停用） */
    @TableField("status")
    private String status;

    /** 删除标志 */
    @TableField("del_flag")
    @TableLogic
    private String delFlag;

    /** 子院区列表（非数据库字段） */
    @TableField(exist = false)
    private List<SysDept> children = new ArrayList<>();
}
