package com.akagiyui.drive.service

/**
 * 设置 服务接口
 *
 * @author AkagiYui
 */
interface SettingService {

    companion object {
        /**
         * 是否开放注册 键名
         */
        const val REGISTER_ENABLED = "registerEnabled"

        /**
         * 是否初始化 键名
         */
        const val IS_INITIALIZED = "isInitialized"

        /**
         * 文件分片大小 键名
         */
        const val FILE_UPLOAD_CHUNK_SIZE = "fileUploadChunkSize"

        /**
         * 全局文件上传大小限制 键名
         */
        const val FILE_UPLOAD_MAX_SIZE = "fileUploadMaxSize"
    }

    /**
     * 是否初始化
     */
    var registerEnabled: Boolean

    /**
     * 是否初始化
     */
    var initialized: Boolean

    /**
     * 文件分片大小，单位：字节，默认：5MB
     */
    var fileUploadChunkSize: Int

    /**
     * 全局文件上传大小限制，单位：字节，默认：100MB
     */
    var fileUploadMaxSize: Long

    /**
     * 获取设置
     */
    fun getSettings(): Map<String, Any>
}
