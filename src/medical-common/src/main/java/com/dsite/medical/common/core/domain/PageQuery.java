package com.dsite.medical.common.core.domain;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询参数
 */
@Data
public class PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

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
    @SuppressWarnings("unchecked")
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
            OrderItem orderItem = new OrderItem();
            orderItem.setColumn(orderByColumn);
            orderItem.setAsc("asc".equalsIgnoreCase(isAsc));
            page.addOrder(orderItem);
        }
        return page;
    }
}
