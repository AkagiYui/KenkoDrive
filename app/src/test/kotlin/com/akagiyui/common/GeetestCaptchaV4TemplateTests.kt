package com.akagiyui.common

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

/**
 * 极验验证码 V4 模板测试类
 * @author AkagiYui
 */

class GeetestCaptchaV4TemplateTests {

    @Test
    @Disabled("需要开通业务")
    fun validate() {
        val geetest = GeetestCaptchaV4Template("", "")
        val response = geetest.validate(
            "",
            "",
            "",
            "",
        )
        println(response)
    }
}
