package com.dsite.medical.common.core.domain;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private long total;

    /** 列表数据 */
    private List<T> rows;

    /** 当前页码 */
    private long pageNum;

    /** 每页大小 */
    private long pageSize;

    /** 总页数 */
    private long pages;

    public PageResult() {
    }

    public PageResult(List<T> list, long total) {
        this.rows = list;
        this.total = total;
    }

    public PageResult(Page<T> page) {
        this.rows = page.getRecords();
        this.total = page.getTotal();
        this.pageNum = page.getCurrent();
        this.pageSize = page.getSize();
        this.pages = page.getPages();
    }

    public static <T> PageResult<T> of(List<T> list, long total) {
        return new PageResult<>(list, total);
    }

    public static <T> PageResult<T> of(Page<T> page) {
        return new PageResult<>(page);
    }
}
