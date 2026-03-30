package com.dsite.medical.cloudfilm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dsite.medical.cloudfilm.domain.entity.CfDiagnosisReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 诊断报告Mapper接口
 */
@Mapper
public interface CfDiagnosisReportMapper extends BaseMapper<CfDiagnosisReport> {

    /**
     * 根据检查ID查询报告
     */
    CfDiagnosisReport selectReportByExamId(@Param("examId") String examId);
}
