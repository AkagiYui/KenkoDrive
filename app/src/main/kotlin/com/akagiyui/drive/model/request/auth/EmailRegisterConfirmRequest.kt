package com.akagiyui.drive.model.request.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

/**
 * 注册确认 请求体
 * @author AkagiYui
 */
class EmailRegisterConfirmRequest {
    /**
     * 邮箱
     */
    @Email(message = "{email format is incorrect}")
    @NotNull(message = "{email is missing}")
    @NotBlank(message = "{email cannot be empty}")
    lateinit var email: String

    /**
     * 验证码
     */
    @NotNull(message = "{otp is missing}")
    @NotBlank(message = "{otp cannot be empty}")
    lateinit var otp: String
}
