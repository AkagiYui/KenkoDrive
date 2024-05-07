package com.akagiyui.drive.component.captcha

/**
 * 极验验证码保护 注解
 * @author AkagiYui
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class GeetestCaptchaV4Protected(
    val value: String = "", // 保留字段，用于限定验证码所属服务
)
