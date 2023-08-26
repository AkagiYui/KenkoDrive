package com.akagiyui.drive.config;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring Security 测试
 *
 * @author AkagiYui
 */
@SpringBootTest
class SecurityConfigTest {

    @Resource
    PasswordEncoder passwordEncoder;

    @Test
    void passwordEncoder() {
        System.out.println(passwordEncoder.encode("123456"));
    }
}
