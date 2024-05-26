package com.akagiyui.drive.service

import com.akagiyui.drive.entity.User
import com.akagiyui.drive.entity.UserFile
import com.akagiyui.drive.entity.cache.UploadTask
import com.akagiyui.drive.model.request.CreateUploadTaskRequest
import org.springframework.web.multipart.MultipartFile

/**
 * 上传 服务接口
 *
 * @author AkagiYui
 */
interface UploadService {
    /**
     * 创建上传任务
     */
    fun createUploadTask(user: User, createUploadTaskRequest: CreateUploadTaskRequest): UploadTask

    /**
     * 上传分片
     */
    fun uploadChunk(user: User, taskId: String, chunk: MultipartFile, chunkHash: String, chunkIndex: Int)

    /**
     * 接收文件
     */
    fun receiveMultipartFiles(user: User, files: List<MultipartFile>, folder: String?): List<UserFile>
}
