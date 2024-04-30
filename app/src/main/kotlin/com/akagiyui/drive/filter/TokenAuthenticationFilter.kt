package com.akagiyui.drive.filter

import com.akagiyui.common.utils.hasText
import com.akagiyui.drive.component.TokenUtils
import com.akagiyui.drive.service.UserService
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

/**
 * Token 认证过滤器
 *
 * @author AkagiYui
 */
@Component
class TokenAuthenticationFilter @Autowired constructor(
    private val tokenUtils: TokenUtils,
    private val userService: UserService,
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val rawToken = request.getHeader("Authorization") // 获取 Token
        if (rawToken.hasText() && rawToken.startsWith("Bearer ")) {
            val token = rawToken.substring(7)
            if (tokenUtils.verifyToken(token)) { // 验证 Token
                val userId = tokenUtils.getUserId(token) // 获取用户 ID
                if (userId != null) {
                    val userDetails = userService.getUserDetails(userId) // 从 redis 或数据库中获取用户信息
                    // 放入 Spring Security 上下文
                    val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                    authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        }
        filterChain.doFilter(request, response)
    }
}
