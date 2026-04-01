package com.dsite.medical.framework.security;

import com.dsite.medical.framework.web.domain.LoginUser;

import java.util.List;

/**
 * 用户详情服务接口
 * 用于从数据库加载用户信息及权限
 */
public interface IUserDetailsService {

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 登录用户信息
     */
    LoginUser getUserInfo(String username);

    /**
     * 根据用户ID获取角色列表
     *
     * @param userId 用户ID
     * @return 角色权限字符串列表
     */
    List<String> getUserRoles(Long userId);
}
