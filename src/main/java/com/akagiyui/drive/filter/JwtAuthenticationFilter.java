package com.akagiyui.drive.filter;


import com.akagiyui.drive.component.JwtUtils;
import com.akagiyui.drive.model.LoginUserDetails;
import com.akagiyui.drive.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器
 *
 * @author AkagiYui
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Resource
    JwtUtils jwtUtils;

    @Resource
    UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        // 获取 Token
        String rawToken = request.getHeader("Authorization");
        if (StringUtils.hasText(rawToken) && rawToken.startsWith("Bearer ")) {
            String token = rawToken.substring(7);
            // 验证 Token
            if (jwtUtils.verifyJwt(token)) {
                // 获取用户 ID
                String userId = jwtUtils.getUserId(token);
                if (userId != null) {
                    // 从 redis 或数据库中获取用户信息
                    LoginUserDetails userDetails = userService.getUserDetails(userId);
                    if (userDetails != null) {
                        // 放入 Spring Security 上下文
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // todo 解释
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}

