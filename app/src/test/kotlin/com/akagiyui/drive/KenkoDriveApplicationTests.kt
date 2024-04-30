package com.akagiyui.drive

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import java.util.*

/**
 * @author AkagiYui
 */
@SpringBootTest
class KenkoDriveApplicationTests @Autowired constructor(
    private val messageSource: MessageSource,
) {
    @Test
    fun contextLoads() {
        Assertions.assertEquals(1, 2 - 1)
    }

    @Test
    fun i18nTest() {
        val locale = LocaleContextHolder.getLocale()
        println("locale: $locale")
        if (locale == Locale.SIMPLIFIED_CHINESE) {
            Assertions.assertEquals("测试", messageSource.getMessage("TEST", arrayOf(), locale))
        } else {
            Assertions.assertEquals("test", messageSource.getMessage("TEST", arrayOf(), locale))
        }
    }
}
