package com.dsite.medical.cloudfilm.controller;

import com.dsite.medical.cloudfilm.domain.entity.CfExamination;
import com.dsite.medical.cloudfilm.service.ICfExaminationService;
import com.dsite.medical.common.core.domain.AjaxResult;
import com.dsite.medical.common.core.domain.PageQuery;
import com.dsite.medical.common.core.domain.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 检查单Controller
 */
@Tag(name = "检查管理")
@RestController
@RequestMapping("/cloudfilm/examination")
@RequiredArgsConstructor
public class CfExaminationController {

    private final ICfExaminationService examinationService;

    @Operation(summary = "检查单列表")
    @PreAuthorize("@ss.hasPermi('cloudfilm:examination:list')")
    @GetMapping("/list")
    public PageResult<CfExamination> list(CfExamination cfExamination, PageQuery pageQuery) {
        return examinationService.selectExaminationPage(cfExamination, pageQuery);
    }

    @Operation(summary = "检查单详情")
    @PreAuthorize("@ss.hasPermi('cloudfilm:examination:query')")
    @GetMapping("/{examId}")
    public AjaxResult getInfo(@PathVariable("examId") String examId) {
        return AjaxResult.success(examinationService.getById(examId));
    }

    @Operation(summary = "新增检查单")
    @PreAuthorize("@ss.hasPermi('cloudfilm:examination:add')")
    @PostMapping
    public AjaxResult add(@RequestBody CfExamination cfExamination) {
        return AjaxResult.success(examinationService.insertExamination(cfExamination) > 0);
    }

    @Operation(summary = "修改检查单")
    @PreAuthorize("@ss.hasPermi('cloudfilm:examination:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody CfExamination cfExamination) {
        return AjaxResult.success(examinationService.updateExamination(cfExamination) > 0);
    }

    @Operation(summary = "删除检查单")
    @PreAuthorize("@ss.hasPermi('cloudfilm:examination:remove')")
    @DeleteMapping("/{examIds}")
    public AjaxResult remove(@PathVariable String[] examIds) {
        return AjaxResult.success(examinationService.deleteExaminationByIds(examIds) > 0);
    }

    @Operation(summary = "更新检查状态")
    @PreAuthorize("@ss.hasPermi('cloudfilm:examination:edit')")
    @PutMapping("/status/{examId}")
    public AjaxResult updateStatus(@PathVariable String examId, @RequestParam String examStatus) {
        return AjaxResult.success(examinationService.updateExamStatus(examId, examStatus) > 0);
    }

    @Operation(summary = "患者检查历史")
    @PreAuthorize("@ss.hasPermi('cloudfilm:examination:list')")
    @GetMapping("/patient/{patientId}")
    public AjaxResult getPatientExamList(@PathVariable String patientId) {
        List<CfExamination> list = examinationService.selectExamListByPatientId(patientId);
        return AjaxResult.success(list);
    }
}
