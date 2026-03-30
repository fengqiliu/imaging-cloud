package com.dsite.medical.cloudfilm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dsite.medical.cloudfilm.domain.entity.CfImage;
import com.dsite.medical.common.core.domain.PageQuery;
import com.dsite.medical.common.core.domain.PageResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 影像Service接口
 */
public interface ICfImageService extends IService<CfImage> {

    /**
     * 上传影像
     *
     * @param file     DICOM文件
     * @param examId   检查ID
     * @return 影像信息
     */
    CfImage uploadImage(MultipartFile file, String examId);

    /**
     * 批量上传影像
     *
     * @param files    DICOM文件数组
     * @param examId   检查ID
     * @return 上传数量
     */
    int batchUploadImages(MultipartFile[] files, String examId);

    /**
     * 分页查询影像列表
     */
    PageResult<CfImage> selectImagePage(CfImage cfImage, PageQuery pageQuery);

    /**
     * 查询检查关联影像
     */
    List<CfImage> selectImageListByExamId(String examId);

    /**
     * 删除影像
     */
    int deleteImageByIds(String[] imageIds);

    /**
     * 标记关键影像
     */
    int markKeyImage(String imageId, boolean isKey);

    /**
     * 获取影像访问URL
     */
    String getImageUrl(String imageId);
}
