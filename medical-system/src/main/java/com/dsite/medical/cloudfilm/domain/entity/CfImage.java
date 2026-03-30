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
 * 影像实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cf_image")
public class CfImage extends Entity {

    /** 影像ID */
    @TableId(value = "image_id", type = IdType.INPUT)
    private String imageId;

    /** 检查ID */
    @TableField("exam_id")
    private String examId;

    /** DICOM UID */
    @TableField("dicom_uid")
    private String dicomUid;

    /** 影像存储路径 */
    @TableField("image_path")
    private String imagePath;

    /** 缩略图路径 */
    @TableField("thumbnail_path")
    private String thumbnailPath;

    /** 系列号 */
    @TableField("series_number")
    private Integer seriesNumber;

    /** 图像号 */
    @TableField("image_number")
    private Integer imageNumber;

    /** 模态 */
    @TableField("modality")
    private String modality;

    /** 是否关键影像 */
    @TableField("is_key_image")
    private String isKeyImage;

    /** 患者姓名（非数据库字段） */
    @TableField(exist = false)
    private String patientName;

    /** 检查类型（非数据库字段） */
    @TableField(exist = false)
    private String examType;
}
