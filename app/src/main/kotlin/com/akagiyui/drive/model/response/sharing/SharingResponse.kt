package com.akagiyui.drive.model.response.sharing

import com.akagiyui.drive.entity.Sharing

/**
 * 分享信息响应
 * @author AkagiYui
 */

data class SharingResponse(
    val id: String,
    val filename: String,
    val createTime: Long,
    val password: String?,
) {

    constructor(sharing: Sharing) : this(
        id = sharing.id,
        filename = sharing.file.name,
        createTime = sharing.createTime.time,
        password = sharing.password
    )

}

/**
 * 从分享列表转换
 *
 * @return 分享信息响应列表
 */
fun List<Sharing>.toResponse(): List<SharingResponse> {
    return this.map { SharingResponse(it) }
}
