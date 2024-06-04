package com.akagiyui.drive.model.request.user

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

/**
 * 修改用户信息 请求
 *
 * @author AkagiYui
 */
class UpdateUserInfoRequest {
    /**
     * 昵称
     */
    @Size(max = 20, message = "{nickname length must be less than 20}")
    var nickname: String? = null

    /**
     * 邮箱
     */
    @Email(message = "{email format is incorrect}")
    val email: String? = null

    /**
     * 密码
     */
    @Size(min = 5, max = 64, message = "{password length must be more than 5}")
    var password: String? = null
}
