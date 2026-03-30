package com.dsite.medical.cloudfilm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dsite.medical.cloudfilm.domain.entity.CfDiagnosisReport;
import com.dsite.medical.common.core.domain.PageQuery;
import com.dsite.medical.common.core.domain.PageResult;

import java.util.List;

/**
 * 诊断报告Service接口
 */
public interface ICfDiagnosisReportService extends IService<CfDiagnosisReport> {

    /**
     * 分页查询报告列表
     */
    PageResult<CfDiagnosisReport> selectReportPage(CfDiagnosisReport cfDiagnosisReport, PageQuery pageQuery);

    /**
     * 新增报告
     */
    int insertReport(CfDiagnosisReport cfDiagnosisReport);

    /**
     * 修改报告
     */
    int updateReport(CfDiagnosisReport cfDiagnosisReport);

    /**
     * 删除报告
     */
    int deleteReportByIds(String[] reportIds);

    /**
     * 根据检查ID查询报告
     */
    CfDiagnosisReport selectReportByExamId(String examId);

    /**
     * 审核报告
     */
    int reviewReport(String reportId, String status, String reviewDoctor);
}
