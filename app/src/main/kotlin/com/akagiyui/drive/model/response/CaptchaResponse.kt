package com.akagiyui.drive.model.response

/**
 * 验证码 响应
 * @author AkagiYui
 */

data class CaptchaResponse(
    /**
     * 验证码ID
     */
    val id: String,
    /**
     * 验证码图片(Base64)
     */
    val image: String,
)
