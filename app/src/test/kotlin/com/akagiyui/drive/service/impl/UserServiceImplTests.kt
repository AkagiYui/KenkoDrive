package com.akagiyui.drive.service.impl

import com.akagiyui.drive.model.request.AddUserRequest
import com.akagiyui.drive.repository.UserRepository
import com.akagiyui.drive.service.UserService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

/**
 * 用户服务 测试类
 * @author AkagiYui
 */
@SpringBootTest
class UserServiceImplTests @Autowired constructor(
    private val userService: UserService,
    private val userRepository: UserRepository,
) {

    @Test
    @Transactional
    fun defaultRoleTest() {
        val addUserRequest = AddUserRequest()
        addUserRequest.username = "test-username"
        addUserRequest.password = "test-password"
        addUserRequest.nickname = "test-nickname"
        addUserRequest.email = "test-email"
        userService.addUser(addUserRequest)

        val user = userRepository.getFirstByUsername("test-username")
        Assertions.assertNotNull(user)
        println(user.id)
        println(user.roles)
        for (role in user.roles) {
            println(role.name)
            Assertions.assertTrue(role.isDefault)
        }
    }

}
