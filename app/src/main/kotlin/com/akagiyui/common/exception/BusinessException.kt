package com.akagiyui.common.exception

import com.akagiyui.common.ResponseEnum


/**
 * 业务异常
 *
 * @author AkagiYui
 */
class BusinessException(
    /**
     * 响应状态枚举
     */
    private val status: ResponseEnum,
) : RuntimeException() {
    fun getStatus() = status
}
