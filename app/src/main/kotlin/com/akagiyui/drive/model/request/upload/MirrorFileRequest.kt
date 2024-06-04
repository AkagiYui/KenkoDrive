package com.akagiyui.drive.model.request.upload

/**
 * 秒传文件 请求实体
 * @author AkagiYui
 */

class MirrorFileRequest {
    /**
     * 文件名
     */
    lateinit var filename: String

    /**
     * 文件大小
     */
    var size: Long = -1

    /**
     * 文件哈希
     */
    lateinit var hash: String

    /**
     * 文件夹ID
     */
    var folder: String? = null
}
