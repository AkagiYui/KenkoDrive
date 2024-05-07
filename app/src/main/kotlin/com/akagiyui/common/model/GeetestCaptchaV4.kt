package com.akagiyui.common.model

/**
 * Geetest V4 验证响应
 * @author AkagiYui
 */
data class GeetestCaptchaV4ValidateResponse(
    /**
     * 验证码类型
     */
    val captchaType: CaptchaType,
    /**
     * 用户IP
     */
    val userIp: String,
    /**
     * 流水号
     */
    val lotNumber: String,
    /**
     * 业务场景
     */
    val scene: String,
    /**
     * 引用页
     */
    val referer: String,
    /**
     * IP类型
     */
    val ipType: Int,
    /**
     * 用户信息
     */
    val userInfo: String,
    /**
     * 客户端类型
     */
    val clientType: ClientType,
    /**
     * 用户代理
     */
    val userAgent: String,
    /**
     * 验证失败次数
     */
    val failCount: Int,
)

/**
 * 验证码类型
 */
enum class CaptchaType {
    /**
     * 滑块验证码
     */
    SLIDE
}

/**
 * 客户端类型
 */
enum class ClientType {
    WEB,
    IOS,
    ANDROID
}
