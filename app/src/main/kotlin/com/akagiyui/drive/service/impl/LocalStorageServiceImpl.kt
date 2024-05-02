package com.akagiyui.drive.service.impl

import com.akagiyui.common.delegate.LoggerDelegate
import com.akagiyui.common.utils.deleteIfExists
import com.akagiyui.common.utils.mkdirOrThrow
import com.akagiyui.common.utils.toSafeFileName
import com.akagiyui.drive.service.StorageService
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.InputStreamResource
import org.springframework.stereotype.Service
import java.io.File
import java.io.InputStream

@Service
class LocalStorageServiceImpl : StorageService {
    private val log by LoggerDelegate()

    @Value("\${application.storage.local.root}")
    private var rootPath: String = "./data"

    private val root: File by lazy {
        val file = File(rootPath)
        file.mkdirOrThrow()
        file
    }

    private fun put(key: String, stream: InputStream) {
        log.debug("Store file to local: $key")
        val file = File(root, key.toSafeFileName())
        file.outputStream().use {
            stream.copyTo(it)
        }
    }

    override fun store(key: String, file: File, contentType: String?, callback: () -> Unit) {
        file.inputStream().use {
            put(key, file.inputStream())
        }
        callback()
    }

    override fun store(key: String, content: ByteArray, contentType: String?, callback: () -> Unit) {
        content.inputStream().use {
            put(key, it)
        }
        callback()
    }

    override fun exists(key: String): Boolean {
        val file = File(root, key.toSafeFileName())
        return file.exists()
    }

    override fun delete(key: String) {
        val file = File(root, key.toSafeFileName())
        file.deleteIfExists()
    }

    override fun get(key: String): InputStreamResource {
        val file = File(root, key.toSafeFileName())
        return InputStreamResource(file.inputStream())
    }

}
