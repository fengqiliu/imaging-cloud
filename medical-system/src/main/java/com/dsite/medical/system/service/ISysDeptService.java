package com.dsite.medical.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dsite.medical.system.domain.entity.SysDept;

import java.util.List;

/**
 * 部门Service接口
 */
public interface ISysDeptService extends IService<SysDept> {

    /**
     * 查询部门列表
     */
    List<SysDept> selectDeptList(SysDept sysDept);

    /**
     * 查询部门树列表
     */
    List<SysDept> selectDeptTreeList();

    /**
     * 根据ID查询所有子部门
     */
    List<SysDept> selectChildrenDeptById(Long deptId);

    /**
     * 根据ID查询部门
     */
    SysDept selectDeptById(Long deptId);

    /**
     * 新增部门
     */
    int insertDept(SysDept sysDept);

    /**
     * 修改部门
     */
    int updateDept(SysDept sysDept);

    /**
     * 删除部门
     */
    int deleteDeptById(Long deptId);

    /**
     * 校验部门名称是否唯一
     */
    boolean checkDeptNameUnique(SysDept sysDept);
}
