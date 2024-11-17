package com.akagiyui.drive.component

import com.akagiyui.common.exception.BusinessException
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
        val token = getTokenFromCookie(request) ?: getTokenFromHeader(request)
        token?.let { authenticateUser(it, request) }
        filterChain.doFilter(request, response)
    }

    /**
     * 从请求头中获取Token
     */
    private fun getTokenFromHeader(request: HttpServletRequest): String? {
        val rawToken = request.getHeader("Authorization")
        return if (rawToken.hasText() && rawToken.startsWith("Bearer ")) {
            rawToken.substring(7)
        } else null
    }

    /**
     * 从Cookie中获取Token
     */
    private fun getTokenFromCookie(request: HttpServletRequest): String? {
        val cookie = request.cookies?.find { it.name == "token" }
        return cookie?.value
    }

    private fun authenticateUser(token: String, request: HttpServletRequest) {
        try {
            val userId = tokenTemplate.getUserId(token)
            userId?.let {
                // 将用户信息放入Spring Security上下文
                val userDetails = userService.getUserById(it)
                val authentication = SessionUserAuthentication(
                    userDetails,
                    WebAuthenticationDetailsSource().buildDetails(request)
                )
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (_: TokenVerifyException) {
            // Token验证失败
        } catch (_: BusinessException) {
            // 找不到用户
        }
    }
}
