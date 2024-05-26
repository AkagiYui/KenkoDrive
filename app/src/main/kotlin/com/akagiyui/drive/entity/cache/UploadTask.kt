package com.akagiyui.drive.entity.cache

import jakarta.persistence.Id
import org.springframework.data.redis.core.RedisHash

/**
 * 上传任务 实体类
 * @author AkagiYui
 */
@RedisHash("upload_task", timeToLive = 1 * 24 * 60 * 60) // 1天
class UploadTask {
    /**
     * 任务ID
     */
    @Id
    lateinit var id: String

    /**
     * 用户ID
     */
    lateinit var userId: String

    /**
     * 文件名
     */
    lateinit var filename: String

    /**
     * 文件类型
     */
    lateinit var fileType: String

    /**
     * 整个文件hash
     */
    lateinit var hash: String

    /**
     * 文件大小
     */
    var size: Long = -1

    /**
     * 文件夹ID
     */
    var folder: String? = null

    /**
     * 预期分片大小，单位：字节Byte
     */
    var chunkSize: Long = -1

    /**
     * 分片数量
     */
    var chunkCount: Long = -1

    /**
     * 任务创建时间
     */
    var createTime: Long = System.currentTimeMillis()

    /**
     * 允许上传
     */
    var allowUpload: Boolean = true

}
