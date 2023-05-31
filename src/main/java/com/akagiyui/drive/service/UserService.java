package com.akagiyui.drive.service;

import com.akagiyui.drive.entity.User;

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
}
