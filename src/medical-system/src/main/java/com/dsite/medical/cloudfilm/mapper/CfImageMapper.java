package com.dsite.medical.cloudfilm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dsite.medical.cloudfilm.domain.entity.CfImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 影像Mapper接口
 */
@Mapper
public interface CfImageMapper extends BaseMapper<CfImage> {

    /**
     * 查询检查关联影像
     */
    List<CfImage> selectImageListByExamId(@Param("examId") String examId);

    /**
     * 统计检查影像数量
     */
    int countByExamId(@Param("examId") String examId);
}
