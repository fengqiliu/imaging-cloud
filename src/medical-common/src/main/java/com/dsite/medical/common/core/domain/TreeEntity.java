package com.dsite.medical.common.core.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 树形基础实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TreeEntity extends Entity {

    /** 父ID */
    private Long parentId;

    /** 祖级列表 */
    private String ancestors;

    /** 名称 */
    private String name;
}
