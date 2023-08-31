package com.akagiyui.drive.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 分页响应基类
 * @author AkagiYui
 */
@Data
@Accessors(chain = true)
public class PageResponse<T> {
    /**
     * 当前页
     */
    int page;

    /**
     * 每页大小
     */
    int size;

    /**
     * 页数
     */
    int pageCount;

    /**
     * 总数
     */
    long total;

    /**
     * 内容
     */
    List<T> list;
}
