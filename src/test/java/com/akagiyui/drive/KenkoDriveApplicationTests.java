package com.akagiyui.drive;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class KenkoDriveApplicationTests {

    @Resource
    private MessageSource messageSource;

    @Test
    void contextLoads() {
        assertEquals(1, 2 - 1);
    }

    @Test
    void i18nTest() {
        Locale locale = LocaleContextHolder.getLocale();
        assertEquals("测试", messageSource.getMessage("TEST", new Object[]{}, locale));
    }
}
