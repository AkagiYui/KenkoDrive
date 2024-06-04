package com.akagiyui.drive.model

import com.akagiyui.drive.model.request.AddUserRequest

/**
 * 添加用户 参数模型
 * @author AkagiYui
 */

class AddUserModel {
    var username: String? = null
    var password: String? = null
    var nickname: String? = null
    var email: String? = null
    var phone: String? = null
}

fun AddUserRequest.toModel(): AddUserModel {
    val model = AddUserModel()
    model.username = this.username
    model.password = this.password
    model.nickname = this.nickname
    model.email = this.email
    model.phone = this.phone
    return model
}
