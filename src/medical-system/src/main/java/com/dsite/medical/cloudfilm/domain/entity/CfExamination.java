package com.dsite.medical.cloudfilm.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dsite.medical.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 检查单实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cf_examination")
public class CfExamination extends BaseEntity {

    /** 检查单ID */
    @TableId(value = "exam_id", type = IdType.INPUT)
    private String examId;

    /** 患者ID */
    @TableField("patient_id")
    private String patientId;

    /** 检查类型 */
    @TableField("exam_type")
    private String examType;

    /** 检查原因 */
    @TableField("exam_reason")
    private String examReason;

    /** 检查状态 */
    @TableField("exam_status")
    private String examStatus;

    /** 申请医生 */
    @TableField("apply_doctor")
    private String applyDoctor;

    /** 检查日期 */
    @TableField("exam_date")
    private Date examDate;

    /** 出报告日期 */
    @TableField("report_date")
    private Date reportDate;

    /** 院区ID */
    @TableField("dept_id")
    private Long deptId;

    /** 删除标志 */
    @TableField("del_flag")
    @TableLogic
    private String delFlag;

    /** 患者姓名（非数据库字段） */
    @TableField(exist = false)
    private String patientName;
}
