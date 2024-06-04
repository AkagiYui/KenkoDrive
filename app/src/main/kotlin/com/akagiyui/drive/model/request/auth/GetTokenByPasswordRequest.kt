package com.akagiyui.drive.model.request.auth

import jakarta.validation.constraints.NotNull

/**
 * 通过密码获取Token 请求体
 * @author AkagiYui
 */

class GetTokenByPasswordRequest {
    /**
     * 用户名/邮箱/手机号
     */
    @NotNull
    lateinit var username: String

    /**
     * 密码
     */
    @NotNull
    lateinit var password: String
}
