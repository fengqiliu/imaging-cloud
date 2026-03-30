package com.dsite.medical.cloudfilm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dsite.medical.cloudfilm.domain.entity.CfImageShare;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 影像分享Mapper接口
 */
@Mapper
public interface CfImageShareMapper extends BaseMapper<CfImageShare> {

    /**
     * 根据分享编号查询
     */
    CfImageShare selectByShareNo(@Param("shareNo") String shareNo);

    /**
     * 增加查看次数
     */
    int incrementViewCount(@Param("shareId") String shareId);

    /**
     * 增加下载次数
     */
    int incrementDownloadCount(@Param("shareId") String shareId);
}
