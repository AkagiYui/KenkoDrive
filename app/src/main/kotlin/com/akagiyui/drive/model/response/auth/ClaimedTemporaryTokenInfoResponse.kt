package com.akagiyui.drive.model.response.auth

/**
 * 认领临时Token信息响应
 * @author AkagiYui
 */
data class ClaimedTemporaryTokenInfoResponse(
    /**
     * 认领Token
     */
    val takenToken: String,
    /**
     * 认领者昵称
     */
    val nickname: String,
    /**
     * 待登录者IP
     */
    val ipRegion: String,
)
