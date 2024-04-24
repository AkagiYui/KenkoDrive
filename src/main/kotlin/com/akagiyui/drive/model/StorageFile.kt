package com.akagiyui.drive.model

/**
 * 存储中的文件
 *
 * @author AkagiYui
 */
class StorageFile(
    /**
     * 文件名
     */
    val key: String,
    /**
     * 文件大小
     */
    val size: Long,
    /**
     * 文件类型
     */
    val type: String,
    /**
     * 文件哈希
     */
    val hash: String,
)
