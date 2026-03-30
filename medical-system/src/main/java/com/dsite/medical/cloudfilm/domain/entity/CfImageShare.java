package com.dsite.medical.cloudfilm.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dsite.medical.common.core.domain.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 影像分享实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cf_image_share")
public class CfImageShare extends Entity {

    /** 分享ID */
    @TableId(value = "share_id", type = IdType.INPUT)
    private String shareId;

    /** 分享编号 */
    @TableField("share_no")
    private String shareNo;

    /** 检查ID */
    @TableField("exam_id")
    private String examId;

    /** 提取码 */
    @TableField("access_code")
    private String accessCode;

    /** 过期时间 */
    @TableField("expire_date")
    private Date expireDate;

    /** 查看次数 */
    @TableField("view_count")
    private Integer viewCount;

    /** 下载次数 */
    @TableField("download_count")
    private Integer downloadCount;

    /** 是否允许下载 */
    @TableField("allow_download")
    private String allowDownload;

    /** 创建人 */
    @TableField("create_user")
    private String createUser;

    /** 患者姓名（非数据库字段） */
    @TableField(exist = false)
    private String patientName;

    /** 检查类型（非数据库字段） */
    @TableField(exist = false)
    private String examType;
}
