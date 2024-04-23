package com.akagiyui.drive.service.impl

import com.akagiyui.drive.entity.KeyValueConfig
import com.akagiyui.drive.repository.ConfigRepository
import com.akagiyui.drive.service.ConfigService
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

/**
 * 配置表操作 实现类
 *
 * @author AkagiYui
 */
@Service
class ConfigServiceImpl(private val configRepository: ConfigRepository) : ConfigService {

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
     * 通过配置项键查找配置项值
     *
     * @param key 配置项键
     * @return 配置项值
     */
    private fun findByKey(key: String): String? {
        return configRepository.findByConfigKey(key)?.configValue
    }

    /**
     * 通过配置项键查找配置项值，并转换为布尔值
     *
     * @param key                  配置项键
     * @param defaultValueSupplier 默认值生产者
     * @return 配置项值
     */
    private fun findBoolean(key: String, defaultValueSupplier: () -> Boolean): Boolean {
        return findByKey(key)?.toBoolean() ?: defaultValueSupplier()
    }

    /**
     * 通过配置项键查找配置项值，并转换为整数
     *
     * @param key                  配置项键
     * @param defaultValueSupplier 默认值生产者
     * @return 配置项值
     */
    private fun findInteger(key: String, defaultValueSupplier: () -> Int): Int {
        return findByKey(key)?.toInt() ?: defaultValueSupplier()
    }

    /**
     * 通过配置项键查找配置项值，并转换为长整数
     *
     * @param key                  配置项键
     * @param defaultValueSupplier 默认值生产者
     * @return 配置项值
     */
    private fun findLong(key: String, defaultValueSupplier: () -> Long): Long {
        return findByKey(key)?.toLong() ?: defaultValueSupplier()
    }

    /**
     * 保存配置项
     *
     * @param key   配置项键
     * @param value 配置项值
     */
    private fun save(key: String, value: Boolean) {
        val keyValueConfig = KeyValueConfig().apply {
            configKey = key
            configValue = value.toString()
        }
        configRepository.save(keyValueConfig)
    }

    /**
     * 保存配置项
     *
     * @param key   配置项键
     * @param value 配置项值
     */
    private fun save(key: String, value: Int) {
        val keyValueConfig = KeyValueConfig().apply {
            configKey = key
            configValue = value.toString()
        }
        configRepository.save(keyValueConfig)
    }

    /**
     * 保存配置项
     *
     * @param key   配置项键
     * @param value 配置项值
     */
    private fun save(key: String, value: Long) {
        val keyValueConfig = KeyValueConfig().apply {
            configKey = key
            configValue = value.toString()
        }
        configRepository.save(keyValueConfig)
    }

    @Cacheable(value = ["config"], key = "#root.target.REGISTER_ENABLED")
    override fun isRegisterEnabled(): Boolean {
        return findBoolean(REGISTER_ENABLED) { setRegisterEnabled(true) }
    }

    @CachePut(value = ["config"], key = "#root.target.REGISTER_ENABLED")
    override fun setRegisterEnabled(enabled: Boolean): Boolean {
        save(REGISTER_ENABLED, enabled)
        return enabled
    }

    @Cacheable(value = ["config"], key = "#root.target.IS_INITIALIZED")
    override fun isInitialized(): Boolean {
        return findBoolean(IS_INITIALIZED) { setInitialized(false) }
    }

    @CachePut(value = ["config"], key = "#root.target.IS_INITIALIZED")
    override fun setInitialized(initialized: Boolean): Boolean {
        save(IS_INITIALIZED, initialized)
        return initialized
    }

    @Cacheable(value = ["config"], key = "#root.target.FILE_UPLOAD_CHUNK_SIZE")
    override fun getFileUploadChunkSize(): Int {
        return findInteger(FILE_UPLOAD_CHUNK_SIZE) { setFileUploadChunkSize(5 * 1024 * 1024) }
    }

    @CachePut(value = ["config"], key = "#root.target.FILE_UPLOAD_CHUNK_SIZE")
    override fun setFileUploadChunkSize(chunkSize: Int): Int {
        save(FILE_UPLOAD_CHUNK_SIZE, chunkSize)
        return chunkSize
    }

    @Cacheable(value = ["config"], key = "#root.target.FILE_UPLOAD_MAX_SIZE")
    override fun getFileUploadMaxSize(): Long {
        return findLong(FILE_UPLOAD_MAX_SIZE) { setFileUploadMaxSize(100L * 1024 * 1024) }
    }

    @CachePut(value = ["config"], key = "#root.target.FILE_UPLOAD_MAX_SIZE")
    override fun setFileUploadMaxSize(maxSize: Long): Long {
        save(FILE_UPLOAD_MAX_SIZE, maxSize)
        return maxSize
    }

    override fun getConfig(): Map<String, Any> {
        return mapOf(
            REGISTER_ENABLED to isRegisterEnabled(),
            IS_INITIALIZED to isInitialized(),
            FILE_UPLOAD_CHUNK_SIZE to getFileUploadChunkSize(),
            FILE_UPLOAD_MAX_SIZE to getFileUploadMaxSize()
        )
    }
}
