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
 * 诊断报告实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cf_diagnosis_report")
public class CfDiagnosisReport extends BaseEntity {

    /** 报告ID */
    @TableId(value = "report_id", type = IdType.INPUT)
    private String reportId;

    /** 检查ID */
    @TableField("exam_id")
    private String examId;

    /** 患者ID */
    @TableField("patient_id")
    private String patientId;

    /** 临床资料 */
    @TableField("clinical_data")
    private String clinicalData;

    /** 检查所见 */
    @TableField("exam_findings")
    private String examFindings;

    /** 诊断意见 */
    @TableField("diagnosis")
    private String diagnosis;

    /** 报告状态 */
    @TableField("report_status")
    private String reportStatus;

    /** 撰写医生 */
    @TableField("create_doctor")
    private String createDoctor;

    /** 审核医生 */
    @TableField("review_doctor")
    private String reviewDoctor;

    /** 审核时间 */
    @TableField("review_time")
    private Date reviewTime;

    /** 删除标志 */
    @TableField("del_flag")
    @TableLogic
    private String delFlag;

    /** 患者姓名（非数据库字段） */
    @TableField(exist = false)
    private String patientName;

    /** 检查类型（非数据库字段） */
    @TableField(exist = false)
    private String examType;
}
