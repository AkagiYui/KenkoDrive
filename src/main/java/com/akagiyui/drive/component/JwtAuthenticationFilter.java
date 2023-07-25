package com.akagiyui.drive.component;


import com.akagiyui.drive.model.LoginUserDetails;
import com.akagiyui.drive.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器
 * @author AkagiYui
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Resource
    JwtUtils jwtUtils;

    @Resource
    UserService userService;

    @Resource
    RedisCache redisCache;

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
                if (userId != null && userService.isExist(userId)) { // todo 性能优化，不需要每次都去数据库查询
                    // 从 redis 中获取用户信息
                    String redisKey = "user:" + userId;
                    LoginUserDetails userDetails = redisCache.get(redisKey);
                    // 在 redis 中没有找到用户信息 todo 从数据库中获取用户信息
                    if (userDetails != null) {
                        // 放入 Spring Security 上下文
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // todo ?
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }

        }
        filterChain.doFilter(request, response);
    }
}

