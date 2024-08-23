package com.akagiyui.drive.model.cache

/**
 * 临时登录信息缓存
 * @author AkagiYui
 */
class TemporaryLoginInfo() {
    lateinit var token: String

    /**
     * 是否已被认领
     */
    var taken: Boolean = false

    /**
     * 认领Token
     */
    var takenToken: String? = null

    /**
     * 认领者昵称
     */
    var confirmed: Boolean = false

    /**
     * 是否已被取消
     */
    var canceled: Boolean = false

    /**
     * 用户ID
     */
    var userId: String? = null

    /**
     * 临时登录信息缓存
     * @param token 临时Token
     */
    constructor(token: String) : this() {
        this.token = token
    }
}
