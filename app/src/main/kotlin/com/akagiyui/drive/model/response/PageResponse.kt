package com.akagiyui.drive.model.response

/**
 * 分页响应基类
 * @author AkagiYui
 */
class PageResponse<T> {
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
