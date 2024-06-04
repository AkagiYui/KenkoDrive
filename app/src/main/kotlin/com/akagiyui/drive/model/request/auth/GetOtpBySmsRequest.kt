package com.akagiyui.drive.model.request.auth

import jakarta.validation.constraints.NotNull

/**
 * 通过短信获取OTP 请求体
 * @author AkagiYui
 */

class GetOtpBySmsRequest {
    /**
     * 用户名/邮箱/手机号
     */
    @NotNull
    lateinit var phone: String
}
