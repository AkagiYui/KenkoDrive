package com.akagiyui.drive.model.response.file

import com.akagiyui.drive.entity.FileInfo

/**
 * 文件信息 响应实体
 *
 * @author AkagiYui
 */
data class FileInfoResponse(
    /**
     * 文件ID
     */
    val id: String,

    /**
     * 初次上传文件名
     */
    val name: String,

    /**
     * 创建时间
     */
    val createTime: Long,

    /**
     * 文件大小
     */
    val size: Long,

    /**
     * 文件类型
     */
    val type: String,

    /**
     * 下载次数
     */
    val downloadCount: Long,

    /**
     * 是否被锁定
     */
    val locked: Boolean,
) {
    constructor(file: FileInfo) : this(
        file.id,
        file.name,
        file.createTime.time,
        file.size,
        file.type,
        file.downloadCount,
        file.locked
    )
}


/**
 * 将实体列表转换为响应列表
 *
 * @return 响应 列表
 */
fun List<FileInfo>.toResponse(): List<FileInfoResponse> {
    return this.map { FileInfoResponse(it) }.toList()
}
