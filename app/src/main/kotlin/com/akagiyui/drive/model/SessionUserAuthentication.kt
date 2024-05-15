package com.akagiyui.drive.model

import com.akagiyui.drive.entity.User
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.web.authentication.WebAuthenticationDetails


/**
 * 当前 Session 用户认证
 * @author AkagiYui
 */

class SessionUserAuthentication(
    val user: User,
    private val details: WebAuthenticationDetails,
) : Authentication {

    private val userAuthorities by lazy {
        user.roles
            .asSequence()
            .map { it.permissions }
            .flatten()
            .map { GrantedAuthority { it.name } }
            .toMutableList()
    }

    override fun getName(): String {
        return user.id
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return userAuthorities
    }

    override fun getCredentials(): Any {
        TODO("Not yet implemented")
    }

    override fun getDetails(): Any {
        return details
    }

    override fun getPrincipal(): Any {
        return user
    }

    override fun isAuthenticated(): Boolean {
        return true
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {}
}
