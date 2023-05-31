package com.akagiyui.drive.service.impl;

import com.akagiyui.drive.entity.User;
import com.akagiyui.drive.repository.UserRepository;
import com.akagiyui.drive.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


/**
 * 用户服务实现类
 * @author AkagiYui
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    UserRepository repository;

    @Override
    public User findUserById(String id) {
        return repository.findById(id).orElse(null);
    }
}
