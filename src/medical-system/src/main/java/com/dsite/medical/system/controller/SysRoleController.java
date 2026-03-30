package com.dsite.medical.system.controller;

import com.dsite.medical.common.core.domain.AjaxResult;
import com.dsite.medical.common.core.domain.PageQuery;
import com.dsite.medical.common.core.domain.PageResult;
import com.dsite.medical.common.utils.StringUtils;
import com.dsite.medical.system.domain.entity.SysRole;
import com.dsite.medical.system.service.ISysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 角色Controller
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/system/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final ISysRoleService roleService;

    @Operation(summary = "角色列表")
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/list")
    public PageResult<SysRole> list(SysRole sysRole, PageQuery pageQuery) {
        return roleService.selectRolePage(sysRole, pageQuery);
    }

    @Operation(summary = "角色详情")
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping("/{roleId}")
    public AjaxResult getInfo(@PathVariable("roleId") Long roleId) {
        return AjaxResult.success(roleService.getById(roleId));
    }

    @Operation(summary = "新增角色")
    @PreAuthorize("@ss.hasPermi('system:role:add')")
    @PostMapping
    public AjaxResult add(@RequestBody SysRole sysRole) {
        return AjaxResult.success(roleService.insertRole(sysRole) > 0);
    }

    @Operation(summary = "修改角色")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody SysRole sysRole) {
        if (StringUtils.isNotNull(sysRole.getRoleId()) && "admin".equals(sysRole.getRoleKey())) {
            return AjaxResult.error("不能修改超级管理员角色");
        }
        return AjaxResult.success(roleService.updateRole(sysRole) > 0);
    }

    @Operation(summary = "删除角色")
    @PreAuthorize("@ss.hasPermi('system:role:remove')")
    @DeleteMapping("/{roleIds}")
    public AjaxResult remove(@PathVariable Long[] roleIds) {
        return AjaxResult.success(roleService.deleteRoleByIds(roleIds) > 0);
    }

    @Operation(summary = "分配菜单权限")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @PutMapping("/authMenu")
    public AjaxResult authMenu(@RequestBody SysRole sysRole) {
        return AjaxResult.success(roleService.insertRoleMenuAuth(sysRole.getRoleId(), sysRole.getMenuIds()) > 0);
    }
}
