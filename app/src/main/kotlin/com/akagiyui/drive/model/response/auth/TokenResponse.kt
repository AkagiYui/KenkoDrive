package com.akagiyui.drive.model.response.auth

/**
 * 登录响应
 * @author AkagiYui
 */
data class TokenResponse(
    /**
     * token
     */
    val token: String,
    /**
     * 刷新token
     */
    val refreshToken: String? = null, // TODO 刷新token
)
