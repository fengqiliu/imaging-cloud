package com.dsite.medical.system.service.impl;

import com.dsite.medical.framework.security.IUserDetailsService;
import com.dsite.medical.framework.web.domain.LoginUser;
import com.dsite.medical.system.domain.entity.SysRole;
import com.dsite.medical.system.service.ISysRoleService;
import com.dsite.medical.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户详情服务实现
 * 从数据库加载用户信息和角色
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements IUserDetailsService {

    private final ISysUserService userService;
    private final ISysRoleService roleService;

    @Override
    public LoginUser getUserInfo(String username) {
        // TODO: 实现从数据库加载完整用户信息
        // 暂时返回基础信息
        return null;
    }

    @Override
    public List<String> getUserRoles(Long userId) {
        // 根据用户ID查询角色列表
        List<SysRole> roles = roleService.selectRolesByUserId(userId);
        // 提取角色权限字符串
        return roles.stream()
                .map(SysRole::getRoleKey)
                .collect(Collectors.toList());
    }
}
