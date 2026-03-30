package com.dsite.medical.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dsite.medical.common.core.domain.PageQuery;
import com.dsite.medical.common.core.domain.PageResult;
import com.dsite.medical.system.domain.entity.SysUser;

/**
 * 用户Service接口
 */
public interface ISysUserService extends IService<SysUser> {

    /**
     * 根据用户名查询用户
     */
    SysUser selectUserByUserName(String userName);

    /**
     * 分页查询用户列表
     */
    PageResult<SysUser> selectUserPage(SysUser sysUser, PageQuery pageQuery);

    /**
     * 新增用户
     */
    int insertUser(SysUser sysUser);

    /**
     * 修改用户
     */
    int updateUser(SysUser sysUser);

    /**
     * 删除用户
     */
    int deleteUserByIds(Long[] userIds);

    /**
     * 重置密码
     */
    int resetUserPassword(Long userId, String password);

    /**
     * 分配用户角色
     */
    int insertUserAuth(Long userId, Long[] roleIds);
}
