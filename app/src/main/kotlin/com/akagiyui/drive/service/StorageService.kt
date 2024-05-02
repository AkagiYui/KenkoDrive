package com.akagiyui.drive.service

import org.springframework.core.io.InputStreamResource
import org.springframework.scheduling.annotation.Async
import java.io.File

/**
 * 存储 服务接口
 * <p>
 *     存储服务是一个 Key-Value 存储服务，Key 为文件名，Value 为文件内容
 *     {key: String, value: byte[]/Stream}
 * </p>
 *
 * @author AkagiYui
 */
interface StorageService {
    /**
     * 保存文件
     * @param key 文件名
     * @param file 文件
     * @param contentType 文件类型
     * @param callback 完成回调
     */
    @Async
    fun store(key: String, file: File, contentType: String?, callback: () -> Unit = {})

    /**
     * 保存文件
     * @param key 文件名
     * @param content 文件内容
     * @param contentType 文件类型
     * @param callback 完成回调
     */
    @Async
    fun store(key: String, content: ByteArray, contentType: String?, callback: () -> Unit = {})

    /**
     * 文件是否存在
     * @param key 文件名
     */
    fun exists(key: String): Boolean

    /**
     * 删除文件
     * @param key 文件名
     */
    @Async
    fun delete(key: String)

    /**
     * 获取文件
     * @param key 文件名
     */
    fun get(key: String): InputStreamResource
}
