package com.akagiyui.drive.model

import com.akagiyui.drive.model.request.PreUploadRequest
import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable

/**
 * 分块文件上传信息
 *
 * @author AkagiYui
 */
class ChunkedUploadInfo() : Serializable {
    /**
     * 整个文件hash
     */
    lateinit var hash: String

    /**
     * 预期分片大小，单位：字节Byte
     */
    var chunkSize: Long = -1

    /**
     * 分片数量
     */
    var chunkCount: Long = -1

    /**
     * 文件名
     */
    lateinit var filename: String

    /**
     * 用户ID
     */
    lateinit var userId: String

    /**
     * 文件大小
     */
    var filesize: Long = -1

    /**
     * 分片信息 列表
     */
    lateinit var chunks: MutableList<Chunk>

    constructor(request: PreUploadRequest) : this() {
        this.hash = request.hash
        this.chunkSize = request.chunkSize.toLong()
        this.chunkCount = request.chunkCount.toLong()
        this.filename = request.filename
        this.filesize = request.filesize
        // 初始化分片信息
        this.chunks = mutableListOf()
        for (i in 0 until this.chunkCount) {
            chunks.add(Chunk(i, -1, null, false))
        }
    }


    /**
     * 是否上传完成
     */
    @JsonIgnore
    fun isUploadFinish(): Boolean {
        for (chunk in chunks) {
            if (!chunk.checkSuccess) {
                return false
            }
        }
        return true
    }
}

/**
 * 分片信息
 */
class Chunk() : Serializable {
    /**
     * 分片序号
     */
    var index: Long = -1

    /**
     * 分片大小
     */
    var size: Long = -1

    /**
     * 分片hash
     */
    var hash: String? = null

    /**
     * 校验成功
     */
    var checkSuccess: Boolean = false

    constructor(index: Long, size: Long, hash: String?, checkSuccess: Boolean) : this() {
        this.index = index
        this.size = size
        this.hash = hash
        this.checkSuccess = checkSuccess
    }
}
