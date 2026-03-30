package com.dsite.medical.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dsite.medical.common.constant.Constants;
import com.dsite.medical.common.core.domain.PageQuery;
import com.dsite.medical.common.core.domain.PageResult;
import com.dsite.medical.common.exception.ServiceException;
import com.dsite.medical.common.utils.StringUtils;
import com.dsite.medical.system.domain.entity.SysRole;
import com.dsite.medical.system.mapper.SysRoleMapper;
import com.dsite.medical.system.service.ISysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 角色Service实现
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    @Autowired
    private SysRoleMapper roleMapper;

    @Override
    public List<SysRole> selectRoleList(SysRole sysRole) {
        LambdaQueryWrapper<SysRole> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotEmpty(sysRole.getRoleName())) {
            lqw.like(SysRole::getRoleName, sysRole.getRoleName());
        }
        if (StringUtils.isNotEmpty(sysRole.getRoleKey())) {
            lqw.like(SysRole::getRoleKey, sysRole.getRoleKey());
        }
        if (StringUtils.isNotEmpty(sysRole.getStatus())) {
            lqw.eq(SysRole::getStatus, sysRole.getStatus());
        }
        lqw.orderByAsc(SysRole::getRoleSort);
        return list(lqw);
    }

    @Override
    public PageResult<SysRole> selectRolePage(SysRole sysRole, PageQuery pageQuery) {
        LambdaQueryWrapper<SysRole> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotEmpty(sysRole.getRoleName())) {
            lqw.like(SysRole::getRoleName, sysRole.getRoleName());
        }
        if (StringUtils.isNotEmpty(sysRole.getRoleKey())) {
            lqw.like(SysRole::getRoleKey, sysRole.getRoleKey());
        }
        if (StringUtils.isNotEmpty(sysRole.getStatus())) {
            lqw.eq(SysRole::getStatus, sysRole.getStatus());
        }
        lqw.orderByAsc(SysRole::getRoleSort);
        Page<SysRole> page = page(pageQuery.build(), lqw);
        return PageResult.of(page.getRecords(), page.getTotal());
    }

    @Override
    public List<SysRole> selectRolesByUserId(Long userId) {
        return baseMapper.selectList(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRole(SysRole sysRole) {
        // 验证角色权限字符串唯一性
        LambdaQueryWrapper<SysRole> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysRole::getRoleKey, sysRole.getRoleKey());
        Long count = baseMapper.selectCount(lqw);
        if (count > 0) {
            throw new ServiceException("角色权限字符串已存在");
        }
        // 设置状态
        if (StringUtils.isEmpty(sysRole.getStatus())) {
            sysRole.setStatus(Constants.STATUS_NORMAL);
        }
        // 保存角色
        int rows = baseMapper.insert(sysRole);
        // 保存角色菜单关联
        if (sysRole.getMenuIds() != null && sysRole.getMenuIds().length > 0) {
            for (Long menuId : sysRole.getMenuIds()) {
                roleMapper.insertRoleMenu(sysRole.getRoleId(), menuId);
            }
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRole(SysRole sysRole) {
        // 角色权限字符串不允许修改
        sysRole.setRoleKey(null);
        // 更新角色
        int rows = baseMapper.updateById(sysRole);
        // 更新角色菜单关联
        roleMapper.deleteRoleMenu(sysRole.getRoleId());
        if (sysRole.getMenuIds() != null && sysRole.getMenuIds().length > 0) {
            for (Long menuId : sysRole.getMenuIds()) {
                roleMapper.insertRoleMenu(sysRole.getRoleId(), menuId);
            }
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRoleByIds(Long[] roleIds) {
        for (Long roleId : roleIds) {
            SysRole role = baseMapper.selectById(roleId);
            if (role != null && "admin".equals(role.getRoleKey())) {
                throw new ServiceException("不能删除超级管理员角色");
            }
        }
        int rows = baseMapper.deleteBatchIds(Arrays.asList(roleIds));
        // 删除角色菜单关联
        for (Long roleId : roleIds) {
            roleMapper.deleteRoleMenu(roleId);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRoleMenuAuth(Long roleId, Long[] menuIds) {
        roleMapper.deleteRoleMenu(roleId);
        if (menuIds != null && menuIds.length > 0) {
            for (Long menuId : menuIds) {
                roleMapper.insertRoleMenu(roleId, menuId);
            }
        }
        return menuIds != null ? menuIds.length : 0;
    }
}
