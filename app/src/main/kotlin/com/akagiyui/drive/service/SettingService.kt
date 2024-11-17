package com.akagiyui.drive.service

/**
 * 设置 服务接口
 *
 * @author AkagiYui
 */
interface SettingService {

    /**
     * 获取设置
     */
    fun getSettings(): Map<String, Any>

    /**
     * 更新设置
     * @param key 设置键
     * @param value 设置值
     */
    fun updateSetting(key: SettingKey, value: String)

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
     * SMTP 服务器地址
     */
    var smtpHost: String

    /**
     * SMTP 服务器端口
     */
    var smtpPort: Int

    /**
     * SMTP 服务器用户名
     */
    var smtpUsername: String

    /**
     * SMTP 服务器密码
     */
    var smtpPassword: String

    /**
     * SMTP 服务器是否启用 SSL
     */
    var smtpSsl: Boolean

    /**
     * 邮件发送者
     */
    var mailFrom: String

    /**
     * 邮件验证码有效时间，单位：分钟，默认：10分钟
     */
    var mailVerifyCodeTimeout: Int

    /**
     * 阿里云短信服务 AccessKeyId
     */
    var aliyunSmsAccessKeyId: String

    /**
     * 阿里云短信服务 AccessKeySecret
     */
    var aliyunSmsAccessKeySecret: String

    /**
     * 阿里云短信服务签名
     */
    var aliyunSmsSignName: String

    /**
     * 阿里云短信服务模板
     */
    var aliyunSmsTemplateCode: String

    /**
     * 阿里云短信服务模板参数
     */
    var aliyunSmsTemplateCodeParam: String
}
