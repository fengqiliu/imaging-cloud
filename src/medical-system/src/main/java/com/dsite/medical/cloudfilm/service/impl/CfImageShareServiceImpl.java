package com.dsite.medical.cloudfilm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dsite.medical.cloudfilm.domain.entity.CfImageShare;
import com.dsite.medical.cloudfilm.mapper.CfImageShareMapper;
import com.dsite.medical.cloudfilm.service.ICfImageShareService;
import com.dsite.medical.common.core.domain.PageQuery;
import com.dsite.medical.common.core.domain.PageResult;
import com.dsite.medical.common.exception.ServiceException;
import com.dsite.medical.common.utils.DateUtils;
import com.dsite.medical.common.utils.IdUtils;
import com.dsite.medical.common.utils.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 影像分享Service实现
 */
@Service
public class CfImageShareServiceImpl extends ServiceImpl<CfImageShareMapper, CfImageShare>
        implements ICfImageShareService {

    @Override
    public PageResult<CfImageShare> selectSharePage(CfImageShare cfImageShare, PageQuery pageQuery) {
        LambdaQueryWrapper<CfImageShare> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotEmpty(cfImageShare.getExamId())) {
            lqw.eq(CfImageShare::getExamId, cfImageShare.getExamId());
        }
        if (StringUtils.isNotEmpty(cfImageShare.getCreateUser())) {
            lqw.like(CfImageShare::getCreateUser, cfImageShare.getCreateUser());
        }
        lqw.ge(CfImageShare::getExpireDate, new Date()); // 未过期的分享
        lqw.orderByDesc(CfImageShare::getCreateTime);
        Page<CfImageShare> page = page(pageQuery.build(), lqw);
        return PageResult.of(page.getRecords(), page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CfImageShare createShare(String examId, int expireDays, boolean allowDownload, String createUser) {
        // 生成分享信息
        CfImageShare share = new CfImageShare();
        share.setShareId(IdUtils.shareId());
        share.setShareNo(IdUtils.shareNo());
        share.setExamId(examId);
        share.setAccessCode(IdUtils.accessCode());
        share.setExpireDate(DateUtils.addDays(new Date(), expireDays));
        share.setViewCount(0);
        share.setDownloadCount(0);
        share.setAllowDownload(allowDownload ? "1" : "0");
        share.setCreateUser(createUser);

        baseMapper.insert(share);
        return share;
    }

    @Override
    public CfImageShare getByShareNo(String shareNo) {
        return baseMapper.selectByShareNo(shareNo);
    }

    @Override
    public boolean verifyAccessCode(String shareNo, String accessCode) {
        CfImageShare share = getByShareNo(shareNo);
        if (share == null) {
            throw new ServiceException("分享不存在");
        }
        // 检查是否过期
        if (new Date().after(share.getExpireDate())) {
            throw new ServiceException("分享链接已过期");
        }
        // 验证提取码
        return accessCode.equals(share.getAccessCode());
    }

    @Override
    public void incrementViewCount(String shareId) {
        baseMapper.incrementViewCount(shareId);
    }

    @Override
    public void incrementDownloadCount(String shareId) {
        baseMapper.incrementDownloadCount(shareId);
    }

    @Override
    public int cancelShare(String shareId) {
        return baseMapper.deleteById(shareId);
    }

    @Override
    public boolean isExpired(String shareNo) {
        CfImageShare share = getByShareNo(shareNo);
        if (share == null) {
            return true;
        }
        return new Date().after(share.getExpireDate());
    }
}
