package com.akagiyui.drive.controller;

import com.akagiyui.drive.entity.User;
import com.akagiyui.drive.entity.request.AddUserRequest;
import com.akagiyui.drive.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 用户 API
 * @author AkagiYui
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    UserService service;

    /**
     * 根据用户id查找用户
     * @param id 用户id
     * @return 用户
     */
    @GetMapping("/{id}")
    User findUserById(@PathVariable("id") String id) {
        return service.findUserById(id);
    }

    /**
     * 新增用户
     * @param user 用户
     * @return 是否成功
     */
    @PostMapping
    boolean addUser(@Validated @RequestBody AddUserRequest user) {
        return service.addUser(user);
    }

    /**
     * 获取所有用户
     * @return 用户列表
     */
    @GetMapping
    Iterable<User> findAll() {
        return service.findAll();
    }
}
