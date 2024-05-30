package com.akagiyui.drive.model.response

import com.akagiyui.drive.entity.Folder

/**
 * 文件夹信息 响应实体
 *
 * @author AkagiYui
 */
data class FolderResponse(
    /**
     * 文件夹ID
     */
    val id: String,

    /**
     * 文件夹名
     */
    val name: String,

    /**
     * 创建时间
     */
    val createTime: Long,
) {
    constructor(folder: Folder) : this(
        id = folder.id,
        name = folder.name,
        createTime = folder.createTime.time
    )
}


/**
 * 将文件夹列表转换为文件夹响应列表
 *
 * @return 文件夹响应 列表
 */
fun List<Folder>.toResponse(): List<FolderResponse> {
    return this.map { FolderResponse(it) }.toList()
}
