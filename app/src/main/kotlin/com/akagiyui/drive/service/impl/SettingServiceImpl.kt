package com.akagiyui.drive.service.impl

import com.akagiyui.common.delegate.LoggerDelegate
import com.akagiyui.common.utils.toCamelCase
import com.akagiyui.drive.entity.KeyValueSetting
import com.akagiyui.drive.repository.SettingRepository
import com.akagiyui.drive.service.SettingKey
import com.akagiyui.drive.service.SettingService
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

    fun <T> getSetting(key: SettingKey, defaultValue: T): T {
        cache?.get(key)?.get()?.let {
            @Suppress("UNCHECKED_CAST")
            return it as T
        }
        val settingValue = settingRepository.findBySettingKey(key.toString())?.settingValue?.let(key.transform) ?: run {
            log.warn("Setting $key not found, use default value: $defaultValue")
            saveSetting(key, defaultValue)
            defaultValue
        }
        settingValue as T
        cache?.put(key, settingValue)
        return settingValue
    }

    fun <T> saveSetting(key: SettingKey, value: T): T {
        cache?.put(key, value)
        settingRepository.save(KeyValueSetting().apply {
            settingKey = key.toString()
            settingValue = value.toString()
        })
        return value
    }

    override fun updateSetting(key: SettingKey, value: String) {
        saveSetting(key, key.transform(value))
    }

    override var registerEnabled: Boolean
        get() = getSetting(SettingKey.REGISTER_ENABLED, true)
        set(value) {
            saveSetting(SettingKey.REGISTER_ENABLED, value)
        }

    override var initialized: Boolean
        get() = getSetting(SettingKey.IS_INITIALIZED, false)
        set(value) {
            saveSetting(SettingKey.IS_INITIALIZED, value)
        }

    override var fileUploadChunkSize: Int
        get() = getSetting(SettingKey.FILE_UPLOAD_CHUNK_SIZE, 5 * 1024 * 1024)
        set(value) {
            saveSetting(SettingKey.FILE_UPLOAD_CHUNK_SIZE, value)
        }

    override var fileUploadMaxSize: Long
        get() = getSetting(SettingKey.FILE_UPLOAD_MAX_SIZE, 100L * 1024 * 1024)
        set(value) {
            saveSetting(SettingKey.FILE_UPLOAD_MAX_SIZE, value)
        }

    override var smtpHost: String
        get() = getSetting(SettingKey.SMTP_HOST, "smtp.example.com")
        set(value) {
            saveSetting(SettingKey.SMTP_HOST, value)
        }

    override var smtpPort: Int
        get() = getSetting(SettingKey.SMTP_PORT, 465)
        set(value) {
            saveSetting(SettingKey.SMTP_PORT, value)
        }

    override var smtpUsername: String
        get() = getSetting(SettingKey.SMTP_USERNAME, "username")
        set(value) {
            saveSetting(SettingKey.SMTP_USERNAME, value)
        }

    override var smtpPassword: String
        get() = getSetting(SettingKey.SMTP_PASSWORD, "password")
        set(value) {
            saveSetting(SettingKey.SMTP_PASSWORD, value)
        }

    override var smtpSsl: Boolean
        get() = getSetting(SettingKey.SMTP_SSL, true)
        set(value) {
            saveSetting(SettingKey.SMTP_SSL, value)
        }

    override var mailFrom: String
        get() = getSetting(SettingKey.MAIL_FROM, "address")
        set(value) {
            saveSetting(SettingKey.MAIL_FROM, value)
        }

    override var mailVerifyCodeTimeout: Int
        get() = getSetting(SettingKey.MAIL_VERIFY_CODE_TIMEOUT, 10)
        set(value) {
            saveSetting(SettingKey.MAIL_VERIFY_CODE_TIMEOUT, value)
        }

    override var aliyunSmsAccessKeyId: String
        get() = getSetting(SettingKey.ALIYUN_SMS_ACCESS_KEY_ID, "")
        set(value) {
            saveSetting(SettingKey.ALIYUN_SMS_ACCESS_KEY_ID, value)
        }

    override var aliyunSmsAccessKeySecret: String
        get() = getSetting(SettingKey.ALIYUN_SMS_ACCESS_KEY_SECRET, "")
        set(value) {
            saveSetting(SettingKey.ALIYUN_SMS_ACCESS_KEY_SECRET, value)
        }

    override var aliyunSmsSignName: String
        get() = getSetting(SettingKey.ALIYUN_SMS_SIGN_NAME, "")
        set(value) {
            saveSetting(SettingKey.ALIYUN_SMS_SIGN_NAME, value)
        }

    override var aliyunSmsTemplateCode: String
        get() = getSetting(SettingKey.ALIYUN_SMS_TEMPLATE_CODE, "")
        set(value) {
            saveSetting(SettingKey.ALIYUN_SMS_TEMPLATE_CODE, value)
        }

    override fun getSettings(): Map<String, Any> {
        val rawMap = mutableMapOf(
            SettingKey.REGISTER_ENABLED to registerEnabled,
            SettingKey.FILE_UPLOAD_CHUNK_SIZE to fileUploadChunkSize,
            SettingKey.FILE_UPLOAD_MAX_SIZE to fileUploadMaxSize,
            SettingKey.SMTP_HOST to smtpHost,
            SettingKey.SMTP_PORT to smtpPort,
            SettingKey.SMTP_USERNAME to smtpUsername,
            SettingKey.SMTP_PASSWORD to smtpPassword,
            SettingKey.SMTP_SSL to smtpSsl,
            SettingKey.MAIL_FROM to mailFrom,
            SettingKey.MAIL_VERIFY_CODE_TIMEOUT to mailVerifyCodeTimeout,
            SettingKey.ALIYUN_SMS_ACCESS_KEY_ID to aliyunSmsAccessKeyId,
            SettingKey.ALIYUN_SMS_ACCESS_KEY_SECRET to aliyunSmsAccessKeySecret,
            SettingKey.ALIYUN_SMS_SIGN_NAME to aliyunSmsSignName,
            SettingKey.ALIYUN_SMS_TEMPLATE_CODE to aliyunSmsTemplateCode,
        )
        return mutableMapOf<String, Any>().apply {
            rawMap.forEach { (key, value) ->
                this[key.toString().toCamelCase()] = value
            }
        }
    }
}
