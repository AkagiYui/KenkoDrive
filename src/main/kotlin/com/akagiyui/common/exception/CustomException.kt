package com.akagiyui.common.exception

import com.akagiyui.common.ResponseEnum


/**
 * 自定义异常
 *
 * @author AkagiYui
 */
class CustomException(
    /**
     * 响应状态枚举
     */
    private val status: ResponseEnum
) : RuntimeException() {
    fun getStatus() = status
}
