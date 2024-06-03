package com.akagiyui.drive.controller

import com.akagiyui.drive.component.captcha.GeetestCaptchaV4Protected
import com.akagiyui.drive.component.limiter.Limit
import com.akagiyui.drive.model.request.EmailVerifyCodeRequest
import com.akagiyui.drive.model.request.RegisterConfirmRequest
import com.akagiyui.drive.model.response.LoginResponse
import com.akagiyui.drive.service.UserService
import jakarta.validation.constraints.NotNull
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.concurrent.TimeUnit

/**
 * 认证 控制器
 * @author AkagiYui
 */
@RestController
@RequestMapping("/auth")
class AuthController(private val userService: UserService) {

    /**
     * 获取Token
     * @param username 用户名/邮箱
     * @param password 密码
     */
    @PostMapping("/token")
    @PreAuthorize("isAnonymous()")
    fun getToken(@NotNull username: String, @NotNull password: String): LoginResponse {
        return LoginResponse(userService.getAccessToken(username, password), null)
    }

    /**
     * 获取邮件验证码
     *
     * @param verifyRequest 预注册请求体
     */
    @PostMapping("/register/email")
    @PreAuthorize("isAnonymous()")
    @GeetestCaptchaV4Protected
    @Limit(key = "getVerifyCode", permitsPerSecond = 1, timeout = 1, timeunit = TimeUnit.SECONDS)
    fun getEmailVerifyCode(@RequestBody @Validated verifyRequest: EmailVerifyCodeRequest) {
        userService.sendEmailVerifyCode(verifyRequest)
    }

    /**
     * 获取短信验证码
     *
     * @param phone 手机号
     */
    @PostMapping("/sms")
    @PreAuthorize("isAnonymous()")
    @GeetestCaptchaV4Protected
    @Limit(key = "getVerifyCode", permitsPerSecond = 1, timeout = 1, timeunit = TimeUnit.SECONDS)
    fun getSmsOneTimePassword(@RequestParam("phone") phone: String) {
        // 校验手机号格式
        val regex = Regex("^1[3-9]\\d{9}$")
        if (!regex.matches(phone)) {
            // 直接返回，不让前端知道，避免恶意攻击
            return
        }
        userService.sendSmsOneTimePassword(phone)
    }

    @GetMapping("/token/sms")
    @PreAuthorize("isAnonymous()")
    @Limit(key = "smsLogin", permitsPerSecond = 1, timeout = 1, timeunit = TimeUnit.SECONDS)
    fun getSmsToken(@RequestParam("phone") phone: String, @RequestParam("code") code: String): LoginResponse {
        return LoginResponse(userService.getAccessTokenBySms(phone, code), null)
    }

    /**
     * 确认注册
     *
     * @param registerConfirmRequest 注册请求体
     */
    @PostMapping("/register")
    @PreAuthorize("isAnonymous()")
    fun confirmRegister(@RequestBody @Validated registerConfirmRequest: RegisterConfirmRequest) {
        userService.confirmRegister(registerConfirmRequest)
    }

}
