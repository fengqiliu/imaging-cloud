package com.dsite.medical.admin.controller;

import com.dsite.medical.cloudfilm.domain.entity.CfImage;
import com.dsite.medical.cloudfilm.service.ICfImageService;
import com.dsite.medical.cloudfilm.storage.StorageService;
import com.dsite.medical.common.core.domain.AjaxResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DICOM影像查看Controller
 * 提供OHIF Viewer所需的数据接口
 */
@Tag(name = "DICOM查看器")
@RestController
@RequestMapping("/dicom")
@RequiredArgsConstructor
public class DicomViewerController {

    private final ICfImageService imageService;

    @Autowired
    @Qualifier("localStorageService")
    private StorageService storageService;

    @Operation(summary = "获取检查的所有影像列表")
    @GetMapping("/studies/{examId}/series")
    public AjaxResult getStudySeries(@PathVariable String examId) {
        List<CfImage> images = imageService.selectImageListByExamId(examId);

        // 构建OHIF兼容的Study/Series数据结构
        Map<String, Object> studyData = new HashMap<>();
        studyData.put("studyInstanceUID", examId);
        studyData.put("patientId", "");
        studyData.put("patientName", "");
        studyData.put("studyDate", "");

        // 按Series分组
        // 这里简化处理，实际需要更复杂的数据转换
        return AjaxResult.success(studyData);
    }

    @Operation(summary = "获取影像实例")
    @GetMapping("/instances/{imageId}")
    public AjaxResult getInstance(@PathVariable String imageId) {
        CfImage image = imageService.getById(imageId);
        if (image == null) {
            return AjaxResult.error("影像不存在");
        }

        // 获取文件URL
        String imageUrl = imageService.getImageUrl(imageId);

        Map<String, Object> instanceData = new HashMap<>();
        instanceData.put("imageId", image.getImageId());
        instanceData.put("sopInstanceUID", image.getDicomUid());
        instanceData.put("instanceNumber", image.getImageNumber());
        instanceData.put("url", imageUrl);

        return AjaxResult.success(instanceData);
    }

    @Operation(summary = "WADO-RS获取影像数据")
    @GetMapping("/studies/{studyInstanceUID}/series/{seriesInstanceUID}/instances/{sopInstanceUID}")
    public byte[] getInstanceData(@PathVariable String studyInstanceUID,
                                  @PathVariable String seriesInstanceUID,
                                  @PathVariable String sopInstanceUID) {
        // 简化实现，实际需要通过DICOM UID查询影像
        // 返回DICOM文件二进制数据
        return new byte[0];
    }

    @Operation(summary = "获取OHIF配置")
    @GetMapping("/config")
    public AjaxResult getOhifConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("wadoUri", "/dicom/instances");
        config.put("wadoRs", "/dicom/studies");
        config.put("qido", "/dicom/studies");
        return AjaxResult.success(config);
    }
}
