package com.akagiyui.drive.repository

import com.akagiyui.drive.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query

/**
 * 用户表操作接口
 * @author AkagiYui
 */
interface UserRepository : JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户
     */
    fun getFirstByUsername(username: String): User

    /**
     * 根据用户名或邮箱查找用户
     * @param text 用户名或邮箱
     * @return 用户
     */
    @Query("select u from User u where u.username = ?1 or u.email = ?1")
    fun getFirstByUsernameOrEmail(text: String): User

    /**
     * 邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    fun existsByEmail(email: String): Boolean

    /**
     * 用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    fun existsByUsername(username: String): Boolean

}
