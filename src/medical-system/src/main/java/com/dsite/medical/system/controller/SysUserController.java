package com.dsite.medical.system.controller;

import com.dsite.medical.common.core.domain.AjaxResult;
import com.dsite.medical.common.core.domain.PageQuery;
import com.dsite.medical.common.core.domain.PageResult;
import com.dsite.medical.common.utils.StringUtils;
import com.dsite.medical.system.domain.entity.SysUser;
import com.dsite.medical.system.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 用户Controller
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/system/user")
@RequiredArgsConstructor
public class SysUserController {

    private final ISysUserService userService;

    @Operation(summary = "用户列表")
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public PageResult<SysUser> list(SysUser sysUser, PageQuery pageQuery) {
        return userService.selectUserPage(sysUser, pageQuery);
    }

    @Operation(summary = "用户详情")
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/{userId}")
    public AjaxResult getInfo(@PathVariable("userId") Long userId) {
        return AjaxResult.success(userService.getById(userId));
    }

    @Operation(summary = "新增用户")
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @PostMapping
    public AjaxResult add(@RequestBody SysUser sysUser) {
        return AjaxResult.success(userService.insertUser(sysUser) > 0);
    }

    @Operation(summary = "修改用户")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody SysUser sysUser) {
        if (StringUtils.isNotNull(sysUser.getUserId()) && "admin".equals(sysUser.getUserName())) {
            return AjaxResult.error("不能修改超级管理员");
        }
        return AjaxResult.success(userService.updateUser(sysUser) > 0);
    }

    @Operation(summary = "删除用户")
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds) {
        return AjaxResult.success(userService.deleteUserByIds(userIds) > 0);
    }

    @Operation(summary = "重置密码")
    @PreAuthorize("@ss.hasPermi('system:user:resetPwd')")
    @PutMapping("/resetPwd")
    public AjaxResult resetPwd(@RequestBody SysUser user) {
        userService.resetUserPassword(user.getUserId(), user.getPassword());
        return AjaxResult.success();
    }

    @Operation(summary = "分配角色")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @PutMapping("/authRole")
    public AjaxResult authRole(@RequestBody SysUser user) {
        return AjaxResult.success(userService.insertUserAuth(user.getUserId(), user.getRoleIds().toArray(new Long[0])) > 0);
    }
}
