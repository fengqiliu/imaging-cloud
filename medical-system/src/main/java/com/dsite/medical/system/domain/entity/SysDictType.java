package com.dsite.medical.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dsite.medical.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典类型实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_type")
public class SysDictType extends BaseEntity {

    /** 字典主键 */
    @TableId(value = "dict_id", type = IdType.AUTO)
    private Long dictId;

    /** 字典名称 */
    @TableField("dict_name")
    private String dictName;

    /** 字典类型 */
    @TableField("dict_type")
    private String dictType;

    /** 状态（0正常 1停用） */
    @TableField("status")
    private String status;
}
