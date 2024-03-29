package com.akagiyui.drive.config;


import com.akagiyui.common.ResponseEnum;
import com.akagiyui.common.ResponseResult;
import com.akagiyui.drive.component.JwtUtils;
import com.akagiyui.drive.component.RequestMatcherBuilder;
import com.akagiyui.drive.entity.User;
import com.akagiyui.drive.filter.CustomPasswordHandleFilter;
import com.akagiyui.drive.filter.JwtAuthenticationFilter;
import com.akagiyui.drive.model.LoginUserDetails;
import com.akagiyui.drive.model.response.LoginResponse;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Security 配置类
 *
 * @author AkagiYui
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Resource
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Resource
    CustomPasswordHandleFilter customPasswordHandleFilter;

    @Resource
    AuthenticationEntryPoint authenticationEntryPoint;

    @Resource
    AccessDeniedHandler accessDeniedHandler;

    @Resource
    JwtUtils jwtUtils;

    public static final String LOGIN_URL = "/user/token";

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置 Spring Security
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception 异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, RequestMatcherBuilder mvc) throws Exception {
        return http
                .authorizeHttpRequests(a -> a
                        // 允许指定路径通过
                        .requestMatchers(mvc.matchers("/server/version")).permitAll()
                        .requestMatchers(mvc.matchers("/info/**")).permitAll()
                        .requestMatchers(mvc.matchers("/user/register/**")).permitAll()
                        .anyRequest().authenticated() // 其他请求需要认证
                )
                // 关闭 CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // 关闭 Session
                .sessionManagement(s -> s
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // 添加 JWT 过滤器
                .addFilterBefore(customPasswordHandleFilter, UsernamePasswordAuthenticationFilter.class) // 添加密码处理过滤器
                .formLogin(conf -> conf
                        .loginProcessingUrl(LOGIN_URL)
                        .successHandler(this::onAuthenticationSuccess)
                        .failureHandler(this::onAuthenticationFailure)
                        .permitAll()
                )
                .exceptionHandling(conf -> conf
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .cors(Customizer.withDefaults())
                .build();
    }

    /**
     * 配置跨域
     *
     * @return CorsFilter
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("https://drive.akagiyui.com"); // 允许指定域名通过
        configuration.addAllowedOriginPattern("*"); // 允许所有域名通过
        configuration.addAllowedHeader("*"); // 允许所有请求头通过
        configuration.addAllowedMethod("*"); // 允许所有请求方法通过
        configuration.setAllowCredentials(true); // 允许跨域携带cookie

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * 认证失败处理
     */
    private void onAuthenticationFailure(
            HttpServletRequest httpServletRequest,
            HttpServletResponse response,
            AuthenticationException e
    ) {
        ResponseResult.writeResponse(response, HttpStatus.UNAUTHORIZED, ResponseEnum.UNAUTHORIZED);
    }

    /**
     * 认证成功处理
     */
    private void onAuthenticationSuccess(
            HttpServletRequest httpServletRequest,
            HttpServletResponse response,
            Authentication authentication
    ) {
        LoginUserDetails loginUserDetails = (LoginUserDetails)authentication.getPrincipal();

        User user = loginUserDetails.getUser();
        String jwt = jwtUtils.createJwt(user);
        LoginResponse loginResponse = new LoginResponse(jwt, null);
        ResponseResult.writeResponse(response, HttpStatus.OK, loginResponse);
    }

}
