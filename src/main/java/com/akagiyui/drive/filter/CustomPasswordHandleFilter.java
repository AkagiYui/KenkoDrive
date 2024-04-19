package com.akagiyui.drive.filter;

import com.akagiyui.common.utils.RequestWrapper;
import com.akagiyui.drive.config.SecurityConfig;
import com.akagiyui.drive.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

/**
 * 自定义密码处理过滤器
 *
 * @author AkagiYui
 */
@Component
public class CustomPasswordHandleFilter extends OncePerRequestFilter {
    @Resource
    UserService userService;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String url = request.getRequestURI();
        String method = request.getMethod();

        if (Objects.equals(SecurityConfig.LOGIN_URL, url) && "POST".equals(method)) {
            String rawUsername = request.getParameter("username");
            String rawPassword = request.getParameter("password");

            // 把明文密码加密后放入请求参数中
            // todo RSA 加解密
            if (StringUtils.hasText(rawUsername) && StringUtils.hasText(rawPassword)) {
                String encryptedPassword = userService.encryptPassword(rawUsername, rawPassword, true);
                RequestWrapper requestWrapper = new RequestWrapper(request);
                requestWrapper.setParameter("password", encryptedPassword);
                filterChain.doFilter(requestWrapper, response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
