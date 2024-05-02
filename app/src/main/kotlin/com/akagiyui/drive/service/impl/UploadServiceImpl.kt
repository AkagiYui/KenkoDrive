package com.akagiyui.drive.service.impl


import com.akagiyui.common.ResponseEnum
import com.akagiyui.common.exception.CustomException
import com.akagiyui.common.utils.deleteIfExists
import com.akagiyui.common.utils.hasText
import com.akagiyui.common.utils.mkdirOrThrow
import com.akagiyui.drive.component.RedisCache
import com.akagiyui.drive.entity.FileInfo
import com.akagiyui.drive.model.ChunkedUploadInfo
import com.akagiyui.drive.model.request.PreUploadRequest
import com.akagiyui.drive.service.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.task.TaskExecutor
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.security.MessageDigest

/**
 * 上传 服务实现类
 *
 * AkagiYui
 */
@Service
class UploadServiceImpl(
    private val redisCache: RedisCache,
    private val userService: UserService,
    private val settingService: SettingService,
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
        val uploadFileSizeLimit = settingService.fileUploadMaxSize
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
        val realMd5 = MessageDigest.getInstance("MD5").digest(chunkBytes).joinToString("") { "%02x".format(it) }
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

//        storageService.saveChunk(user.id, fileHash, chunkIndex, chunkBytes)
//        if (chunkedInfo.isUploadFinish()) {
//            taskExecutor.execute {
//                // 合并分片
//                val storageFile = storageService.mergeChunk(user.id, fileHash, chunkedInfo.chunkCount)
//                // todo 校验整个文件的md5
//                // 保存文件信息
//                val fileInfo = FileInfo().apply {
//                    name = chunkedInfo.filename
//                    size = chunkedInfo.filesize
//                    type = storageFile.type
//                    hash = fileHash
//                    storageKey = storageFile.key
//                }
//                fileInfoService.addFileInfo(fileInfo)
//                // 添加用户文件关联
//                userFileService.addAssociation(user, fileInfo)
//            }
//        }
    }

    @Value("\${application.storage.local.cacheRoot}")
    private var uploadCacheFolder: String = "./"
        get() {
            File(field).mkdirOrThrow(); return field
        }

    override fun receiveMultipartFiles(files: List<MultipartFile>, folder: String?): List<FileInfo> {
        // 上传文件大小限制
        val uploadFileSizeLimit = settingService.fileUploadMaxSize
        files.forEach {
            if (it.size > uploadFileSizeLimit) {
                throw CustomException(ResponseEnum.FILE_TOO_LARGE)
            }
        }
        // 获取缓存目录
        val cacheDirectory: File = getUserCacheDirectory().let {
            if (folder.hasText()) {
                File(it, folder!!).apply { mkdirOrThrow() }
            } else {
                it
            }
        }
        val fileInfos = mutableListOf<FileInfo>()
        files.forEach { file ->
            val onlineFileStream = file.inputStream
            val cacheFile = File.createTempFile("upload", ".tmp", cacheDirectory)
            val cacheFileStream = cacheFile.outputStream()
            val messageDigest = MessageDigest.getInstance("MD5")
            onlineFileStream.use { input ->
                cacheFileStream.use { output ->
                    // 逐缓冲区读取文件内容，同时计算MD5
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    var bytes = input.read(buffer)
                    while (bytes >= 0) {
                        output.write(buffer, 0, bytes)
                        messageDigest.update(buffer, 0, bytes)
                        bytes = input.read(buffer)
                    }
                }
            }
            val md5String = messageDigest.digest().joinToString("") { "%02x".format(it) }
            val fileInfo = FileInfo().apply {
                name = file.originalFilename!!
                size = file.size
                type = file.contentType!!
                hash = md5String
                storageKey = md5String
                locked = true
            }
            fileInfoService.addFileInfo(fileInfo)
            userFileService.addAssociation(userService.getUser(), fileInfo, folder)
            storageService.store("file/${fileInfo.storageKey}", cacheFile, file.contentType) {
                cacheFile.deleteIfExists()
                fileInfo.locked = false
                fileInfoService.addFileInfo(fileInfo)
            }
            fileInfos.add(fileInfo)
        }
        return fileInfos
    }

    private fun getUserCacheDirectory(userId: String? = null): File {
        val id = userId ?: userService.getUser().id
        return File("$uploadCacheFolder/$id").apply { mkdirOrThrow() }
    }
}
