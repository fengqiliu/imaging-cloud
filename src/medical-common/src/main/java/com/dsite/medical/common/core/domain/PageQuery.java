package com.dsite.medical.common.core.domain;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * 分页查询参数
 */
@Data
public class PageQuery {

    /** 当前页码 */
    private Integer pageNum = 1;

    /** 每页大小 */
    private Integer pageSize = 10;

    /** 排序字段 */
    private String orderByColumn;

    /** 排序方向 */
    private String isAsc = "asc";

    /**
     * 构建MyBatis Plus分页对象
     */
    public <T> Page<T> build() {
        Integer pageNum = this.pageNum;
        Integer pageSize = this.pageSize;
        if (pageNum != null && pageNum <= 0) {
            pageNum = 1;
        }
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;

        Page<T> page = new Page<>(pageNum, pageSize);
        if (orderByColumn != null && !orderByColumn.isEmpty()) {
            String orderBy = orderByColumn + " " + isAsc;
            page.setOrders(com.baomidou.mybatisplus.core.toolkit.StringUtils.getOrders(orderBy));
        }
        return page;
    }
}
