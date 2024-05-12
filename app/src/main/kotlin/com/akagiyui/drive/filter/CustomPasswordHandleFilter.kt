package com.akagiyui.drive.filter

import com.akagiyui.common.utils.RequestWrapper
import com.akagiyui.common.utils.hasText
import com.akagiyui.drive.config.SecurityConfig
import com.akagiyui.drive.service.UserService
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

/**
 * 自定义密码处理过滤器
 *
 * @author AkagiYui
 */
@Component
class CustomPasswordHandleFilter @Autowired constructor(
    private val userService: UserService,
) : OncePerRequestFilter() {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val url = request.requestURI
        val method = request.method

        if ((SecurityConfig.LOGIN_URL == url) && (HttpMethod.POST.name() == method)) {
            val rawUsername = request.getParameter("username")
            val rawPassword = request.getParameter("password")

            // 把明文密码加密后放入请求参数中
            // todo RSA 加解密
            if (rawUsername.hasText() && rawPassword.hasText()) {
                val encryptedPassword = userService.encryptPassword(rawUsername, rawPassword, true)
                val requestWrapper = RequestWrapper(request)
                requestWrapper.setParameter("password", encryptedPassword)
                filterChain.doFilter(requestWrapper, response)
                return
            }
        }

        filterChain.doFilter(request, response)
    }
}
