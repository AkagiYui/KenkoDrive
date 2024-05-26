package com.akagiyui.drive.service.impl


import cn.hutool.core.util.IdUtil
import com.akagiyui.common.ResponseEnum
import com.akagiyui.common.delegate.LoggerDelegate
import com.akagiyui.common.exception.CustomException
import com.akagiyui.common.utils.deleteIfExists
import com.akagiyui.common.utils.hasText
import com.akagiyui.common.utils.mkdirOrThrow
import com.akagiyui.drive.component.RedisCache
import com.akagiyui.drive.entity.FileInfo
import com.akagiyui.drive.entity.User
import com.akagiyui.drive.entity.UserFile
import com.akagiyui.drive.entity.cache.UploadTask
import com.akagiyui.drive.model.request.CreateUploadTaskRequest
import com.akagiyui.drive.repository.cache.UploadTaskRepository
import com.akagiyui.drive.service.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.task.TaskExecutor
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.security.MessageDigest

/**
 * 上传 服务实现类
 *
 * AkagiYui
 */
@Service
class UploadServiceImpl(
    private val redisCache: RedisCache,
    private val settingService: SettingService,
    private val storageService: StorageService,
    private val fileInfoService: FileInfoService,
    private val userFileService: UserFileService,
    private val uploadTaskRepository: UploadTaskRepository,
    @Qualifier("taskExecutor") private val taskExecutor: TaskExecutor,
) : UploadService {
    private val idGenerator = IdUtil.getSnowflake()
    private val log by LoggerDelegate()

    override fun createUploadTask(user: User, createUploadTaskRequest: CreateUploadTaskRequest): UploadTask {
        // 上传文件大小限制
        val uploadFileSizeLimit = settingService.fileUploadMaxSize
        if (createUploadTaskRequest.filesize > uploadFileSizeLimit) {
            throw CustomException(ResponseEnum.FILE_TOO_LARGE)
        }

        val task = UploadTask().apply {
            id = idGenerator.nextIdStr()
            userId = user.id
            filename = createUploadTaskRequest.filename
            hash = createUploadTaskRequest.hash
            size = createUploadTaskRequest.filesize
            fileType = createUploadTaskRequest.type
            folder = createUploadTaskRequest.folder
            chunkSize = createUploadTaskRequest.chunkSize
            chunkCount = createUploadTaskRequest.chunkCount
        }
        for (i in 0 until task.chunkCount) {
            redisCache.set("chunk_uploaded:${task.id}", i, false)
        }
        uploadTaskRepository.save(task)
        return task
    }

    @Transactional
    override fun uploadChunk(user: User, taskId: String, chunk: MultipartFile, chunkHash: String, chunkIndex: Int) {
        val task = uploadTaskRepository.findById(taskId).orElseThrow {
            CustomException(ResponseEnum.TASK_NOT_FOUND)
        }
        if (task.userId != user.id) {
            throw CustomException(ResponseEnum.TASK_NOT_FOUND)
        }
        if (task.chunkCount <= chunkIndex) {
            throw CustomException(ResponseEnum.TASK_NOT_FOUND)
        }
        if (!task.allowUpload) {
            throw CustomException(ResponseEnum.TASK_NOT_FOUND)
        }

        // 上传文件大小限制
        val uploadFileSizeLimit = settingService.fileUploadMaxSize
        if (chunk.size > uploadFileSizeLimit) {
            throw CustomException(ResponseEnum.FILE_TOO_LARGE)
        }

        // 获取缓存目录
        val userCacheDirectory = getUserCacheDirectory(user.id)
        val taskCacheDirectory = File(userCacheDirectory, taskId).apply { mkdirOrThrow() }
        val localCacheFile = File(taskCacheDirectory, chunkIndex.toString()).apply { createNewFile() }
        // 接收文件
        val onlineFileStream = chunk.inputStream
        val cacheFileStream = localCacheFile.outputStream()
        val messageDigest = MessageDigest.getInstance("SHA-256")
        onlineFileStream.use { input ->
            cacheFileStream.use { output ->
                // 逐缓冲区读取文件内容，同时计算哈希值
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                var bytes = input.read(buffer)
                while (bytes >= 0) {
                    output.write(buffer, 0, bytes)
                    messageDigest.update(buffer, 0, bytes)
                    bytes = input.read(buffer)
                }
            }
        }
        val hashString = messageDigest.digest().joinToString("") { "%02x".format(it) }
        // 对比哈希
        if (hashString != chunkHash) {
            log.debug("task: $taskId, chunk: $chunkIndex, hash not match")
            throw CustomException(ResponseEnum.GENERAL_ERROR)
        }
        // 标记已上传
        redisCache.set("chunk_uploaded:$taskId", chunkIndex, true)
        val chunkMap = redisCache.getMap<Int, Boolean>("chunk_uploaded:$taskId")
        if (chunkMap.values.all { it }) {
            task.allowUpload = false
            uploadTaskRepository.save(task)
            taskExecutor.execute {
                mergeChunks(user, task)
            }
        } else {
            uploadTaskRepository.save(task)
        }
        log.debug("task: $taskId, chunk: $chunkIndex, upload success")
    }

    private fun mergeChunks(user: User, task: UploadTask) {
        log.debug("task: ${task.id}, merge chunks")
        val userCacheDirectory = getUserCacheDirectory(task.userId)
        val taskCacheDirectory = File(userCacheDirectory, task.id)
        val localCacheFiles = taskCacheDirectory.listFiles() ?: return
        val cacheChunks = localCacheFiles.sortedBy { it.name.toInt() }
        val cacheFile = File.createTempFile("upload", ".tmp", taskCacheDirectory)
        val cacheFileStream = cacheFile.outputStream()
        val messageDigest = MessageDigest.getInstance("SHA-256")
        cacheFileStream.use { output ->
            cacheChunks.forEach { file ->
                file.inputStream().use { input ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    var bytes = input.read(buffer)
                    while (bytes >= 0) {
                        output.write(buffer, 0, bytes)
                        messageDigest.update(buffer, 0, bytes)
                        bytes = input.read(buffer)
                    }
                }
            }
        }
        val hashString = messageDigest.digest().joinToString("") { "%02x".format(it) }
        if (hashString != task.hash) {
            log.debug("task: ${task.id}, hash not match")
            throw CustomException(ResponseEnum.GENERAL_ERROR)
        }
        // 检查文件是否已存在
        val existFileInfo = try {
            fileInfoService.getFileInfoByHash(hashString)
        } catch (e: CustomException) {
            null
        }
        if (existFileInfo != null) {
            // 如果文件已存在，则直接关联
            userFileService.addAssociation(
                user,
                task.filename,
                existFileInfo,
                task.folder
            )
            taskCacheDirectory.deleteRecursively()
            return
        }
        val storageKey = "file/$hashString"
        val fileInfo = FileInfo().apply {
            name = task.filename
            size = task.size
            type = task.fileType
            hash = hashString
            this.storageKey = storageKey
            locked = true
        }
        fileInfoService.addFileInfo(fileInfo)
        userFileService.addAssociation(user, fileInfo.name, fileInfo, task.folder)
        storageService.store(storageKey, cacheFile, task.fileType) {
            taskCacheDirectory.deleteRecursively()
            fileInfo.locked = false
            fileInfoService.addFileInfo(fileInfo)
        }
    }

    @Value("\${application.storage.local.cacheRoot}")
    private var uploadCacheFolder: String = "./"
        get() {
            File(field).mkdirOrThrow(); return field
        }

    override fun receiveMultipartFiles(user: User, files: List<MultipartFile>, folder: String?): List<UserFile> {
        // 上传文件大小限制
        val uploadFileSizeLimit = settingService.fileUploadMaxSize
        files.forEach {
            if (it.size > uploadFileSizeLimit) {
                throw CustomException(ResponseEnum.FILE_TOO_LARGE)
            }
        }
        // 获取缓存目录
        val cacheDirectory: File = getUserCacheDirectory(user.id).let {
            if (folder.hasText()) {
                File(it, folder).apply { mkdirOrThrow() }
            } else {
                it
            }
        }
        val userInfos = mutableListOf<UserFile>()
        files.forEach { file ->
            val onlineFileStream = file.inputStream
            val cacheFile = File.createTempFile("upload", ".tmp", cacheDirectory)
            val cacheFileStream = cacheFile.outputStream()
            val messageDigest = MessageDigest.getInstance("SHA-256")
            onlineFileStream.use { input ->
                cacheFileStream.use { output ->
                    // 逐缓冲区读取文件内容，同时计算哈希值
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    var bytes = input.read(buffer)
                    while (bytes >= 0) {
                        output.write(buffer, 0, bytes)
                        messageDigest.update(buffer, 0, bytes)
                        bytes = input.read(buffer)
                    }
                }
            }
            val hashString = messageDigest.digest().joinToString("") { "%02x".format(it) }
            val existFileInfo = try {
                fileInfoService.getFileInfoByHash(hashString)
            } catch (e: CustomException) {
                null
            }
            if (existFileInfo != null) {
                // 如果文件已存在，则直接关联
                userInfos.add(
                    userFileService.addAssociation(
                        user,
                        file.originalFilename!!,
                        existFileInfo,
                        folder
                    )
                )
                return@forEach
            }
            val storageKey = "file/$hashString"
            val fileInfo = FileInfo().apply {
                name = file.originalFilename!!
                size = file.size
                type = file.contentType!!
                hash = hashString
                this.storageKey = storageKey
                locked = true
            }
            fileInfoService.addFileInfo(fileInfo)
            userInfos.add(userFileService.addAssociation(user, fileInfo.name, fileInfo, folder))
            storageService.store(storageKey, cacheFile, file.contentType) {
                cacheFile.deleteIfExists()
                fileInfo.locked = false
                fileInfoService.addFileInfo(fileInfo)
            }
        }
        return userInfos
    }

    private fun getUserCacheDirectory(userId: String? = null): File {
        return File("$uploadCacheFolder/$userId").apply { mkdirOrThrow() }
    }
}
