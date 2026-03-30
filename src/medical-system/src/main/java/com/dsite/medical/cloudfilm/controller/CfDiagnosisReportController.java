package com.dsite.medical.cloudfilm.controller;

import com.dsite.medical.cloudfilm.domain.entity.CfDiagnosisReport;
import com.dsite.medical.cloudfilm.service.ICfDiagnosisReportService;
import com.dsite.medical.common.core.domain.AjaxResult;
import com.dsite.medical.common.core.domain.PageQuery;
import com.dsite.medical.common.core.domain.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 诊断报告Controller
 */
@Tag(name = "报告管理")
@RestController
@RequestMapping("/cloudfilm/report")
@RequiredArgsConstructor
public class CfDiagnosisReportController {

    private final ICfDiagnosisReportService reportService;

    @Operation(summary = "报告列表")
    @PreAuthorize("@ss.hasPermi('cloudfilm:report:list')")
    @GetMapping("/list")
    public PageResult<CfDiagnosisReport> list(CfDiagnosisReport cfDiagnosisReport, PageQuery pageQuery) {
        return reportService.selectReportPage(cfDiagnosisReport, pageQuery);
    }

    @Operation(summary = "报告详情")
    @PreAuthorize("@ss.hasPermi('cloudfilm:report:query')")
    @GetMapping("/{reportId}")
    public AjaxResult getInfo(@PathVariable String reportId) {
        return AjaxResult.success(reportService.getById(reportId));
    }

    @Operation(summary = "根据检查ID获取报告")
    @PreAuthorize("@ss.hasPermi('cloudfilm:report:query')")
    @GetMapping("/exam/{examId}")
    public AjaxResult getByExamId(@PathVariable String examId) {
        return AjaxResult.success(reportService.selectReportByExamId(examId));
    }

    @Operation(summary = "新增报告")
    @PreAuthorize("@ss.hasPermi('cloudfilm:report:add')")
    @PostMapping
    public AjaxResult add(@RequestBody CfDiagnosisReport cfDiagnosisReport) {
        return AjaxResult.success(reportService.insertReport(cfDiagnosisReport) > 0);
    }

    @Operation(summary = "修改报告")
    @PreAuthorize("@ss.hasPermi('cloudfilm:report:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody CfDiagnosisReport cfDiagnosisReport) {
        return AjaxResult.success(reportService.updateReport(cfDiagnosisReport) > 0);
    }

    @Operation(summary = "删除报告")
    @PreAuthorize("@ss.hasPermi('cloudfilm:report:remove')")
    @DeleteMapping("/{reportIds}")
    public AjaxResult remove(@PathVariable String[] reportIds) {
        return AjaxResult.success(reportService.deleteReportByIds(reportIds) > 0);
    }

    @Operation(summary = "审核报告")
    @PreAuthorize("@ss.hasPermi('cloudfilm:report:review')")
    @PutMapping("/review/{reportId}")
    public AjaxResult review(@PathVariable String reportId,
                            @RequestParam String status,
                            @RequestParam String reviewDoctor) {
        return AjaxResult.success(reportService.reviewReport(reportId, status, reviewDoctor) > 0);
    }
}
