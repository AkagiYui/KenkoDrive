package com.akagiyui.drive.service.impl;

import com.akagiyui.drive.component.ResponseEnum;
import com.akagiyui.drive.entity.User;
import com.akagiyui.drive.entity.filter.UserFilter;
import com.akagiyui.drive.entity.request.AddUserRequest;
import com.akagiyui.drive.exception.CustomException;
import com.akagiyui.drive.repository.UserRepository;
import com.akagiyui.drive.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;


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
        return repository.findById(id).orElseThrow(() -> new CustomException(ResponseEnum.NOT_FOUND));
    }

    @Override
    public User register(User user) {
        return null;
    }

    @Override
    public Page<User> find(int index, int size, UserFilter userFilter) {
        Pageable pageable = PageRequest.of(index, size);
        return repository.findAll(pageable);
    }

    @Override
    public List<User> find() {
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

    @Override
    public boolean delete(String id) {
        if (!repository.existsById(id)) {
            throw new CustomException(ResponseEnum.NOT_FOUND);
        }
        repository.deleteById(id);
        return true;
    }
}
