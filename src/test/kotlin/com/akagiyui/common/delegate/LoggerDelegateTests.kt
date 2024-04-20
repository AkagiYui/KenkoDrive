package com.akagiyui.common.delegate

import org.junit.jupiter.api.Test

/**
 * Logger 委托测试
 *
 * @author AkagiYui
 */
class LoggerDelegateTests {
    private val logger by LoggerDelegate()

    @Test
    fun testLoggerDelegate() {
        logger.info("test logger delegate")
    }
}
