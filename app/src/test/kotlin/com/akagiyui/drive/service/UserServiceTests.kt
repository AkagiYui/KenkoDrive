package com.akagiyui.drive.service

import com.akagiyui.drive.model.UserFilter
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/**
 * 用户服务测试
 * @author AkagiYui
 */
@SpringBootTest
class UserServiceTests @Autowired constructor(
    private val userService: UserService,
) {

    /**
     * 缓存分页条件查询用户
     */
    @Test
    fun cachePageTest() {
        val filter = UserFilter().apply {
            expression = "123"
        }
        val users = userService.find(1, 10, filter)
        Assertions.assertNotNull(users)
    }

}
