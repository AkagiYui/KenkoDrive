package com.akagiyui.drive.model

import com.akagiyui.drive.entity.User
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * 登录用户详情
 * @author AkagiYui
 */
class LoginUserDetails() : UserDetails {

    /**
     * 用户
     */
    lateinit var user: User

    /**
     * 权限字符串
     */
    val permissions by lazy {
        // 获取权限，注意如果使用懒加载，需要在事务内获取
        user.roles
            .map { it.permissions }
            .flatten()
            .map { it.name }
            .toSet()
    }

    /**
     * 权限列表
     */
    private val innerAuthorities: MutableSet<out GrantedAuthority> by lazy {
        permissions
            .map { SimpleGrantedAuthority(it) }
            .toMutableSet()
    }

    constructor(user: User) : this() {
        this.user = user
    }

    /**
     * 获取用户权限
     * @return 用户权限集合
     */
    @JsonIgnore
    override fun getAuthorities() = innerAuthorities

    /**
     * 获取用户密码
     * @return 用户密码
     */
    @JsonIgnore
    override fun getPassword(): String = user.password

    /**
     * 获取用户名
     * @return 用户名
     */
    @JsonIgnore
    override fun getUsername(): String = user.username

    /**
     * 用户账号是否未过期
     * @return 用户账号是否未过期
     */
    @JsonIgnore
    override fun isAccountNonExpired() = true

    /**
     * 用户账号是否未被锁定
     * @return 用户账号是否未被锁定
     */
    @JsonIgnore
    override fun isAccountNonLocked() = true

    /**
     * 用户密码是否未过期
     * @return 用户密码是否未过期
     */
    @JsonIgnore
    override fun isCredentialsNonExpired() = true

    /**
     * 用户是否可用
     * @return 用户是否可用
     */
    @JsonIgnore
    override fun isEnabled(): Boolean = !user.disabled

}
