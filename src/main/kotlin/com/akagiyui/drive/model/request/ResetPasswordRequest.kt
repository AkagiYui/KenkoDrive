package com.akagiyui.drive.model.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

/**
 * 重置密码 请求体
 *
 * @author AkagiYui
 */
class ResetPasswordRequest {
    /**
     * 新密码
     * todo 补充校验信息
     */
    @NotNull
    @NotBlank
    lateinit var newPassword: String
}
