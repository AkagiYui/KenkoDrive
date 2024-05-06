package com.akagiyui.drive.service.impl.storage

import com.akagiyui.common.delegate.LoggerDelegate
import com.akagiyui.common.utils.hasText
import com.akagiyui.drive.service.StorageService
import io.minio.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Primary
import org.springframework.core.io.InputStreamResource
import org.springframework.stereotype.Service
import java.io.File
import java.io.InputStream

/**
 * Minio 存储服务实现类
 *
 * @author AkagiYui
 */
@Service
@Primary
@ConditionalOnProperty(prefix = "application.storage.minio", name = ["enable"], havingValue = "true")
class MinioStorageServiceImpl(
    @Autowired private val minioClient: MinioClient,
    @Value("\${application.storage.minio.bucket}")
    private val bucketName: String,
) : StorageService {
    private val log by LoggerDelegate()

    init {
        log.debug("Minio storage service init, bucket: $bucketName")
    }

    private fun put(key: String, stream: InputStream, length: Long, bufferSize: Long, contentType: String?) {
        log.debug("Store file to minio: $key")
        val args = PutObjectArgs.builder()
            .bucket(bucketName)
            .`object`(key)
            .contentType(if (contentType.hasText()) contentType else "application/octet-stream")
            .stream(stream, length, bufferSize)
            .build()
        val response = minioClient.putObject(args)
        log.debug("Store file to minio: {}, etag: {}", key, response.etag())
    }

    override fun store(key: String, file: File, contentType: String?, callback: () -> Unit) {
        file.inputStream().use {
            put(key, it, file.length(), 100 * 1024 * 1024, contentType)
        }
        callback()
    }

    override fun store(key: String, content: ByteArray, contentType: String?, callback: () -> Unit) {
        content.inputStream().use {
            put(key, it, content.size.toLong(), -1, contentType)
        }
        callback()
    }

    override fun exists(key: String): Boolean {
        val args = StatObjectArgs.builder()
            .bucket(bucketName)
            .`object`(key)
            .build()
        return try {
            minioClient.statObject(args)
            true
        } catch (e: Exception) {
            log.debug("File not exists: $key")
            false
        }
    }

    override fun delete(key: String) {
        val args = RemoveObjectArgs.builder()
            .bucket(bucketName)
            .`object`(key)
            .build()
        minioClient.removeObject(args)
    }

    override fun get(key: String): InputStreamResource {
        val args = GetObjectArgs.builder()
            .bucket(bucketName)
            .`object`(key)
            .build()
        val response = minioClient.getObject(args)
        return InputStreamResource(response)
    }
}
