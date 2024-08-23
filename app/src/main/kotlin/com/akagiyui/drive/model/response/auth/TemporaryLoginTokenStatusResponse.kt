package com.akagiyui.drive.model.response.auth

import com.akagiyui.drive.model.cache.TemporaryLoginInfo

/**
 * 临时登录Token状态响应
 * @author AkagiYui
 */
data class TemporaryLoginTokenStatusResponse(
    /**
     * 是否已被认领
     */
    var taken: Boolean = false,

    /**
     * 是否确认登录
     */
    var confirmed: Boolean = false,

    /**
     * 是否已被取消
     */
    var canceled: Boolean = false,

    /**
     * 用户ID
     */
    var userId: String? = null,

    /**
     * 用户昵称
     */
    var nickname: String? = null,

    /**
     * Token
     */
    var token: TokenResponse? = null,
) {

    /**
     * 临时登录Token状态响应
     * @param temporaryLoginInfo 临时登录信息
     */
    constructor(temporaryLoginInfo: TemporaryLoginInfo) : this(
        temporaryLoginInfo.taken,
        temporaryLoginInfo.confirmed,
        temporaryLoginInfo.canceled,
        temporaryLoginInfo.userId
    )
}
