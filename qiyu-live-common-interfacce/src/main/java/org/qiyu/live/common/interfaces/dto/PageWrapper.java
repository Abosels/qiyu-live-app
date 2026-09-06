package org.qiyu.live.common.interfaces.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页查询所用的包装类
 */
@Data
public class PageWrapper<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页数据
     */
    private List<T> list;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 当前页码
     */
    private long currentPage;

    /**
     * 每页数量
     */
    private long pageSize;

    /**
     * 是否存在下一页
     */
    private boolean hasNext;
}