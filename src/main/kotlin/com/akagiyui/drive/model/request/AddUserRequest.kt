package com.akagiyui.drive.model.request

import com.akagiyui.drive.entity.User
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
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
    @NotBlank(message = "{username cannot be empty}")
    @NotNull(message = "{username is missing}")
    @Size(min = 3, max = 20, message = "{username length must be between 3 and 20}")
    lateinit var username: String

    /**
     * 密码
     */
    @NotBlank(message = "{password cannot be empty}")
    @NotNull(message = "{password is missing}")
    @Size(min = 5, max = 64, message = "{password length must be more than 8}")
    lateinit var password: String

    /**
     * 昵称
     */
    @Size(max = 20, message = "{nickname length must be less than 20}")
    lateinit var nickname: String

    /**
     * 邮箱
     */
    @Email(message = "{email format is incorrect}")
    var email: String? = null

    /**
     * 转换为用户实体
     */
    fun toUser(): User {
        return User().also {
            it.username = username
            it.password = password
            it.nickname = nickname
            it.email = email
        }
    }
}
