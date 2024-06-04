package com.akagiyui.drive.model.request.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

/**
 * 获取邮箱注册验证码 请求体
 *
 * @author AkagiYui
 */
class GetEmailOtpRegisterRequest {
    /**
     * 邮箱
     */
    @NotBlank(message = "{email cannot be empty}")
    @NotNull(message = "{email is missing}")
    @Email(message = "{email format is incorrect}")
    lateinit var email: String

    /**
     * 密码
     */
    @NotBlank(message = "{password cannot be empty}")
    @NotNull(message = "{password is missing}")
    @Size(min = 5, max = 64, message = "{password length must be more than 5}")
    lateinit var password: String
}
