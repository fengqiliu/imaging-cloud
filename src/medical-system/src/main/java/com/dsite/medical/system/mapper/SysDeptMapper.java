package com.dsite.medical.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dsite.medical.system.domain.entity.SysDept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 部门Mapper接口
 */
@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {

    /**
     * 查询所有子部门
     */
    List<SysDept> selectChildrenDeptById(@Param("deptId") Long deptId);

    /**
     * 修改父部门祖先
     */
    int updateDeptAncestors(@Param("deptId") Long deptId, @Param("ancestors") String ancestors);
}
