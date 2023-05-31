package com.akagiyui.drive.controller;

import com.akagiyui.drive.entity.User;
import com.akagiyui.drive.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
}
