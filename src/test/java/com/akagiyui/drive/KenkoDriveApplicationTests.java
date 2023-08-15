package com.akagiyui.drive;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

@SpringBootTest
class KenkoDriveApplicationTests {

    @Resource
    private MessageSource messageSource;

    @Test
    void contextLoads() {
    }

    @Test
    void i18nTest() {
        Locale locale = LocaleContextHolder.getLocale();
        System.out.println(messageSource.getMessage("TEST", new Object[]{}, locale));
    }
}
