package com.dsite.medical.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dsite.medical.common.constant.Constants;
import com.dsite.medical.common.core.domain.TreeEntity;
import com.dsite.medical.common.exception.ServiceException;
import com.dsite.medical.common.utils.StringUtils;
import com.dsite.medical.system.domain.entity.SysDept;
import com.dsite.medical.system.mapper.SysDeptMapper;
import com.dsite.medical.system.service.ISysDeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门Service实现
 */
@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements ISysDeptService {

    @Autowired
    private SysDeptMapper deptMapper;

    @Override
    public List<SysDept> selectDeptList(SysDept sysDept) {
        LambdaQueryWrapper<SysDept> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotEmpty(sysDept.getDeptName())) {
            lqw.like(SysDept::getDeptName, sysDept.getDeptName());
        }
        if (StringUtils.isNotEmpty(sysDept.getStatus())) {
            lqw.eq(SysDept::getStatus, sysDept.getStatus());
        }
        lqw.orderByAsc(SysDept::getOrderNum);
        return list(lqw);
    }

    @Override
    public List<SysDept> selectDeptTreeList() {
        List<SysDept> deptList = selectDeptList(new SysDept());
        return buildDeptTree(deptList);
    }

    @Override
    public List<SysDept> selectChildrenDeptById(Long deptId) {
        return deptMapper.selectChildrenDeptById(deptId);
    }

    @Override
    public SysDept selectDeptById(Long deptId) {
        return baseMapper.selectById(deptId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDept(SysDept sysDept) {
        // 校验部门名称唯一性
        SysDept parentDept = baseMapper.selectById(sysDept.getParentId());
        if (parentDept == null) {
            throw new ServiceException("上级部门不存在");
        }
        if (!checkDeptNameUnique(sysDept)) {
            throw new ServiceException("部门名称已存在");
        }
        // 设置祖先
        sysDept.setAncestors(parentDept.getAncestors() + "," + sysDept.getParentId());
        // 设置状态
        if (StringUtils.isEmpty(sysDept.getStatus())) {
            sysDept.setStatus(Constants.STATUS_NORMAL);
        }
        return baseMapper.insert(sysDept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDept(SysDept sysDept) {
        SysDept parentDept = baseMapper.selectById(sysDept.getParentId());
        if (parentDept == null) {
            throw new ServiceException("上级部门不存在");
        }
        // 如果修改了父部门
        if (!sysDept.getParentId().equals(sysDept.getDeptId())) {
            SysDept oldDept = baseMapper.selectById(sysDept.getDeptId());
            // 修改祖先
            String newAncestors = parentDept.getAncestors() + "," + sysDept.getParentId();
            String oldAncestors = oldDept.getAncestors();
            // 更新子部门的祖先
            List<SysDept> children = deptMapper.selectChildrenDeptById(sysDept.getDeptId());
            for (SysDept child : children) {
                child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
            }
            baseMapper.updateDeptAncestors(sysDept.getDeptId(), newAncestors);
        }
        return baseMapper.updateById(sysDept);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDeptById(Long deptId) {
        // 校验是否有子部门
        LambdaQueryWrapper<SysDept> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysDept::getParentId, deptId);
        Long count = baseMapper.selectCount(lqw);
        if (count > 0) {
            throw new ServiceException("存在下级部门，不允许删除");
        }
        return baseMapper.deleteById(deptId);
    }

    @Override
    public boolean checkDeptNameUnique(SysDept sysDept) {
        LambdaQueryWrapper<SysDept> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysDept::getDeptName, sysDept.getDeptName());
        lqw.eq(SysDept::getParentId, sysDept.getParentId());
        if (sysDept.getDeptId() != null) {
            lqw.ne(SysDept::getDeptId, sysDept.getDeptId());
        }
        return baseMapper.selectCount(lqw) == 0;
    }

    /**
     * 构建部门树
     */
    private List<SysDept> buildDeptTree(List<SysDept> depts) {
        List<SysDept> returnList = new ArrayList<>();
        List<Long> tempList = depts.stream().map(SysDept::getDeptId).collect(Collectors.toList());
        for (SysDept dept : depts) {
            // 不是子节点
            if (!tempList.contains(dept.getParentId())) {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }
        if (returnList.isEmpty()) {
            returnList = depts;
        }
        return returnList;
    }

    /**
     * 递归
     */
    private void recursionFn(List<SysDept> list, SysDept t) {
        // 得到子节点列表
        List<SysDept> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysDept tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysDept> getChildList(List<SysDept> list, SysDept t) {
        List<SysDept> tlist = new ArrayList<>();
        Iterator<SysDept> it = list.iterator();
        while (it.hasNext()) {
            SysDept n = it.next();
            if (n.getParentId() != null && n.getParentId().longValue() == t.getDeptId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysDept> list, SysDept t) {
        return getChildList(list, t).size() > 0;
    }
}
