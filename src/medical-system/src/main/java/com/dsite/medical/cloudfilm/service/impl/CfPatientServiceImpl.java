package com.dsite.medical.cloudfilm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dsite.medical.cloudfilm.domain.entity.CfPatient;
import com.dsite.medical.cloudfilm.mapper.CfPatientMapper;
import com.dsite.medical.cloudfilm.service.ICfPatientService;
import com.dsite.medical.common.core.domain.PageQuery;
import com.dsite.medical.common.core.domain.PageResult;
import com.dsite.medical.common.exception.ServiceException;
import com.dsite.medical.common.utils.IdUtils;
import com.dsite.medical.common.utils.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * 患者Service实现
 */
@Service
public class CfPatientServiceImpl extends ServiceImpl<CfPatientMapper, CfPatient> implements ICfPatientService {

    @Override
    public PageResult<CfPatient> selectPatientPage(CfPatient cfPatient, PageQuery pageQuery) {
        LambdaQueryWrapper<CfPatient> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotEmpty(cfPatient.getPatientName())) {
            lqw.like(CfPatient::getPatientName, cfPatient.getPatientName());
        }
        if (StringUtils.isNotEmpty(cfPatient.getIdCard())) {
            lqw.like(CfPatient::getIdCard, cfPatient.getIdCard());
        }
        if (StringUtils.isNotEmpty(cfPatient.getPhone())) {
            lqw.like(CfPatient::getPhone, cfPatient.getPhone());
        }
        if (StringUtils.isNotEmpty(cfPatient.getGender())) {
            lqw.eq(CfPatient::getGender, cfPatient.getGender());
        }
        lqw.orderByDesc(CfPatient::getCreateTime);
        Page<CfPatient> page = page(pageQuery.build(), lqw);
        return PageResult.of(page.getRecords(), page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPatient(CfPatient cfPatient) {
        // 生成患者ID
        if (StringUtils.isEmpty(cfPatient.getPatientId())) {
            cfPatient.setPatientId(IdUtils.patientId());
        }
        return baseMapper.insert(cfPatient);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePatient(CfPatient cfPatient) {
        return baseMapper.updateById(cfPatient);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePatientByIds(String[] patientIds) {
        return baseMapper.deleteBatchIds(Arrays.asList(patientIds));
    }
}
