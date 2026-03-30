package com.dsite.medical.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dsite.medical.common.core.domain.AjaxResult;
import com.dsite.medical.common.core.domain.PageQuery;
import com.dsite.medical.common.core.domain.PageResult;
import com.dsite.medical.common.utils.StringUtils;
import com.dsite.medical.system.domain.entity.SysMenu;
import com.dsite.medical.system.mapper.SysMenuMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 菜单Controller
 */
@Tag(name = "菜单管理")
@RestController
@RequestMapping("/system/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuMapper menuMapper;

    @Operation(summary = "菜单列表")
    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    @GetMapping("/list")
    public AjaxResult list(SysMenu sysMenu) {
        List<SysMenu> menus = menuMapper.selectMenuListByRoleId(null);
        return AjaxResult.success(menus);
    }

    @Operation(summary = "菜单树列表")
    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    @GetMapping("/treeselect")
    public AjaxResult treeselect() {
        List<SysMenu> menus = menuMapper.selectMenuTreeAll();
        return AjaxResult.success(buildMenuTree(menus));
    }

    @Operation(summary = "菜单详情")
    @PreAuthorize("@ss.hasPermi('system:menu:query')")
    @GetMapping("/{menuId}")
    public AjaxResult getInfo(@PathVariable("menuId") Long menuId) {
        return AjaxResult.success(menuMapper.selectById(menuId));
    }

    @Operation(summary = "新增菜单")
    @PreAuthorize("@ss.hasPermi('system:menu:add')")
    @PostMapping
    public AjaxResult add(@RequestBody SysMenu sysMenu) {
        return AjaxResult.success(menuMapper.insert(sysMenu) > 0);
    }

    @Operation(summary = "修改菜单")
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody SysMenu sysMenu) {
        return AjaxResult.success(menuMapper.updateById(sysMenu) > 0);
    }

    @Operation(summary = "删除菜单")
    @PreAuthorize("@ss.hasPermi('system:menu:remove')")
    @DeleteMapping("/{menuId}")
    public AjaxResult remove(@PathVariable Long menuId) {
        // 校验是否有子菜单
        LambdaQueryWrapper<SysMenu> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysMenu::getParentId, menuId);
        Long count = menuMapper.selectCount(lqw);
        if (count > 0) {
            return AjaxResult.error("存在子菜单，不允许删除");
        }
        return AjaxResult.success(menuMapper.deleteById(menuId) > 0);
    }

    private List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        List<SysMenu> returnList = new ArrayList<>();
        List<Long> tempList = menus.stream().map(SysMenu::getMenuId).toList();
        for (SysMenu menu : menus) {
            if (!tempList.contains(menu.getParentId())) {
                recursionFn(menus, menu);
                returnList.add(menu);
            }
        }
        if (returnList.isEmpty()) {
            returnList = menus;
        }
        return returnList;
    }

    private void recursionFn(List<SysMenu> list, SysMenu t) {
        List<SysMenu> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysMenu tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    private List<SysMenu> getChildList(List<SysMenu> list, SysMenu t) {
        List<SysMenu> tlist = new ArrayList<>();
        for (SysMenu n : list) {
            if (n.getParentId() != null && n.getParentId().equals(t.getMenuId())) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    private boolean hasChild(List<SysMenu> list, SysMenu t) {
        return getChildList(list, t).size() > 0;
    }
}
