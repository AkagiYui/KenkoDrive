package com.akagiyui.drive.controller

import com.akagiyui.drive.component.CurrentUser
import com.akagiyui.drive.component.captcha.GeetestCaptchaV4Protected
import com.akagiyui.drive.component.ip.ClientIp
import com.akagiyui.drive.component.limiter.Limit
import com.akagiyui.drive.entity.User
import com.akagiyui.drive.model.request.*
import com.akagiyui.drive.model.request.auth.*
import com.akagiyui.drive.model.response.auth.ClaimedTemporaryTokenInfoResponse
import com.akagiyui.drive.model.response.auth.TemporaryLoginTokenStatusResponse
import com.akagiyui.drive.model.response.auth.TokenResponse
import com.akagiyui.drive.service.AuthService
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
class AuthController(
    private val userService: UserService,
    private val authService: AuthService,
) {

    /**
     * 使用密码获取Token
     * @param request 请求体
     */
    @PostMapping("/token", "/token/password")
    @PreAuthorize("isAnonymous()")
    fun getTokenByPassword(@RequestBody @Validated request: GetTokenByPasswordRequest): TokenResponse {
        val accessToken = authService.getAccessToken(request.username, request.password)
        return TokenResponse(accessToken, null)
    }

    /**
     * 使用短信验证码获取Token
     * @param request 请求体
     */
    @PostMapping("/token/sms")
    @PreAuthorize("isAnonymous()")
    @Limit(key = "getTokenBySms", permitsPerSecond = 1, timeout = 1, timeunit = TimeUnit.SECONDS)
    fun getTokenBySms(@RequestBody @Validated request: GetTokenByPhoneRequest): TokenResponse {
        val accessToken = authService.getAccessTokenBySms(request.phone, request.otp)
        return TokenResponse(accessToken, null)
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
        authService.sendSmsOneTimePassword(request.phone)
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

    /**
     * 申请临时token，用于二维码登录
     *
     */
    @PostMapping("/token/temporary")
    @PreAuthorize("isAnonymous()")
    fun requestTemporaryToken(): String {
        return authService.generateTemporaryLoginToken()
    }

    /**
     * 使用临时token获取Token状态
     * @param token 临时token
     */
    @GetMapping("/token/temporary/{token}")
    @PreAuthorize("isAnonymous()")
    fun getTokenStatusByTemporaryToken(@PathVariable token: String): TemporaryLoginTokenStatusResponse {
        val info = authService.getTemporaryLoginTokenStatus(token)
        val response = TemporaryLoginTokenStatusResponse(info)
        if (info.userId != null) {
            response.nickname = userService.getUserById(info.userId!!).nickname
        }
        if (!info.canceled && info.confirmed && info.userId != null) {
            val accessToken = authService.getAccessToken(info.userId!!)
            response.token = TokenResponse(accessToken, null)
        }
        return response
    }

    /**
     * 认领临时token
     * @param token 临时token
     * @param user 用户
     * @param ip IP
     */
    @PostMapping("/token/temporary/{token}")
    fun claimTemporaryToken(
        @PathVariable token: String,
        @CurrentUser user: User,
        @ClientIp ip: String,
    ): ClaimedTemporaryTokenInfoResponse {
        return authService.claimTemporaryLoginToken(token, user, ip)
    }

    /**
     * 确认/取消 临时token登录
     * @param temporaryToken 临时token
     * @param takenToken 认领token
     * @param action 行为
     * @param user 用户
     */
    @PostMapping("/token/temporary/{token}/{action:confirm|cancel}")
    fun confirmOrCancelTemporaryToken(
        @PathVariable("token") temporaryToken: String,
        @RequestParam("token") takenToken: String,
        @PathVariable("action") action: String,
        @CurrentUser user: User,
    ) {
        val func = when (action) {
            "confirm" -> authService::confirmTemporaryLoginToken
            "cancel" -> authService::cancelTemporaryLoginToken
            else -> throw IllegalArgumentException("Invalid action")
        }
        func(temporaryToken, takenToken, user)
    }
}
