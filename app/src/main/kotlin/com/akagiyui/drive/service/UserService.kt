package com.akagiyui.drive.service

import com.akagiyui.drive.entity.Role
import com.akagiyui.drive.entity.User
import com.akagiyui.drive.model.AddUserModel
import com.akagiyui.drive.model.UserFilter
import com.akagiyui.drive.model.request.user.UpdateUserInfoRequest
import org.springframework.data.domain.Page

/**
 * 用户服务接口
 * @author AkagiYui
 */
interface UserService {
    /**
     * 根据ID查找用户
     * @param id 用户ID
     * @return 用户
     */
    fun getUserById(id: String): User

    /**
     * 根据用户名或邮箱或手机号查找用户
     * @param query 用户名或邮箱或手机号
     * @return 用户
     */
    fun getUserByUsernameOrEmailOrPhone(query: String): User?

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户
     */
    fun getUserByPhone(phone: String): User?

    /**
     * 根据ID查找用户
     * @param ids 用户ID
     * @return 用户
     */
    fun getUsersByIds(ids: List<String>): List<User>

    /**
     * 分页查询用户
     * @return 用户列表
     */
    fun getUsers(index: Int, size: Int, userFilter: UserFilter?): Page<User>

    /**
     * 获取所有用户
     * @return 用户列表
     */
    fun getUsers(): List<User>

    /**
     * 新增用户，此处传入所有原始数据，在该方法内进行数据处理
     *
     * @param request 用户
     * @return 用户ID
     */
    fun addUser(request: AddUserModel): User

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    fun deleteUser(id: String)

    /**
     * 用户是否存在
     * @param id 用户ID
     * @return 是否存在
     */
    fun isUserExist(id: String): Boolean

    /**
     * 发送邮箱验证码
     */
    fun registerByEmail(email: String, password: String)

    /**
     * 确认注册
     *
     * @param request 注册确认请求
     */
    fun confirmRegister(email: String, otp: String)

    /**
     * 获取用户权限
     * @return 权限列表
     */
    fun getPermission(user: User): Set<String>

    /**
     * 获取用户角色
     * @return 角色列表
     */
    fun getRole(user: User): Set<String>

    /**
     * 启用/禁用用户
     *
     * @param id       用户 ID
     * @param disabled 是否禁用
     */
    fun disable(id: String, disabled: Boolean)

    /**
     * 重置密码
     *
     * @param id          用户 ID
     * @param newPassword 新密码
     */
    fun resetPassword(id: String, newPassword: String)

    /**
     * 添加角色
     *
     * @param userId 用户ID
     * @param id    角色ID
     */
    fun addRoles(userId: String, id: Set<String>)

    /**
     * 移除角色
     *
     * @param userId 用户ID
     * @param id    角色ID
     */
    fun removeRoles(userId: String, id: Set<String>)

    /**
     * 更新用户信息
     *
     * @param id      用户ID
     * @param userInfo 用户信息
     */
    fun updateInfo(id: String, userInfo: UpdateUserInfoRequest)

    /**
     * 获取用户角色
     *
     * @param id 用户ID
     * @return 角色列表
     */
    fun getRoles(id: String): Set<Role>
}
