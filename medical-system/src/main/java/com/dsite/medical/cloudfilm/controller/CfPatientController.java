package com.dsite.medical.cloudfilm.controller;

import com.dsite.medical.cloudfilm.domain.entity.CfPatient;
import com.dsite.medical.cloudfilm.service.ICfPatientService;
import com.dsite.medical.common.core.domain.AjaxResult;
import com.dsite.medical.common.core.domain.PageQuery;
import com.dsite.medical.common.core.domain.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 患者Controller
 */
@Tag(name = "患者管理")
@RestController
@RequestMapping("/cloudfilm/patient")
@RequiredArgsConstructor
public class CfPatientController {

    private final ICfPatientService patientService;

    @Operation(summary = "患者列表")
    @PreAuthorize("@ss.hasPermi('cloudfilm:patient:list')")
    @GetMapping("/list")
    public PageResult<CfPatient> list(CfPatient cfPatient, PageQuery pageQuery) {
        return patientService.selectPatientPage(cfPatient, pageQuery);
    }

    @Operation(summary = "患者详情")
    @PreAuthorize("@ss.hasPermi('cloudfilm:patient:query')")
    @GetMapping("/{patientId}")
    public AjaxResult getInfo(@PathVariable("patientId") String patientId) {
        return AjaxResult.success(patientService.getById(patientId));
    }

    @Operation(summary = "新增患者")
    @PreAuthorize("@ss.hasPermi('cloudfilm:patient:add')")
    @PostMapping
    public AjaxResult add(@RequestBody CfPatient cfPatient) {
        return AjaxResult.success(patientService.insertPatient(cfPatient) > 0);
    }

    @Operation(summary = "修改患者")
    @PreAuthorize("@ss.hasPermi('cloudfilm:patient:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody CfPatient cfPatient) {
        return AjaxResult.success(patientService.updatePatient(cfPatient) > 0);
    }

    @Operation(summary = "删除患者")
    @PreAuthorize("@ss.hasPermi('cloudfilm:patient:remove')")
    @DeleteMapping("/{patientIds}")
    public AjaxResult remove(@PathVariable String[] patientIds) {
        return AjaxResult.success(patientService.deletePatientByIds(patientIds) > 0);
    }
}
