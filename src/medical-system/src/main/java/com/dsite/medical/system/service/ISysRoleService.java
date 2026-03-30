package com.dsite.medical.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dsite.medical.common.core.domain.PageQuery;
import com.dsite.medical.common.core.domain.PageResult;
import com.dsite.medical.system.domain.entity.SysRole;

import java.util.List;

/**
 * 角色Service接口
 */
public interface ISysRoleService extends IService<SysRole> {

    /**
     * 查询角色列表
     */
    List<SysRole> selectRoleList(SysRole sysRole);

    /**
     * 分页查询角色列表
     */
    PageResult<SysRole> selectRolePage(SysRole sysRole, PageQuery pageQuery);

    /**
     * 根据用户ID查询角色
     */
    List<SysRole> selectRolesByUserId(Long userId);

    /**
     * 新增角色
     */
    int insertRole(SysRole sysRole);

    /**
     * 修改角色
     */
    int updateRole(SysRole sysRole);

    /**
     * 删除角色
     */
    int deleteRoleByIds(Long[] roleIds);

    /**
     * 分配角色菜单
     */
    int insertRoleMenuAuth(Long roleId, Long[] menuIds);
}
