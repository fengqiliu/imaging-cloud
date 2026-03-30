package com.dsite.medical.cloudfilm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dsite.medical.cloudfilm.domain.entity.CfDiagnosisReport;
import com.dsite.medical.cloudfilm.mapper.CfDiagnosisReportMapper;
import com.dsite.medical.cloudfilm.service.ICfDiagnosisReportService;
import com.dsite.medical.cloudfilm.service.ICfExaminationService;
import com.dsite.medical.common.core.domain.PageQuery;
import com.dsite.medical.common.core.domain.PageResult;
import com.dsite.medical.common.utils.DateUtils;
import com.dsite.medical.common.utils.IdUtils;
import com.dsite.medical.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;

/**
 * 诊断报告Service实现
 */
@Service
public class CfDiagnosisReportServiceImpl extends ServiceImpl<CfDiagnosisReportMapper, CfDiagnosisReport>
        implements ICfDiagnosisReportService {

    @Autowired
    private ICfExaminationService examinationService;

    @Override
    public PageResult<CfDiagnosisReport> selectReportPage(CfDiagnosisReport cfDiagnosisReport, PageQuery pageQuery) {
        LambdaQueryWrapper<CfDiagnosisReport> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotEmpty(cfDiagnosisReport.getPatientId())) {
            lqw.eq(CfDiagnosisReport::getPatientId, cfDiagnosisReport.getPatientId());
        }
        if (StringUtils.isNotEmpty(cfDiagnosisReport.getExamId())) {
            lqw.eq(CfDiagnosisReport::getExamId, cfDiagnosisReport.getExamId());
        }
        if (StringUtils.isNotEmpty(cfDiagnosisReport.getReportStatus())) {
            lqw.eq(CfDiagnosisReport::getReportStatus, cfDiagnosisReport.getReportStatus());
        }
        if (StringUtils.isNotEmpty(cfDiagnosisReport.getCreateDoctor())) {
            lqw.like(CfDiagnosisReport::getCreateDoctor, cfDiagnosisReport.getCreateDoctor());
        }
        lqw.orderByDesc(CfDiagnosisReport::getCreateTime);
        Page<CfDiagnosisReport> page = page(pageQuery.build(), lqw);
        return PageResult.of(page.getRecords(), page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertReport(CfDiagnosisReport cfDiagnosisReport) {
        // 生成报告ID
        if (StringUtils.isEmpty(cfDiagnosisReport.getReportId())) {
            cfDiagnosisReport.setReportId(IdUtils.reportId());
        }
        // 设置默认状态
        if (StringUtils.isEmpty(cfDiagnosisReport.getReportStatus())) {
            cfDiagnosisReport.setReportStatus("draft");
        }
        // 保存报告
        int rows = baseMapper.insert(cfDiagnosisReport);

        // 更新检查状态为已出报告
        if (cfDiagnosisReport.getExamId() != null) {
            examinationService.updateExamStatus(cfDiagnosisReport.getExamId(), "reported");
        }

        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateReport(CfDiagnosisReport cfDiagnosisReport) {
        return baseMapper.updateById(cfDiagnosisReport);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteReportByIds(String[] reportIds) {
        return baseMapper.deleteBatchIds(Arrays.asList(reportIds));
    }

    @Override
    public CfDiagnosisReport selectReportByExamId(String examId) {
        return baseMapper.selectReportByExamId(examId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int reviewReport(String reportId, String status, String reviewDoctor) {
        CfDiagnosisReport report = new CfDiagnosisReport();
        report.setReportId(reportId);
        report.setReportStatus(status);
        report.setReviewDoctor(reviewDoctor);
        report.setReviewTime(new Date());
        return baseMapper.updateById(report);
    }
}
