package com.akagiyui.drive.model.request.auth

import jakarta.validation.constraints.NotNull

/**
 * 通过手机号获取Token 请求体
 * @author AkagiYui
 */

class GetTokenByPhoneRequest {
    /**
     * 手机号
     */
    @NotNull
    lateinit var phone: String

    /**
     * 一次性密码
     */
    @NotNull
    lateinit var otp: String
}
