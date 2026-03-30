package com.dsite.medical.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dsite.medical.common.core.domain.AjaxResult;
import com.dsite.medical.common.core.domain.PageQuery;
import com.dsite.medical.common.core.domain.PageResult;
import com.dsite.medical.common.utils.StringUtils;
import com.dsite.medical.system.domain.entity.SysOperLog;
import com.dsite.medical.system.mapper.SysOperLogMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志Controller
 */
@Tag(name = "操作日志")
@RestController
@RequestMapping("/monitor/operlog")
@RequiredArgsConstructor
public class SysOperLogController {

    private final SysOperLogMapper operLogMapper;

    @Operation(summary = "操作日志列表")
    @PreAuthorize("@ss.hasPermi('monitor:operlog:list')")
    @GetMapping("/list")
    public PageResult<SysOperLog> list(SysOperLog sysOperLog, PageQuery pageQuery) {
        LambdaQueryWrapper<SysOperLog> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotEmpty(sysOperLog.getTitle())) {
            lqw.like(SysOperLog::getTitle, sysOperLog.getTitle());
        }
        if (StringUtils.isNotEmpty(sysOperLog.getOperName())) {
            lqw.like(SysOperLog::getOperName, sysOperLog.getOperName());
        }
        if (sysOperLog.getStatus() != null) {
            lqw.eq(SysOperLog::getStatus, sysOperLog.getStatus());
        }
        lqw.orderByDesc(SysOperLog::getOperId);
        Page<SysOperLog> page = operLogMapper.selectPage(pageQuery.build(), lqw);
        return PageResult.of(page.getRecords(), page.getTotal());
    }

    @Operation(summary = "操作日志详情")
    @PreAuthorize("@ss.hasPermi('monitor:operlog:query')")
    @GetMapping("/{operId}")
    public AjaxResult getInfo(@PathVariable Long operId) {
        return AjaxResult.success(operLogMapper.selectById(operId));
    }

    @Operation(summary = "删除操作日志")
    @PreAuthorize("@ss.hasPermi('monitor:operlog:remove')")
    @DeleteMapping("/{operIds}")
    public AjaxResult remove(@PathVariable Long[] operIds) {
        return AjaxResult.success(operLogMapper.deleteBatchIds(java.util.Arrays.asList(operIds)) > 0);
    }

    @Operation(summary = "清空操作日志")
    @PreAuthorize("@ss.hasPermi('monitor:operlog:remove')")
    @DeleteMapping("/clean")
    public AjaxResult clean() {
        return AjaxResult.success(operLogMapper.delete(null) > 0);
    }
}
