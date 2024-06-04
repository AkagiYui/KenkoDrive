package com.akagiyui.drive.controller

import com.akagiyui.drive.component.captcha.GeetestCaptchaV4Protected
import com.akagiyui.drive.component.limiter.Limit
import com.akagiyui.drive.model.request.*
import com.akagiyui.drive.model.request.auth.*
import com.akagiyui.drive.model.response.LoginResponse
import com.akagiyui.drive.service.UserService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.concurrent.TimeUnit

/**
 * 认证 控制器
 * todo RSA 加解密
 * @author AkagiYui
 */
@RestController
@RequestMapping("/auth")
class AuthController(private val userService: UserService) {

    /**
     * 使用密码获取Token
     * @param request 请求体
     */
    @PostMapping("/token", "/token/password")
    @PreAuthorize("isAnonymous()")
    fun getTokenByPassword(@RequestBody @Validated request: GetTokenByPasswordRequest): LoginResponse {
        val accessToken = userService.getAccessToken(request.username, request.password)
        return LoginResponse(accessToken, null)
    }

    @PostMapping("/token/sms")
    @PreAuthorize("isAnonymous()")
    @Limit(key = "getTokenBySms", permitsPerSecond = 1, timeout = 1, timeunit = TimeUnit.SECONDS)
    fun getTokenBySms(@RequestBody @Validated request: GetTokenByPhoneRequest): LoginResponse {
        val accessToken = userService.getAccessTokenBySms(request.phone, request.otp)
        return LoginResponse(accessToken, null)
    }

    /**
     * 获取邮件验证码
     *
     * @param request 预注册请求体
     */
    @PostMapping("/otp/email")
    @PreAuthorize("isAnonymous()")
    @GeetestCaptchaV4Protected
    @Limit(key = "getEmailRegisterOtp", permitsPerSecond = 1, timeout = 1, timeunit = TimeUnit.SECONDS)
    fun getEmailRegisterOtp(@RequestBody @Validated request: GetEmailOtpRegisterRequest) {
        userService.registerByEmail(request.email, request.password)
    }

    /**
     * 获取短信验证码
     *
     * @param phone 手机号
     */
    @PostMapping("/otp/sms")
    @PreAuthorize("isAnonymous()")
    @GeetestCaptchaV4Protected
    @Limit(key = "getSmsOtp", permitsPerSecond = 1, timeout = 1, timeunit = TimeUnit.SECONDS)
    fun getSmsOtp(@RequestBody @Validated request: GetOtpBySmsRequest) {
        // 校验手机号格式
        val regex = Regex("^1[3-9]\\d{9}$")
        if (!regex.matches(request.phone)) {
            // 直接返回，不让前端知道，避免恶意攻击
            return
        }
        userService.sendSmsOneTimePassword(request.phone)
    }

    /**
     * 确认邮箱注册
     *
     * @param request 注册请求体
     */
    @PostMapping("/register/email")
    @PreAuthorize("isAnonymous()")
    fun confirmEmailRegister(@RequestBody @Validated request: EmailRegisterConfirmRequest) {
        userService.confirmRegister(request.email, request.otp)
    }

}
