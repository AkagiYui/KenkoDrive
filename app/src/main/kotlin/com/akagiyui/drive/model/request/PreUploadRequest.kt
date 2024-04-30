package com.akagiyui.drive.model.request

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

/**
 * 预上传文件 请求
 *
 * @author AkagiYui
 */
class PreUploadRequest {
    /**
     * 整个文件hash
     */
    @NotNull
    @Size(min = 32, max = 32)
    lateinit var hash: String

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
}
