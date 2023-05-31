package com.akagiyui.drive.service;

import com.akagiyui.drive.entity.User;
import com.akagiyui.drive.entity.request.AddUserRequest;

/**
 * 用户服务接口
 * @author AkagiYui
 */
public interface UserService {
    /**
     * 根据id查找用户
     * @param id 用户id
     * @return 用户
     */
    User findUserById(String id);

    /**
     * 用户注册
     * @param user 用户
     * @return 用户
     */
    User register(User user);

    /**
     * 获取所有用户
     * @return 用户列表
     */
    Iterable<User> findAll();

    /**
     * 新增用户
     * @param user 用户
     */
    boolean addUser(AddUserRequest user);
}
