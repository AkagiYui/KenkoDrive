package com.akagiyui.drive.model.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

/**
 * 注册确认 请求体
 * @author AkagiYui
 */
class RegisterConfirmRequest {
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
    @NotNull(message = "{code is missing}")
    @NotBlank(message = "{verifyCode cannot be empty}")
    lateinit var verifyCode: String
}
