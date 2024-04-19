package com.akagiyui.drive.config

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * Spring Security 测试
 *
 * @author AkagiYui
 */
@SpringBootTest
class SecurityConfigTests @Autowired constructor(
    private val passwordEncoder: PasswordEncoder,
) {

    @Test
    fun passwordEncoder() {
        val encoded = passwordEncoder.encode("123456")
        println(encoded)
        Assertions.assertNotNull(encoded)
        Assertions.assertNotEquals("123456", encoded)
    }

}
