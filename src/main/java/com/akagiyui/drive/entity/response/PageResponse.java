package com.akagiyui.drive.entity.response;

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
    Integer page;

    /**
     * 每页大小
     */
    Integer size;

    /**
     * 页数
     */
    Integer pageCount;

    /**
     * 总数
     */
    Long total;

    /**
     * 内容
     */
    List<T> list;
}
