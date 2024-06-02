package com.akagiyui.drive.model.response

import org.springframework.data.domain.Page


/**
 * 分页响应基类
 * @author AkagiYui
 */
data class PageResponse<T>(
    /**
     * 页码
     */
    val index: Int = 0,

    /**
     * 每页大小
     */
    val size: Int = 0,

    /**
     * 页数
     */
    val pageCount: Int = 0,

    /**
     * 总数
     */
    val itemCount: Long = 0,

    /**
     * 内容
     */
    val list: List<T> = emptyList(),
) {
    constructor(page: Page<T>) : this(
        index = page.number,
        size = page.size,
        pageCount = page.totalPages,
        itemCount = page.totalElements,
        list = page.content
    )

    constructor(metadata: Page<*>, list: List<T>) : this(
        index = metadata.number,
        size = metadata.size,
        pageCount = metadata.totalPages,
        itemCount = metadata.totalElements,
        list = list
    )
}
