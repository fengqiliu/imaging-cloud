package com.dsite.medical.cloudfilm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dsite.medical.cloudfilm.domain.entity.CfPatient;
import com.dsite.medical.common.core.domain.PageQuery;
import com.dsite.medical.common.core.domain.PageResult;

/**
 * 患者Service接口
 */
public interface ICfPatientService extends IService<CfPatient> {

    /**
     * 分页查询患者列表
     */
    PageResult<CfPatient> selectPatientPage(CfPatient cfPatient, PageQuery pageQuery);

    /**
     * 新增患者
     */
    int insertPatient(CfPatient cfPatient);

    /**
     * 修改患者
     */
    int updatePatient(CfPatient cfPatient);

    /**
     * 删除患者
     */
    int deletePatientByIds(String[] patientIds);
}
