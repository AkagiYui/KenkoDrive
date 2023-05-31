package com.akagiyui.drive.service.impl;

import com.akagiyui.drive.component.ResponseEnum;
import com.akagiyui.drive.entity.User;
import com.akagiyui.drive.entity.request.AddUserRequest;
import com.akagiyui.drive.exception.CustomException;
import com.akagiyui.drive.repository.UserRepository;
import com.akagiyui.drive.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


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

    @Override
    public User register(User user) {
        return null;
    }

    @Override
    public Iterable<User> findAll() {
        return repository.findAll();
    }

    @Override
    public boolean addUser(AddUserRequest user) {
        if (repository.existsByUsername(user.getUsername())) {
            throw new CustomException(ResponseEnum.USER_EXIST);
        }
        if (repository.existsByEmail(user.getEmail())) {
            throw new CustomException(ResponseEnum.EMAIL_EXIST);
        }

        if (!StringUtils.hasText(user.getNickname())){
            user.setNickname(user.getUsername());
        }
        repository.save(user.toUser());
        return true;
    }
}
