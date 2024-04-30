package com.akagiyui.common.delegate

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

/**
 * Logger 委托测试
 *
 * @author AkagiYui
 */
class LoggerDelegateTests {
    private val logger by LoggerDelegate()
    private val logger2 = LoggerFactory.getLogger(LoggerDelegateTests::class.java)
    private val logger3 by LoggerDelegate()

    @Test
    fun testLoggerDelegate() {
        logger.info("test logger delegate")
    }

    @Test
    fun testLogger() {
        Assertions.assertSame(logger, logger2)
        Assertions.assertSame(logger, logger3)
    }
}
