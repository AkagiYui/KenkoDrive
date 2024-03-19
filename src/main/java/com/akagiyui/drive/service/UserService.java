package com.akagiyui.drive.service;

import com.akagiyui.drive.entity.User;
import com.akagiyui.drive.model.LoginUserDetails;
import com.akagiyui.drive.model.filter.UserFilter;
import com.akagiyui.drive.model.request.AddUserRequest;
import com.akagiyui.drive.model.request.EmailVerifyCodeRequest;
import com.akagiyui.drive.model.request.RegisterConfirmRequest;
import com.akagiyui.drive.model.request.UpdateUserInfoRequest;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Set;

/**
 * 用户服务接口
 * @author AkagiYui
 */
public interface UserService extends UserDetailsService {
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
     *
     * @param user 用户
     */
    void addUser(AddUserRequest user);

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    void delete(String id);

    /**
     * 用户是否存在
     * @param id 用户ID
     * @return 是否存在
     */
    boolean isExist(String id);

    /**
     * 从 Security 获取当前用户
     * @return 用户
     */
    User getUser();

    /**
     * 从 redis 或数据库获取当前用户，如果 redis 中不存在则从数据库中获取，并将用户信息存入 redis
     * @param userId 用户ID
     * @return 用户
     */
    LoginUserDetails getUserDetails(String userId);

    /**
     * 发送邮箱验证码
     */
    void sendEmailVerifyCode(EmailVerifyCodeRequest verifyRequest);

    /**
     * 确认注册
     *
     * @param registerConfirmRequest 注册确认请求
     */
    void confirmRegister(RegisterConfirmRequest registerConfirmRequest);

    /**
     * 加密密码
     * @param username 用户名
     * @param password 密码明文
     * @return 密码密文
     */
    String encryptPassword(String username, String password);

    /**
     * 加密密码
     * @param username 用户名
     * @param password 密码明文
     * @param raw 是否不通过加密器加密
     * @return 密码密文
     */
    String encryptPassword(String username, String password, boolean raw);

    /**
     * 更新用户信息
     *
     * @param userInfo 用户信息
     */
    void updateInfo(UpdateUserInfoRequest userInfo);

    /**
     * 获取用户权限
     * @return 权限列表
     */
    Set<String> getPermission();

    /**
     * 获取用户角色
     * @return 角色列表
     */
    Set<String> getRole();

    /**
     * 启用/禁用用户
     *
     * @param id       用户 ID
     * @param disabled 是否禁用
     */
    void disable(String id, boolean disabled);

    /**
     * 重置密码
     *
     * @param id          用户 ID
     * @param newPassword 新密码
     */
    void resetPassword(String id, String newPassword);
}
