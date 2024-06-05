package com.akagiyui.drive.model.response.file

import com.akagiyui.drive.entity.User

/**
 * 文件所有者 响应
 * @author AkagiYui
 */

class FileOwnerResponse(user: User) {
    /**
     * 用户名
     */
    val username: String = user.username

    /**
     * 昵称
     */
    val nickname: String = user.nickname
}

/**
 * 从用户列表转换
 * @return 文件所有者响应列表
 */
fun List<User>.toFileOwnerResponse(): List<FileOwnerResponse> {
    return this.map { FileOwnerResponse(it) }
}
