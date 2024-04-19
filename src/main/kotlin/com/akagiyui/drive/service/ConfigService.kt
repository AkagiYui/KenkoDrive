package com.akagiyui.drive.service

/**
 * 配置 服务接口
 *
 * @author AkagiYui
 */
interface ConfigService {

    /**
     * 是否初始化
     */
    fun isRegisterEnabled(): Boolean

    /**
     * 设置 是否开放注册
     */
    fun setRegisterEnabled(enabled: Boolean): Boolean

    /**
     * 是否初始化
     */
    fun isInitialized(): Boolean

    /**
     * 设置 是否初始化
     */
    fun setInitialized(initialized: Boolean): Boolean

    /**
     * 文件分片大小，单位：字节，默认：5MB
     */
    fun getFileUploadChunkSize(): Int

    /**
     * 设置 文件分片大小
     */
    fun setFileUploadChunkSize(chunkSize: Int): Int

    /**
     * 全局文件上传大小限制，单位：字节，默认：100MB
     */
    fun getFileUploadMaxSize(): Long

    /**
     * 设置 全局文件上传大小限制
     */
    fun setFileUploadMaxSize(maxSize: Long): Long

    /**
     * 获取配置
     */
    fun getConfig(): Map<String?, Any?>
}
