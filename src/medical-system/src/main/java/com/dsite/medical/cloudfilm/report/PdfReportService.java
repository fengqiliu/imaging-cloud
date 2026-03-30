package com.dsite.medical.cloudfilm.report;

import com.dsite.medical.cloudfilm.domain.entity.CfDiagnosisReport;
import com.dsite.medical.cloudfilm.domain.entity.CfPatient;
import com.dsite.medical.cloudfilm.domain.entity.CfExamination;

/**
 * PDF报告生成服务
 * 使用iText生成诊断报告PDF
 */
public interface PdfReportService {

    /**
     * 生成诊断报告PDF
     *
     * @param report 诊断报告
     * @param patient 患者信息
     * @param examination 检查信息
     * @return PDF文件字节数组
     */
    byte[] generateReportPdf(CfDiagnosisReport report, CfPatient patient, CfExamination examination);

    /**
     * 生成诊断报告PDF并保存
     *
     * @param report 诊断报告
     * @param patient 患者信息
     * @param examination 检查信息
     * @return 保存的文件路径
     */
    String generateAndSavePdf(CfDiagnosisReport report, CfPatient patient, CfExamination examination);
}
