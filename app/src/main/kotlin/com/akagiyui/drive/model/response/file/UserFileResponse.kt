package com.akagiyui.drive.model.response.file

import com.akagiyui.drive.entity.UserFile

/**
 * 用户文件信息 响应实体
 *
 * @author AkagiYui
 */
data class UserFileResponse(
    /**
     * 文件ID
     */
    val id: String,

    /**
     * 文件名
     */
    val name: String,

    /**
     * 文件大小（字节Byte）
     */
    val size: Long,

    /**
     * 文件类型
     */
    val type: String,

    /**
     * 创建时间
     */
    val createTime: Long,

    /**
     * 文件已被锁定
     */
    val locked: Boolean,
) {
    constructor(userFile: UserFile) : this(
        id = userFile.id,
        name = userFile.name,
        size = userFile.fileInfo.size,
        type = userFile.fileInfo.type,
        createTime = userFile.createTime.time,
        locked = userFile.fileInfo.locked
    )
}


/**
 * 将文件列表转换为文件响应列表
 *
 * @return 文件响应 列表
 */
fun List<UserFile>.toResponse(): List<UserFileResponse> {
    return this.map { UserFileResponse(it) }.toList()
}
