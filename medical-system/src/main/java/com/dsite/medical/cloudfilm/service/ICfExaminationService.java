package com.dsite.medical.cloudfilm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dsite.medical.cloudfilm.domain.entity.CfExamination;
import com.dsite.medical.common.core.domain.PageQuery;
import com.dsite.medical.common.core.domain.PageResult;

import java.util.List;

/**
 * 检查单Service接口
 */
public interface ICfExaminationService extends IService<CfExamination> {

    /**
     * 分页查询检查单列表
     */
    PageResult<CfExamination> selectExaminationPage(CfExamination cfExamination, PageQuery pageQuery);

    /**
     * 新增检查单
     */
    int insertExamination(CfExamination cfExamination);

    /**
     * 修改检查单
     */
    int updateExamination(CfExamination cfExamination);

    /**
     * 删除检查单
     */
    int deleteExaminationByIds(String[] examIds);

    /**
     * 查询患者检查列表
     */
    List<CfExamination> selectExamListByPatientId(String patientId);

    /**
     * 更新检查状态
     */
    int updateExamStatus(String examId, String examStatus);
}
