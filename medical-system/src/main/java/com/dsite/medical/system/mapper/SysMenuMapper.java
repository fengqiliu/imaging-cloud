package com.dsite.medical.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dsite.medical.system.domain.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单Mapper接口
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 根据角色ID查询菜单
     */
    List<SysMenu> selectMenuListByRoleId(@Param("roleId") Long roleId);

    /**
     * 查询所有菜单
     */
    List<SysMenu> selectMenuTreeAll();

    /**
     * 根据用户ID查询菜单
     */
    List<SysMenu> selectMenuTreeByUserId(@Param("userId") Long userId);
}
