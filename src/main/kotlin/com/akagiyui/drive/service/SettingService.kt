package com.akagiyui.drive.service

/**
 * 设置 服务接口
 *
 * @author AkagiYui
 */
interface SettingService {

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
