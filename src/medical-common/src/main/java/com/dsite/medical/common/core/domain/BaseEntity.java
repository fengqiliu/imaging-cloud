package com.dsite.medical.common.core.domain;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 逻辑删除基础实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseEntity extends Entity {

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;
}
