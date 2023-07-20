package com.akagiyui.drive.service;

import com.akagiyui.drive.entity.User;
import com.akagiyui.drive.entity.filter.UserFilter;
import com.akagiyui.drive.entity.request.AddUserRequest;
import org.springframework.data.domain.Page;

import java.util.List;

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
     * 分页查询用户
     * @return 用户列表
     */
    Page<User> find(int index, int size, UserFilter userFilter);

    /**
     * 获取所有用户
     * @return 用户列表
     */
    List<User> find();

    /**
     * 新增用户
     * @param user 用户
     */
    boolean addUser(AddUserRequest user);

    /**
     * 删除用户
     * @param id 用户ID
     * @return 删除结果
     */
    boolean delete(String id);
}
