package com.dsite.medical.common.core.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页请求参数
 */
@Data
public class PageDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 当前页码 */
    private Integer pageNum = 1;

    /** 每页大小 */
    private Integer pageSize = 10;

    /** 排序字段 */
    private String orderByColumn;

    /** 排序方向 */
    private String isAsc = "asc";

    /** 是否分页 */
    private boolean paginate = true;
}
