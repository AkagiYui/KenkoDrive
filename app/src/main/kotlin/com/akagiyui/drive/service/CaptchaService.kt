package com.akagiyui.drive.service

/**
 * 验证码 服务接口
 * @author AkagiYui
 */

interface CaptchaService {
    /**
     * 创建验证码
     * @return 验证码ID和验证码图片
     */
    fun createCaptcha(): Pair<String, String>

    /**
     * 检查验证码
     * @param captchaId 验证码ID
     * @param text 验证码文本
     * @return 是否正确
     */
    fun checkCaptcha(captchaId: String, text: String): Boolean

    /**
     * 删除验证码
     * @param captchaId 验证码ID
     */
    fun deleteCaptcha(captchaId: String)
}
