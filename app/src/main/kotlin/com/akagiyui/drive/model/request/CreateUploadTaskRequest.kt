package com.akagiyui.drive.model.request

import jakarta.validation.constraints.*

/**
 * 创建上传任务 请求
 *
 * @author AkagiYui
 */
class CreateUploadTaskRequest {
    /**
     * 整个文件hash
     */
    @NotNull
    @Size(min = 64, max = 64, message = "hash长度不合法")
    @Pattern(regexp = "^[0-9a-fA-F]{64}\$")
    lateinit var hash: String

    /**
     * 文件名
     */
    @NotNull
    @NotBlank
    lateinit var filename: String

    /**
     * 文件大小
     */
    @Min(1)
    var filesize: Long = -1

    /**
     * 文件类型
     */
    var type: String = "application/octet-stream"

    /**
     * 目标文件夹ID
     */
    var folder: String? = null

    /**
     * 预期分片大小，单位：字节Byte
     */
    @NotNull
    var chunkSize: Long = -1

    /**
     * 分片数量
     */
    @Min(1)
    var chunkCount: Long = -1
}
