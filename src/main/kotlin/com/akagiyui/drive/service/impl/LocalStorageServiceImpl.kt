package com.akagiyui.drive.service.impl

import com.akagiyui.common.ResponseEnum
import com.akagiyui.common.delegate.LoggerDelegate
import com.akagiyui.common.exception.CreateFolderException
import com.akagiyui.common.exception.CustomException
import com.akagiyui.common.utils.createParentDir
import com.akagiyui.common.utils.deleteIfExists
import com.akagiyui.common.utils.mkdirOrThrow
import com.akagiyui.drive.model.StorageFile
import com.akagiyui.drive.service.StorageService
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.InputStreamResource
import org.springframework.stereotype.Service
import java.io.File
import java.io.OutputStream

/**
 * Local storage service implementation class.
 */
@Service
class LocalStorageServiceImpl : StorageService {
    private val log by LoggerDelegate()

    @Value("\${application.storage.local.root:}")
    private var root: String = "./storage"

    private lateinit var tempChunkDir: String

    @PostConstruct
    fun init() {
        // 检查根目录是否存在，不存在则创建
        log.debug("Local storage root dir: , {}", root)
        val rootDir = File(root)
        try {
            rootDir.mkdirOrThrow()
        } catch (e: Exception) {
            throw CreateFolderException("Create root dir failed")
        }

        // 检查临时分片目录是否存在，不存在则创建
        tempChunkDir = "$root${File.separator}temp"
        val tempChunkDir = File(tempChunkDir)
        try {
            tempChunkDir.mkdirOrThrow()
        } catch (e: Exception) {
            throw CreateFolderException("Create temp chunk dir failed")
        }
    }

    override fun getFile(key: String): InputStreamResource {
        val file = File("$root${File.separator}$key")
        if (!file.exists()) {
            throw CustomException(ResponseEnum.NOT_FOUND)
        }
        return InputStreamResource(file.toURI().toURL().openStream())
    }

    override fun saveFile(key: String, content: ByteArray) {
        // todo key 中可能包含路径不允许的字符，需要处理
        val file = File("$root${File.separator}$key")
        if (file.exists()) return
        file.outputStream().use { it.write(content) }
    }

    override fun saveFile(key: String): OutputStream {
        return saveFile(key, false)
    }

    override fun saveFile(key: String, overwrite: Boolean): OutputStream {
        val file = File("$root${File.separator}$key")
        file.createParentDir()
        if (file.exists() && !overwrite) {
            throw RuntimeException("File already exists")
        }
        file.deleteIfExists()
        return file.outputStream()
    }

    override fun exists(key: String): Boolean {
        return File("$root${File.separator}$key").exists()
    }

    override fun deleteFile(key: String) {
        val file = File("$root${File.separator}$key")
        if (!file.exists()) {
            throw CustomException(ResponseEnum.NOT_FOUND)
        }
        file.deleteIfExists()
    }

    override fun saveChunk(userId: String, fileHash: String, chunkIndex: Int, content: ByteArray) {
        // 如果 userId 目录不存在，则创建
        val chunkFile = getChunkFile(userId, fileHash, chunkIndex)
        // 如果分片已存在，则覆盖
        chunkFile.deleteIfExists()
        // 保存分片
        chunkFile.outputStream().use { it.write(content) }
    }

    private fun getChunkFile(userId: String, fileHash: String, chunkIndex: Int): File {
        // 因为可能有多个用户上传同一个文件，所以需要在 userId 目录下再创建一个 fileHash 目录
        val userDir = File("$tempChunkDir${File.separator}$userId").apply {
            mkdirOrThrow("Creating user directory failed")
        }
        // 如果 fileHash 目录不存在，则创建
        val fileDir = File("$userDir${File.separator}$fileHash").apply {
            mkdirOrThrow("Creating file directory failed")
        }
        // 分片文件名为分片序号
        return File("$fileDir${File.separator}$chunkIndex")
    }

    override fun mergeChunk(userId: String, fileHash: String, chunkCount: Long): StorageFile {
        // 分片目录
        val fileDir = File("$tempChunkDir${File.separator}$userId${File.separator}$fileHash")
        val file = File("$fileDir${File.separator}$fileHash")

        // 合并分片，待验证
        file.outputStream().use { output ->
            (0 until chunkCount).forEach { index ->
                File("$fileDir${File.separator}$index").inputStream().use { input ->
                    input.copyTo(output)
                }
            }
        }
        // 移动完整文件到文件目录
        val targetFile = File("$root${File.separator}$fileHash")
        targetFile.createParentDir()
        targetFile.deleteIfExists()
        // 移动文件
        if (!file.renameTo(targetFile)) {
            throw RuntimeException("Move file failed")
        }
        return StorageFile(
            key = fileHash,
            size = targetFile.length(),
            type = "",
            hash = fileHash,
        )
    }
}
