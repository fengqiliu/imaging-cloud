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
import com.dsite.medical.system.domain.entity.SysUser;
import com.dsite.medical.system.mapper.SysUserMapper;
import com.dsite.medical.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 用户Service实现
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public SysUser selectUserByUserName(String userName) {
        return userMapper.selectUserByUserName(userName);
    }

    @Override
    public PageResult<SysUser> selectUserPage(SysUser sysUser, PageQuery pageQuery) {
        LambdaQueryWrapper<SysUser> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotEmpty(sysUser.getUserName())) {
            lqw.like(SysUser::getUserName, sysUser.getUserName());
        }
        if (StringUtils.isNotEmpty(sysUser.getNickName())) {
            lqw.like(SysUser::getNickName, sysUser.getNickName());
        }
        if (StringUtils.isNotEmpty(sysUser.getStatus())) {
            lqw.eq(SysUser::getStatus, sysUser.getStatus());
        }
        if (StringUtils.isNotEmpty(sysUser.getPhone())) {
            lqw.like(SysUser::getPhone, sysUser.getPhone());
        }
        lqw.orderByDesc(SysUser::getUserId);
        Page<SysUser> page = page(pageQuery.build(), lqw);
        return PageResult.of(page.getRecords(), page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertUser(SysUser sysUser) {
        // 验证用户名唯一性
        SysUser existUser = selectUserByUserName(sysUser.getUserName());
        if (existUser != null) {
            throw new ServiceException("用户名已存在");
        }
        // 加密密码
        if (StringUtils.isEmpty(sysUser.getPassword())) {
            sysUser.setPassword(passwordEncoder.encode("admin123"));
        } else {
            sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        }
        // 设置状态
        if (StringUtils.isEmpty(sysUser.getStatus())) {
            sysUser.setStatus(Constants.STATUS_NORMAL);
        }
        // 保存用户
        int rows = baseMapper.insert(sysUser);
        // 保存用户角色关联
        if (sysUser.getRoleIds() != null && sysUser.getRoleIds().size() > 0) {
            for (Long roleId : sysUser.getRoleIds()) {
                userMapper.insertUserRole(sysUser.getUserId(), roleId);
            }
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateUser(SysUser sysUser) {
        // 用户名不允许修改
        sysUser.setUserName(null);
        sysUser.setPassword(null);
        // 更新用户
        int rows = baseMapper.updateById(sysUser);
        // 更新用户角色关联
        if (sysUser.getRoleIds() != null && sysUser.getRoleIds().size() > 0) {
            userMapper.deleteUserRole(sysUser.getUserId());
            for (Long roleId : sysUser.getRoleIds()) {
                userMapper.insertUserRole(sysUser.getUserId(), roleId);
            }
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteUserByIds(Long[] userIds) {
        for (Long userId : userIds) {
            SysUser user = baseMapper.selectById(userId);
            if (user != null && "admin".equals(user.getUserName())) {
                throw new ServiceException("不能删除超级管理员");
            }
        }
        // 逻辑删除
        int rows = baseMapper.deleteBatchIds(Arrays.asList(userIds));
        // 删除用户角色关联
        for (Long userId : userIds) {
            userMapper.deleteUserRole(userId);
        }
        return rows;
    }

    @Override
    public int resetUserPassword(Long userId, String password) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setPassword(passwordEncoder.encode(password));
        return baseMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertUserAuth(Long userId, Long[] roleIds) {
        userMapper.deleteUserRole(userId);
        if (roleIds != null && roleIds.length > 0) {
            for (Long roleId : roleIds) {
                userMapper.insertUserRole(userId, roleId);
            }
        }
        return roleIds != null ? roleIds.length : 0;
    }
}
