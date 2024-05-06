package com.akagiyui.drive.component.captcha

/**
 * 验证码保护 注解
 * @author AkagiYui
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CaptchaProtected(
    val value: String = "", // 保留字段，用于限定验证码所属服务
    val idParam: String = "captchaId",
    val textParam: String = "captchaText",
)
