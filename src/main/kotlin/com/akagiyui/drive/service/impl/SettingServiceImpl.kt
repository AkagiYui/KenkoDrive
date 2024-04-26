package com.akagiyui.drive.service.impl

import com.akagiyui.drive.entity.KeyValueSetting
import com.akagiyui.drive.repository.SettingRepository
import com.akagiyui.drive.service.SettingService
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

/**
 * 设置表操作 实现类
 *
 * @author AkagiYui
 */
@Service
class SettingServiceImpl(private val settingRepository: SettingRepository) : SettingService {

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
     * 通过设置项键查找设置项值
     *
     * @param key 设置项键
     * @return 设置项值
     */
    private fun findByKey(key: String): String? {
        return settingRepository.findBySettingKey(key)?.settingValue
    }

    /**
     * 通过设置项键查找设置项值，并转换为布尔值
     *
     * @param key                  设置项键
     * @param defaultValueSupplier 默认值生产者
     * @return 设置项值
     */
    private fun findBoolean(key: String, defaultValueSupplier: () -> Boolean): Boolean {
        return findByKey(key)?.toBoolean() ?: defaultValueSupplier()
    }

    /**
     * 通过设置项键查找设置项值，并转换为整数
     *
     * @param key                  设置项键
     * @param defaultValueSupplier 默认值生产者
     * @return 设置项值
     */
    private fun findInteger(key: String, defaultValueSupplier: () -> Int): Int {
        return findByKey(key)?.toInt() ?: defaultValueSupplier()
    }

    /**
     * 通过设置项键查找设置项值，并转换为长整数
     *
     * @param key                  设置项键
     * @param defaultValueSupplier 默认值生产者
     * @return 设置项值
     */
    private fun findLong(key: String, defaultValueSupplier: () -> Long): Long {
        return findByKey(key)?.toLong() ?: defaultValueSupplier()
    }

    /**
     * 保存设置项
     *
     * @param key   设置项键
     * @param value 设置项值
     */
    private fun save(key: String, value: Boolean): Boolean {
        val keyValueSetting = KeyValueSetting().apply {
            settingKey = key
            settingValue = value.toString()
        }
        settingRepository.save(keyValueSetting)
        return value
    }

    /**
     * 保存设置项
     *
     * @param key   设置项键
     * @param value 设置项值
     */
    private fun save(key: String, value: Int): Int {
        val keyValueSetting = KeyValueSetting().apply {
            settingKey = key
            settingValue = value.toString()
        }
        settingRepository.save(keyValueSetting)
        return value
    }

    /**
     * 保存设置项
     *
     * @param key   设置项键
     * @param value 设置项值
     */
    private fun save(key: String, value: Long): Long {
        val keyValueSetting = KeyValueSetting().apply {
            settingKey = key
            settingValue = value.toString()
        }
        settingRepository.save(keyValueSetting)
        return value
    }

    @get:Cacheable(value = ["config"], key = "#root.target.REGISTER_ENABLED")
    @set:CacheEvict(value = ["config"], key = "#root.target.REGISTER_ENABLED")
    override var registerEnabled: Boolean
        get() = findBoolean(REGISTER_ENABLED) { save(REGISTER_ENABLED, true) }
        set(value) {
            save(REGISTER_ENABLED, value)
        }

    @get:Cacheable(value = ["config"], key = "#root.target.IS_INITIALIZED")
    @set:CacheEvict(value = ["config"], key = "#root.target.IS_INITIALIZED")
    override var initialized: Boolean
        get() = findBoolean(IS_INITIALIZED) { save(IS_INITIALIZED, false) }
        set(value) {
            save(IS_INITIALIZED, value)
        }

    @get:Cacheable(value = ["config"], key = "#root.target.FILE_UPLOAD_CHUNK_SIZE")
    @set:CacheEvict(value = ["config"], key = "#root.target.FILE_UPLOAD_CHUNK_SIZE")
    override var fileUploadChunkSize: Int
        get() = findInteger(FILE_UPLOAD_CHUNK_SIZE) { save(FILE_UPLOAD_CHUNK_SIZE, 5 * 1024 * 1024) }
        set(value) {
            save(FILE_UPLOAD_CHUNK_SIZE, value)
        }

    @get:Cacheable(value = ["config"], key = "#root.target.FILE_UPLOAD_MAX_SIZE")
    @set:CacheEvict(value = ["config"], key = "#root.target.FILE_UPLOAD_MAX_SIZE")
    override var fileUploadMaxSize: Long
        get() = findLong(FILE_UPLOAD_MAX_SIZE) { save(FILE_UPLOAD_MAX_SIZE, 100L * 1024 * 1024) }
        set(value) {
            save(FILE_UPLOAD_MAX_SIZE, value)
        }

    override fun getSettings(): Map<String, Any> = mapOf(
        REGISTER_ENABLED to registerEnabled,
        IS_INITIALIZED to initialized,
        FILE_UPLOAD_CHUNK_SIZE to fileUploadChunkSize,
        FILE_UPLOAD_MAX_SIZE to fileUploadMaxSize,
    )
}
