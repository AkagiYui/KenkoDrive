package com.akagiyui.common.notifier

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

/**
 * 阿里云短信验证码推送器测试
 * @author AkagiYui
 */

class AliyunSmsCodePusherTests {

    @Test
    @Disabled("需要开通业务")
    fun sendSms() {
        AliyunSmsCodePusher(
            "",
            "",
            "",
            ""
        ).sendSms("", mapOf("code" to "iloveu"))
    }
}
