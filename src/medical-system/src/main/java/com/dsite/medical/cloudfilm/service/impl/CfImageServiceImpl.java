package com.dsite.medical.cloudfilm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dsite.medical.cloudfilm.dicom.DicomParser;
import com.dsite.medical.cloudfilm.dicom.DicomParser.DicomInfo;
import com.dsite.medical.cloudfilm.domain.entity.CfImage;
import com.dsite.medical.cloudfilm.mapper.CfImageMapper;
import com.dsite.medical.cloudfilm.service.ICfImageService;
import com.dsite.medical.cloudfilm.storage.StorageService;
import com.dsite.medical.common.core.domain.PageQuery;
import com.dsite.medical.common.core.domain.PageResult;
import com.dsite.medical.common.utils.IdUtils;
import com.dsite.medical.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * 影像Service实现
 */
@Service
public class CfImageServiceImpl extends ServiceImpl<CfImageMapper, CfImage> implements ICfImageService {

    @Autowired
    private DicomParser dicomParser;

    @Autowired
    @Qualifier("localStorageService")
    private StorageService storageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CfImage uploadImage(MultipartFile file, String examId) {
        try {
            // 解析DICOM文件
            DicomInfo dicomInfo = dicomParser.parse(file);

            // 上传文件
            String filePath = storageService.upload(file.getInputStream(), file.getOriginalFilename(), "application/dicom");

            // 创建影像记录
            CfImage image = new CfImage();
            image.setImageId(IdUtils.imageId());
            image.setExamId(examId);
            image.setDicomUid(dicomInfo.getSopInstanceUID());
            image.setImagePath(filePath);
            image.setModality(dicomInfo.getModality());
            image.setSeriesNumber(dicomInfo.getSeriesNumber() != null ? dicomInfo.getSeriesNumber() : 0);
            image.setImageNumber(dicomInfo.getInstanceNumber() != null ? dicomInfo.getInstanceNumber() : 0);
            image.setIsKeyImage("0");

            baseMapper.insert(image);
            return image;
        } catch (Exception e) {
            throw new RuntimeException("上传影像失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchUploadImages(MultipartFile[] files, String examId) {
        int count = 0;
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    uploadImage(file, examId);
                    count++;
                } catch (Exception e) {
                    // 记录失败但继续上传其他文件
                    System.err.println("上传文件失败: " + file.getOriginalFilename() + ", " + e.getMessage());
                }
            }
        }
        return count;
    }

    @Override
    public PageResult<CfImage> selectImagePage(CfImage cfImage, PageQuery pageQuery) {
        LambdaQueryWrapper<CfImage> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotEmpty(cfImage.getExamId())) {
            lqw.eq(CfImage::getExamId, cfImage.getExamId());
        }
        if (StringUtils.isNotEmpty(cfImage.getModality())) {
            lqw.eq(CfImage::getModality, cfImage.getModality());
        }
        if (StringUtils.isNotEmpty(cfImage.getIsKeyImage())) {
            lqw.eq(CfImage::getIsKeyImage, cfImage.getIsKeyImage());
        }
        lqw.orderByAsc(CfImage::getSeriesNumber, CfImage::getImageNumber);
        Page<CfImage> page = page(pageQuery.build(), lqw);
        return PageResult.of(page.getRecords(), page.getTotal());
    }

    @Override
    public List<CfImage> selectImageListByExamId(String examId) {
        return baseMapper.selectImageListByExamId(examId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteImageByIds(String[] imageIds) {
        // 删除存储文件
        List<CfImage> images = baseMapper.selectBatchIds(Arrays.asList(imageIds));
        for (CfImage image : images) {
            if (StringUtils.isNotEmpty(image.getImagePath())) {
                try {
                    storageService.delete(image.getImagePath());
                } catch (Exception e) {
                    System.err.println("删除文件失败: " + e.getMessage());
                }
            }
            if (StringUtils.isNotEmpty(image.getThumbnailPath())) {
                try {
                    storageService.delete(image.getThumbnailPath());
                } catch (Exception e) {
                    System.err.println("删除缩略图失败: " + e.getMessage());
                }
            }
        }
        return baseMapper.deleteBatchIds(Arrays.asList(imageIds));
    }

    @Override
    public int markKeyImage(String imageId, boolean isKey) {
        CfImage image = new CfImage();
        image.setImageId(imageId);
        image.setIsKeyImage(isKey ? "1" : "0");
        return baseMapper.updateById(image);
    }

    @Override
    public String getImageUrl(String imageId) {
        CfImage image = baseMapper.selectById(imageId);
        if (image != null && StringUtils.isNotEmpty(image.getImagePath())) {
            return storageService.getUrl(image.getImagePath());
        }
        return null;
    }
}
