package com.akagiyui.drive.filter

import com.akagiyui.common.exception.CustomException
import com.akagiyui.common.token.TokenTemplate
import com.akagiyui.common.token.TokenVerifyException
import com.akagiyui.common.utils.hasText
import com.akagiyui.drive.model.SessionUserAuthentication
import com.akagiyui.drive.service.UserService
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
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
    private val tokenTemplate: TokenTemplate,
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
            try {
                val userId = tokenTemplate.getUserId(token) // 获取用户 ID
                if (userId != null) {
                    // 将用户信息放入 Spring Security 上下文
                    val authentication = SessionUserAuthentication(
                        userService.findUserById(userId),
                        WebAuthenticationDetailsSource().buildDetails(request)
                    )
                    SecurityContextHolder.getContext().authentication = authentication
                }
            } catch (_: TokenVerifyException) {
                // Token 验证失败
            } catch (_: CustomException) {
                // 找不到用户
            }
        }
        filterChain.doFilter(request, response)
    }
}
