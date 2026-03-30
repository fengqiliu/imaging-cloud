package com.dsite.medical.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dsite.medical.common.core.domain.AjaxResult;
import com.dsite.medical.common.core.domain.PageQuery;
import com.dsite.medical.common.core.domain.PageResult;
import com.dsite.medical.common.utils.StringUtils;
import com.dsite.medical.system.domain.entity.SysDictType;
import com.dsite.medical.system.mapper.SysDictTypeMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 字典Controller
 */
@Tag(name = "字典管理")
@RestController
@RequestMapping("/system/dict/type")
@RequiredArgsConstructor
public class SysDictTypeController {

    private final SysDictTypeMapper dictTypeMapper;

    @Operation(summary = "字典类型列表")
    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    @GetMapping("/list")
    public PageResult<SysDictType> list(SysDictType sysDictType, PageQuery pageQuery) {
        LambdaQueryWrapper<SysDictType> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotEmpty(sysDictType.getDictName())) {
            lqw.like(SysDictType::getDictName, sysDictType.getDictName());
        }
        if (StringUtils.isNotEmpty(sysDictType.getDictType())) {
            lqw.like(SysDictType::getDictType, sysDictType.getDictType());
        }
        lqw.orderByDesc(SysDictType::getDictId);
        Page<SysDictType> page = dictTypeMapper.selectPage(pageQuery.build(), lqw);
        return PageResult.of(page.getRecords(), page.getTotal());
    }

    @Operation(summary = "字典类型详情")
    @PreAuthorize("@ss.hasPermi('system:dict:query')")
    @GetMapping("/{dictId}")
    public AjaxResult getInfo(@PathVariable Long dictId) {
        return AjaxResult.success(dictTypeMapper.selectById(dictId));
    }

    @Operation(summary = "新增字典类型")
    @PreAuthorize("@ss.hasPermi('system:dict:add')")
    @PostMapping
    public AjaxResult add(@RequestBody SysDictType sysDictType) {
        return AjaxResult.success(dictTypeMapper.insert(sysDictType) > 0);
    }

    @Operation(summary = "修改字典类型")
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody SysDictType sysDictType) {
        return AjaxResult.success(dictTypeMapper.updateById(sysDictType) > 0);
    }

    @Operation(summary = "删除字典类型")
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @DeleteMapping("/{dictIds}")
    public AjaxResult remove(@PathVariable Long[] dictIds) {
        return AjaxResult.success(dictTypeMapper.deleteBatchIds(java.util.Arrays.asList(dictIds)) > 0);
    }
}
