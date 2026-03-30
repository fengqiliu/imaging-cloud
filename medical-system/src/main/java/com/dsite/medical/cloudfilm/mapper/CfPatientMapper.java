package com.dsite.medical.cloudfilm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dsite.medical.cloudfilm.domain.entity.CfPatient;
import org.apache.ibatis.annotations.Mapper;

/**
 * 患者Mapper接口
 */
@Mapper
public interface CfPatientMapper extends BaseMapper<CfPatient> {
}
