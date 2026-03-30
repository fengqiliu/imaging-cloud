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
 * 患者实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cf_patient")
public class CfPatient extends BaseEntity {

    /** 患者ID */
    @TableId(value = "patient_id", type = IdType.INPUT)
    private String patientId;

    /** 患者姓名 */
    @TableField("patient_name")
    private String patientName;

    /** 性别 */
    @TableField("gender")
    private String gender;

    /** 出生日期 */
    @TableField("birth_date")
    private Date birthDate;

    /** 身份证号 */
    @TableField("id_card")
    private String idCard;

    /** 联系电话 */
    @TableField("phone")
    private String phone;

    /** 住址 */
    @TableField("address")
    private String address;

    /** 过敏史 */
    @TableField("allergy_history")
    private String allergyHistory;

    /** 病史 */
    @TableField("medical_history")
    private String medicalHistory;

    /** 删除标志 */
    @TableField("del_flag")
    @TableLogic
    private String delFlag;
}
