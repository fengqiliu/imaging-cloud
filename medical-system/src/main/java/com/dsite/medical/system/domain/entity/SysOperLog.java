package com.dsite.medical.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dsite.medical.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 操作日志实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_oper_log")
public class SysOperLog extends BaseEntity {

    /** 日志主键 */
    @TableId(value = "oper_id", type = IdType.AUTO)
    private Long operId;

    /** 模块标题 */
    @TableField("title")
    private String title;

    /** 业务类型 */
    @TableField("business_type")
    private Integer businessType;

    /** 方法名称 */
    @TableField("method")
    private String method;

    /** 请求方式 */
    @TableField("request_method")
    private String requestMethod;

    /** 操作类型 */
    @TableField("operator_type")
    private Integer operatorType;

    /** 操作人员 */
    @TableField("oper_name")
    private String operName;

    /** 部门名称 */
    @TableField("dept_name")
    private String deptName;

    /** 请求URL */
    @TableField("oper_url")
    private String operUrl;

    /** 主机地址 */
    @TableField("oper_ip")
    private String operIp;

    /** 操作地点 */
    @TableField("oper_location")
    private String operLocation;

    /** 请求参数 */
    @TableField("oper_param")
    private String operParam;

    /** 返回结果 */
    @TableField("json_result")
    private String jsonResult;

    /** 操作状态 */
    @TableField("status")
    private Integer status;

    /** 错误消息 */
    @TableField("error_msg")
    private String errorMsg;

    /** 操作时间 */
    @TableField("oper_time")
    private Date operTime;

    /** 消耗时间 */
    @TableField("cost_time")
    private Long costTime;
}
