package com.akagiyui.drive.controller

import com.akagiyui.drive.component.limiter.Limit
import com.akagiyui.drive.model.response.captcha.CaptchaResponse
import com.akagiyui.drive.service.CaptchaService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 验证码 接口
 * @author AkagiYui
 */
@RestController
@RequestMapping("/captcha")
class CaptchaController(private val captchaService: CaptchaService) {

    /**
     * 创建验证码
     * @return 验证码ID和验证码图片
     */
    @GetMapping("/", "")
    @PreAuthorize("permitAll()")
    @Limit(key = "captcha", permitsPerSecond = 10)
    fun createCaptcha(): CaptchaResponse {
        val captcha = captchaService.createCaptcha()
        return CaptchaResponse(captcha.first, captcha.second)
    }

    /**
     * 检查验证码
     * @param id 验证码ID
     * @param text 验证码文本
     * @return 是否正确
     */
    @GetMapping("/check")
    @PreAuthorize("permitAll()")
    @Limit(key = "captcha", permitsPerSecond = 10)
    fun checkCaptcha(id: String, text: String): Boolean {
        return captchaService.checkCaptcha(id, text)
    }
}
