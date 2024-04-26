package com.akagiyui.drive.service.impl

import com.akagiyui.common.delegate.LoggerDelegate
import com.akagiyui.drive.entity.KeyValueSetting
import com.akagiyui.drive.repository.SettingRepository
import com.akagiyui.drive.service.SettingService
import com.akagiyui.drive.service.SettingService.Companion.FILE_UPLOAD_CHUNK_SIZE
import com.akagiyui.drive.service.SettingService.Companion.FILE_UPLOAD_MAX_SIZE
import com.akagiyui.drive.service.SettingService.Companion.IS_INITIALIZED
import com.akagiyui.drive.service.SettingService.Companion.REGISTER_ENABLED
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Service

/**
 * 设置表操作 实现类
 *
 * @author AkagiYui
 */
@Service
class SettingServiceImpl(
    private val settingRepository: SettingRepository,
    cacheManager: CacheManager,
) : SettingService {
    private val log by LoggerDelegate()
    private val cache = cacheManager.getCache("settings")

    fun <T> getSetting(key: String, defaultValue: T, transform: (String) -> T): T {
        cache?.get(key)?.get()?.let {
            @Suppress("UNCHECKED_CAST")
            return it as T
        }
        val settingValue = settingRepository.findBySettingKey(key)?.settingValue?.let(transform) ?: run {
            log.warn("Setting $key not found, use default value: $defaultValue")
            saveSetting(key, defaultValue)
            defaultValue
        }
        cache?.put(key, settingValue)
        return settingValue
    }

    fun <T> saveSetting(key: String, value: T): T {
        cache?.put(key, value)
        settingRepository.save(KeyValueSetting().apply {
            settingKey = key
            settingValue = value.toString()
        })
        return value
    }

    override var registerEnabled: Boolean
        get() = getSetting(REGISTER_ENABLED, true) { it.toBoolean() }
        set(value) {
            saveSetting(REGISTER_ENABLED, value)
        }

    override var initialized: Boolean
        get() = getSetting(IS_INITIALIZED, false) { it.toBoolean() }
        set(value) {
            saveSetting(IS_INITIALIZED, value)
        }

    override var fileUploadChunkSize: Int
        get() = getSetting(FILE_UPLOAD_CHUNK_SIZE, 5 * 1024 * 1024) { it.toInt() }
        set(value) {
            saveSetting(FILE_UPLOAD_CHUNK_SIZE, value)
        }

    override var fileUploadMaxSize: Long
        get() = getSetting(FILE_UPLOAD_MAX_SIZE, 100L * 1024 * 1024) { it.toLong() }
        set(value) {
            saveSetting(FILE_UPLOAD_MAX_SIZE, value)
        }

    override fun getSettings(): Map<String, Any> = mapOf(
        REGISTER_ENABLED to registerEnabled,
        FILE_UPLOAD_CHUNK_SIZE to fileUploadChunkSize,
        FILE_UPLOAD_MAX_SIZE to fileUploadMaxSize,
    )
}
