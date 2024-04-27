package com.akagiyui.drive.service

/**
 * 系统设置键枚举类
 *
 * @param transform 转换函数
 */
enum class SettingKey(val transform: (String) -> Any) {
    REGISTER_ENABLED({ it.toBoolean() }),
    IS_INITIALIZED({ it.toBoolean() }),
    FILE_UPLOAD_CHUNK_SIZE({ it.toInt() }),
    FILE_UPLOAD_MAX_SIZE({ it.toLong() }),
    SMTP_HOST({ it }),
    SMTP_PORT({ it.toInt() }),
    SMTP_USERNAME({ it }),
    SMTP_PASSWORD({ it }),
    SMTP_SSL({ it.toBoolean() }),
    MAIL_FROM({ it }),
    MAIL_VERIFY_CODE_TIMEOUT({ it.toInt() })
}
