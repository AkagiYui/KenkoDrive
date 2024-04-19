package com.akagiyui.drive.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus

/**
 * SystemController 测试类
 *
 * @author AkagiYui
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SystemControllerTests @Autowired constructor(
    val restTemplate: TestRestTemplate,
) {
    @BeforeAll
    fun setup() {
        println(">> Setup")
    }

    @Test
    fun `Assert system version, content and status code`() {
        println(">> Assert system version, content and status code")
        val entity = restTemplate.getForEntity<String>("/system/version")
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).contains("\"code\":10000")
    }

    @AfterAll
    fun teardown() {
        println(">> Tear down")
    }
}
