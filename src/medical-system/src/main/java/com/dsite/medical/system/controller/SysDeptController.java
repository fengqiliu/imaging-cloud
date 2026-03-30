package com.dsite.medical.system.controller;

import com.dsite.medical.common.core.domain.AjaxResult;
import com.dsite.medical.system.domain.entity.SysDept;
import com.dsite.medical.system.service.ISysDeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门Controller
 */
@Tag(name = "院区管理")
@RestController
@RequestMapping("/system/dept")
@RequiredArgsConstructor
public class SysDeptController {

    private final ISysDeptService deptService;

    @Operation(summary = "部门列表")
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/list")
    public AjaxResult list(SysDept sysDept) {
        List<SysDept> depts = deptService.selectDeptList(sysDept);
        return AjaxResult.success(depts);
    }

    @Operation(summary = "部门树列表")
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/treeselect")
    public AjaxResult treeselect() {
        List<SysDept> depts = deptService.selectDeptTreeList();
        return AjaxResult.success(depts);
    }

    @Operation(summary = "部门详情")
    @PreAuthorize("@ss.hasPermi('system:dept:query')")
    @GetMapping("/{deptId}")
    public AjaxResult getInfo(@PathVariable("deptId") Long deptId) {
        return AjaxResult.success(deptService.selectDeptById(deptId));
    }

    @Operation(summary = "新增部门")
    @PreAuthorize("@ss.hasPermi('system:dept:add')")
    @PostMapping
    public AjaxResult add(@RequestBody SysDept sysDept) {
        return AjaxResult.success(deptService.insertDept(sysDept) > 0);
    }

    @Operation(summary = "修改部门")
    @PreAuthorize("@ss.hasPermi('system:dept:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody SysDept sysDept) {
        return AjaxResult.success(deptService.updateDept(sysDept) > 0);
    }

    @Operation(summary = "删除部门")
    @PreAuthorize("@ss.hasPermi('system:dept:remove')")
    @DeleteMapping("/{deptId}")
    public AjaxResult remove(@PathVariable Long deptId) {
        if (deptId.equals(100L)) {
            return AjaxResult.error("不能删除总院");
        }
        return AjaxResult.success(deptService.deleteDeptById(deptId) > 0);
    }
}
