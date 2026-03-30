package com.dsite.medical.cloudfilm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dsite.medical.cloudfilm.domain.entity.CfExamination;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 检查单Mapper接口
 */
@Mapper
public interface CfExaminationMapper extends BaseMapper<CfExamination> {

    /**
     * 查询患者检查列表
     */
    List<CfExamination> selectExamListByPatientId(@Param("patientId") String patientId);
}
