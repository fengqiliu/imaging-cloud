package com.dsite.medical.cloudfilm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dsite.medical.cloudfilm.domain.entity.CfExamination;
import com.dsite.medical.cloudfilm.mapper.CfExaminationMapper;
import com.dsite.medical.cloudfilm.service.ICfExaminationService;
import com.dsite.medical.common.core.domain.PageQuery;
import com.dsite.medical.common.core.domain.PageResult;
import com.dsite.medical.common.utils.IdUtils;
import com.dsite.medical.common.utils.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 检查单Service实现
 */
@Service
public class CfExaminationServiceImpl extends ServiceImpl<CfExaminationMapper, CfExamination> implements ICfExaminationService {

    @Override
    public PageResult<CfExamination> selectExaminationPage(CfExamination cfExamination, PageQuery pageQuery) {
        LambdaQueryWrapper<CfExamination> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotEmpty(cfExamination.getPatientId())) {
            lqw.eq(CfExamination::getPatientId, cfExamination.getPatientId());
        }
        if (StringUtils.isNotEmpty(cfExamination.getPatientName())) {
            lqw.like(CfExamination::getPatientName, cfExamination.getPatientName());
        }
        if (StringUtils.isNotEmpty(cfExamination.getExamType())) {
            lqw.eq(CfExamination::getExamType, cfExamination.getExamType());
        }
        if (StringUtils.isNotEmpty(cfExamination.getExamStatus())) {
            lqw.eq(CfExamination::getExamStatus, cfExamination.getExamStatus());
        }
        if (StringUtils.isNotEmpty(cfExamination.getApplyDoctor())) {
            lqw.like(CfExamination::getApplyDoctor, cfExamination.getApplyDoctor());
        }
        lqw.orderByDesc(CfExamination::getCreateTime);
        Page<CfExamination> page = page(pageQuery.build(), lqw);
        return PageResult.of(page.getRecords(), page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertExamination(CfExamination cfExamination) {
        // 生成检查单ID
        if (StringUtils.isEmpty(cfExamination.getExamId())) {
            cfExamination.setExamId(IdUtils.examId());
        }
        // 设置默认状态
        if (StringUtils.isEmpty(cfExamination.getExamStatus())) {
            cfExamination.setExamStatus("pending");
        }
        // 设置检查日期
        if (cfExamination.getExamDate() == null) {
            cfExamination.setExamDate(new Date());
        }
        return baseMapper.insert(cfExamination);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateExamination(CfExamination cfExamination) {
        return baseMapper.updateById(cfExamination);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteExaminationByIds(String[] examIds) {
        return baseMapper.deleteBatchIds(Arrays.asList(examIds));
    }

    @Override
    public List<CfExamination> selectExamListByPatientId(String patientId) {
        return baseMapper.selectExamListByPatientId(patientId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateExamStatus(String examId, String examStatus) {
        CfExamination examination = new CfExamination();
        examination.setExamId(examId);
        examination.setExamStatus(examStatus);
        // 如果状态变为已出报告，设置报告日期
        if ("reported".equals(examStatus)) {
            examination.setReportDate(new Date());
        }
        return baseMapper.updateById(examination);
    }
}
