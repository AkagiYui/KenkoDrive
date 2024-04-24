package com.akagiyui.drive.service.impl

import cn.hutool.crypto.digest.DigestUtil
import com.akagiyui.common.ResponseEnum
import com.akagiyui.common.exception.CustomException
import com.akagiyui.drive.component.RedisCache
import com.akagiyui.drive.entity.FileInfo
import com.akagiyui.drive.model.ChunkedUploadInfo
import com.akagiyui.drive.model.request.PreUploadRequest
import com.akagiyui.drive.service.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.task.TaskExecutor
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

/**
 * 上传 服务实现类
 *
 * AkagiYui
 */
@Service
class UploadServiceImpl(
    private val redisCache: RedisCache,
    private val userService: UserService,
    private val configService: ConfigService,
    private val storageService: StorageService,
    private val fileInfoService: FileInfoService,
    private val userFileService: UserFileService,
    @Qualifier("taskExecutor") private val taskExecutor: TaskExecutor,
) : UploadService {

    private fun isInfoInRedis(userId: String, hash: String): Boolean {
        val redisKey = "upload:$userId:$hash"
        return redisKey in redisCache
    }

    private fun saveInfoToRedis(userId: String, hash: String, chunkedInfo: ChunkedUploadInfo) {
        val redisKey = "upload:$userId:$hash"
        redisCache[redisKey] = chunkedInfo
    }

    private fun getInfoFromRedis(userId: String, hash: String): ChunkedUploadInfo {
        val redisKey = "upload:$userId:$hash"
        return redisCache[redisKey] ?: throw CustomException(ResponseEnum.TASK_NOT_FOUND)
    }

    override fun requestUpload(preUploadRequest: PreUploadRequest) {
        // 上传文件大小限制
        val uploadFileSizeLimit = configService.getFileUploadMaxSize()
        if (preUploadRequest.filesize > uploadFileSizeLimit) {
            throw CustomException(ResponseEnum.FILE_TOO_LARGE)
        }

        val user = userService.getUser()

        // 是否已经存在上传信息
        if (isInfoInRedis(user.id, preUploadRequest.hash)) {
            throw CustomException(ResponseEnum.TASK_EXIST)
        }
        // 上传信息
        val chunkedInfo = ChunkedUploadInfo(preUploadRequest).apply {
            userId = user.id
        }
        // 保存上传信息到redis
        saveInfoToRedis(user.id, preUploadRequest.hash, chunkedInfo)
    }

    override fun uploadChunk(fileHash: String, chunk: MultipartFile, chunkHash: String, chunkIndex: Int) {
        if (chunk.isEmpty || (chunkIndex < 0) || chunkHash.isBlank()) {
            throw CustomException(ResponseEnum.BAD_REQUEST)
        }

        val user = userService.getUser()
        if (!isInfoInRedis(user.id, fileHash)) {
            throw CustomException(ResponseEnum.TASK_NOT_FOUND)
        }

        val chunkedInfo = getInfoFromRedis(user.id, fileHash)
        if (chunkedInfo.isUploadFinish()) {
            throw CustomException(ResponseEnum.TASK_NOT_FOUND)
        }

        // 校验分片
        val chunkBytes = try {
            chunk.bytes
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        val realMd5 = DigestUtil.md5Hex(chunkBytes)
        if (realMd5 != chunkHash) {
            throw CustomException(ResponseEnum.VERIFY_FAILED)
        }

        // 保存分片信息
//        val chunksInfo = chunkedInfo.chunks
//        val chunkInfo = chunksInfo[chunkIndex].apply {
//            hash = chunkHash
//            isCheckSuccess = true
//        }
        saveInfoToRedis(user.id, fileHash, chunkedInfo)

        storageService.saveChunk(user.id, fileHash, chunkIndex, chunkBytes)
        if (chunkedInfo.isUploadFinish()) {
            taskExecutor.execute {
                // 合并分片
                val storageFile = storageService.mergeChunk(user.id, fileHash, chunkedInfo.chunkCount)
                // todo 校验整个文件的md5
                // 保存文件信息
                val fileInfo = FileInfo().apply {
                    name = chunkedInfo.filename
                    size = chunkedInfo.filesize
                    type = storageFile.type
                    hash = fileHash
                    storageKey = storageFile.key
                }
                fileInfoService.addFileInfo(fileInfo)
                // 添加用户文件关联
                userFileService.addAssociation(user, fileInfo, null)
            }
        }
    }
}
