package com.dsite.medical.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dsite.medical.system.domain.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户
     */
    SysUser selectUserByUserName(@Param("userName") String userName);

    /**
     * 查询用户角色列表
     */
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 新增用户角色关联
     */
    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 删除用户角色关联
     */
    int deleteUserRole(@Param("userId") Long userId);
}
