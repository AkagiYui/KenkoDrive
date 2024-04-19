package com.akagiyui.drive.service

import com.akagiyui.drive.model.StorageFile
import org.springframework.core.io.InputStreamResource
import org.springframework.scheduling.annotation.Async
import java.io.OutputStream

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
     * 获取文件
     */
    fun getFile(key: String): InputStreamResource

    /**
     * 保存文件
     */
    @Async
    fun saveFile(key: String, content: ByteArray)

    /**
     * 保存文件，请手动关闭输出流
     *
     * @param key 文件名
     * @return 文件输出流
     */
    fun saveFile(key: String): OutputStream

    /**
     * 保存文件
     *
     * @param key       文件名
     * @param overwrite 是否覆盖
     * @return 文件输出流
     */
    fun saveFile(key: String, overwrite: Boolean): OutputStream

    /**
     * 文件是否存在
     */
    fun exists(key: String): Boolean

    /**
     * 删除文件
     */
    @Async
    fun deleteFile(key: String)

    /**
     * 存储分片
     */
    fun saveChunk(userId: String, fileHash: String, chunkIndex: Int, content: ByteArray)

    /**
     * 合并分片
     *
     * @return
     */
    fun mergeChunk(userId: String, fileHash: String, chunkCount: Int): StorageFile

}
