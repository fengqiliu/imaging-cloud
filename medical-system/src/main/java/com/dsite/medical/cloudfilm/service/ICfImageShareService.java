package com.dsite.medical.cloudfilm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dsite.medical.cloudfilm.domain.entity.CfImageShare;
import com.dsite.medical.common.core.domain.PageQuery;
import com.dsite.medical.common.core.domain.PageResult;

/**
 * 影像分享Service接口
 */
public interface ICfImageShareService extends IService<CfImageShare> {

    /**
     * 分页查询分享列表
     */
    PageResult<CfImageShare> selectSharePage(CfImageShare cfImageShare, PageQuery pageQuery);

    /**
     * 创建分享
     */
    CfImageShare createShare(String examId, int expireDays, boolean allowDownload, String createUser);

    /**
     * 根据分享编号获取分享信息
     */
    CfImageShare getByShareNo(String shareNo);

    /**
     * 验证提取码
     */
    boolean verifyAccessCode(String shareNo, String accessCode);

    /**
     * 增加查看次数
     */
    void incrementViewCount(String shareId);

    /**
     * 增加下载次数
     */
    void incrementDownloadCount(String shareId);

    /**
     * 取消分享
     */
    int cancelShare(String shareId);

    /**
     * 检查分享是否过期
     */
    boolean isExpired(String shareNo);
}
