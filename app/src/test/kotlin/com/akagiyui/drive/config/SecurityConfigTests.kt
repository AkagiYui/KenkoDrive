package com.akagiyui.drive.config

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Spring Security 测试
 *
 * @author AkagiYui
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTests @Autowired constructor(
    private val passwordEncoder: PasswordEncoder,
    private val mvc: MockMvc,
) {

    @Test
    fun passwordEncoder() {
        val encoded = passwordEncoder.encode("123456")
        println(encoded)
        Assertions.assertNotNull(encoded)
        Assertions.assertNotEquals("123456", encoded)
    }

    @Test
    fun noRouteException() {
        mvc.perform(get("/not-found").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized)
        mvc.perform(get("/share/123").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
    }

}
