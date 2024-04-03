package com.akagiyui.drive.config;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        String encoded = passwordEncoder.encode("123456");
        System.out.println(encoded);
        assertNotNull(encoded);
        assertNotEquals("123456", encoded);
    }
}
