package com.akagiyui.drive.service.impl;

import com.akagiyui.drive.entity.Role;
import com.akagiyui.drive.entity.User;
import com.akagiyui.drive.model.request.AddUserRequest;
import com.akagiyui.drive.repository.UserRepository;
import com.akagiyui.drive.service.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 用户服务 测试类
 * @author AkagiYui
 */
@SpringBootTest
class UserServiceImplTest {

    @Resource
    private UserService userService;

    @Resource
    private UserRepository userRepository;

    @Test
    @Transactional
    void defaultRoleTest() {
        AddUserRequest addUserRequest = new AddUserRequest();
        addUserRequest.setUsername("test-username");
        addUserRequest.setPassword("test-password");
        addUserRequest.setNickname("test-nickname");
        addUserRequest.setEmail("test-email");
        userService.addUser(addUserRequest);

        User user = userRepository.getFirstByUsername("test-username");
        assertNotNull(user);
        System.out.println(user.getId());
        System.out.println(user.getRoles());
        for (Role role : user.getRoles()) {
            System.out.println(role.getName());
            assertTrue(role.getIsDefault());
        }

    }
}
