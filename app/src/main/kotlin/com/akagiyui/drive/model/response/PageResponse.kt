package com.akagiyui.drive.model.response

import org.springframework.data.domain.Page


/**
 * 分页响应基类
 * @author AkagiYui
 */
class PageResponse<T>() {

    constructor(page: Page<T>) : this() {
        this.page = page.number
        this.size = page.size
        this.pageCount = page.totalPages
        this.total = page.totalElements
        this.list = page.content
    }

    /**
     * 当前页
     */
    var page: Int = 0

    /**
     * 每页大小
     */
    var size: Int = 0

    /**
     * 页数
     */
    var pageCount: Int = 0

    /**
     * 总数
     */
    var total: Long = 0

    /**
     * 内容
     */
    var list: List<T>? = null
}
