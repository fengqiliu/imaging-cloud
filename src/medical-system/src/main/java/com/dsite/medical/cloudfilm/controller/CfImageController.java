package com.dsite.medical.cloudfilm.controller;

import com.dsite.medical.cloudfilm.domain.entity.CfImage;
import com.dsite.medical.cloudfilm.service.ICfImageService;
import com.dsite.medical.common.core.domain.AjaxResult;
import com.dsite.medical.common.core.domain.PageQuery;
import com.dsite.medical.common.core.domain.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 影像Controller
 */
@Tag(name = "影像管理")
@RestController
@RequestMapping("/cloudfilm/image")
@RequiredArgsConstructor
public class CfImageController {

    private final ICfImageService imageService;

    @Operation(summary = "影像列表")
    @PreAuthorize("@ss.hasPermi('cloudfilm:image:list')")
    @GetMapping("/list")
    public PageResult<CfImage> list(CfImage cfImage, PageQuery pageQuery) {
        return imageService.selectImagePage(cfImage, pageQuery);
    }

    @Operation(summary = "检查影像列表")
    @PreAuthorize("@ss.hasPermi('cloudfilm:image:list')")
    @GetMapping("/exam/{examId}")
    public AjaxResult getExamImages(@PathVariable String examId) {
        List<CfImage> list = imageService.selectImageListByExamId(examId);
        return AjaxResult.success(list);
    }

    @Operation(summary = "影像详情")
    @PreAuthorize("@ss.hasPermi('cloudfilm:image:query')")
    @GetMapping("/{imageId}")
    public AjaxResult getInfo(@PathVariable String imageId) {
        return AjaxResult.success(imageService.getById(imageId));
    }

    @Operation(summary = "上传影像")
    @PreAuthorize("@ss.hasPermi('cloudfilm:image:upload')")
    @PostMapping("/upload")
    public AjaxResult upload(@RequestParam("file") MultipartFile file,
                            @RequestParam("examId") String examId) {
        CfImage image = imageService.uploadImage(file, examId);
        return AjaxResult.success(image);
    }

    @Operation(summary = "批量上传影像")
    @PreAuthorize("@ss.hasPermi('cloudfilm:image:upload')")
    @PostMapping("/batch/upload")
    public AjaxResult batchUpload(@RequestParam("files") MultipartFile[] files,
                                  @RequestParam("examId") String examId) {
        int count = imageService.batchUploadImages(files, examId);
        return AjaxResult.success("成功上传" + count + "个影像文件");
    }

    @Operation(summary = "删除影像")
    @PreAuthorize("@ss.hasPermi('cloudfilm:image:remove')")
    @DeleteMapping("/{imageIds}")
    public AjaxResult remove(@PathVariable String[] imageIds) {
        return AjaxResult.success(imageService.deleteImageByIds(imageIds) > 0);
    }

    @Operation(summary = "标记关键影像")
    @PreAuthorize("@ss.hasPermi('cloudfilm:image:edit')")
    @PutMapping("/key/{imageId}")
    public AjaxResult markKey(@PathVariable String imageId, @RequestParam boolean isKey) {
        return AjaxResult.success(imageService.markKeyImage(imageId, isKey) > 0);
    }

    @Operation(summary = "获取影像URL")
    @PreAuthorize("@ss.hasPermi('cloudfilm:image:query')")
    @GetMapping("/url/{imageId}")
    public AjaxResult getUrl(@PathVariable String imageId) {
        return AjaxResult.success(imageService.getImageUrl(imageId));
    }
}
