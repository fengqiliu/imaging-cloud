package com.dsite.medical.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dsite.medical.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 参数配置实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_config")
public class SysConfig extends BaseEntity {

    /** 参数主键 */
    @TableId(value = "config_id", type = IdType.AUTO)
    private Long configId;

    /** 参数名称 */
    @TableField("config_name")
    private String configName;

    /** 参数键名 */
    @TableField("config_key")
    private String configKey;

    /** 参数键值 */
    @TableField("config_value")
    private String configValue;

    /** 参数类型 */
    @TableField("config_type")
    private String configType;
}
