package com.dsite.medical.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dsite.medical.common.core.domain.AjaxResult;
import com.dsite.medical.common.core.domain.PageQuery;
import com.dsite.medical.common.core.domain.PageResult;
import com.dsite.medical.common.utils.StringUtils;
import com.dsite.medical.system.domain.entity.SysConfig;
import com.dsite.medical.system.mapper.SysConfigMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 参数Controller
 */
@Tag(name = "参数管理")
@RestController
@RequestMapping("/system/config")
@RequiredArgsConstructor
public class SysConfigController {

    private final SysConfigMapper configMapper;

    @Operation(summary = "参数列表")
    @PreAuthorize("@ss.hasPermi('system:config:list')")
    @GetMapping("/list")
    public PageResult<SysConfig> list(SysConfig sysConfig, PageQuery pageQuery) {
        LambdaQueryWrapper<SysConfig> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotEmpty(sysConfig.getConfigName())) {
            lqw.like(SysConfig::getConfigName, sysConfig.getConfigName());
        }
        if (StringUtils.isNotEmpty(sysConfig.getConfigKey())) {
            lqw.like(SysConfig::getConfigKey, sysConfig.getConfigKey());
        }
        lqw.orderByDesc(SysConfig::getConfigId);
        Page<SysConfig> page = configMapper.selectPage(pageQuery.build(), lqw);
        return PageResult.of(page.getRecords(), page.getTotal());
    }

    @Operation(summary = "参数详情")
    @PreAuthorize("@ss.hasPermi('system:config:query')")
    @GetMapping("/{configId}")
    public AjaxResult getInfo(@PathVariable Long configId) {
        return AjaxResult.success(configMapper.selectById(configId));
    }

    @Operation(summary = "新增参数")
    @PreAuthorize("@ss.hasPermi('system:config:add')")
    @PostMapping
    public AjaxResult add(@RequestBody SysConfig sysConfig) {
        return AjaxResult.success(configMapper.insert(sysConfig) > 0);
    }

    @Operation(summary = "修改参数")
    @PreAuthorize("@ss.hasPermi('system:config:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody SysConfig sysConfig) {
        return AjaxResult.success(configMapper.updateById(sysConfig) > 0);
    }

    @Operation(summary = "删除参数")
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @DeleteMapping("/{configIds}")
    public AjaxResult remove(@PathVariable Long[] configIds) {
        return AjaxResult.success(configMapper.deleteBatchIds(java.util.Arrays.asList(configIds)) > 0);
    }
}
