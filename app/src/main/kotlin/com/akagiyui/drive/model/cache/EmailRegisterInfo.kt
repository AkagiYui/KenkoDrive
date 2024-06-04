package com.akagiyui.drive.model.cache

/**
 * 邮箱注册信息
 * @author AkagiYui
 */
class EmailRegisterInfo() {
    lateinit var email: String
    lateinit var password: String
    lateinit var otp: String

    constructor(email: String, password: String, otp: String) : this() {
        this.email = email
        this.password = password
        this.otp = otp
    }
}
