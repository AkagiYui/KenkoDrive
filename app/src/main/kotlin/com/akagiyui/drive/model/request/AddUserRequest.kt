package com.akagiyui.drive.model.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * 添加用户 请求
 *
 * @author AkagiYui
 */
class AddUserRequest {

    /**
     * 用户名
     */
    @Size(min = 5, max = 64, message = "{username length must be more than 5}")
    var username: String? = null

    /**
     * 密码
     */
    @NotBlank(message = "{password cannot be empty}")
    @Size(min = 5, max = 64, message = "{password length must be more than 8}")
    var password: String? = null

    /**
     * 昵称
     */
    var nickname: String? = null

    /**
     * 邮箱
     */
    @Email(message = "{email format is incorrect}")
    var email: String? = null

    /**
     * 手机号
     */
    var phone: String? = null
}
