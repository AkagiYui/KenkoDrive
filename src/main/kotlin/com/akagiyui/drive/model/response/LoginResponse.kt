package com.akagiyui.drive.model.response

/**
 * 登录响应
 * @author AkagiYui
 */
data class LoginResponse(
    /**
     * token
     */
    val token: String,
    /**
     * 刷新token
     */
    val refreshToken: String? = null, // TODO 刷新token
)
