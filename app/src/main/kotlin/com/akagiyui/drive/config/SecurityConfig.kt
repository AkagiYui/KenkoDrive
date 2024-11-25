package com.akagiyui.drive.config

import com.akagiyui.drive.component.TokenAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.*
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.IpAddressMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


/**
 * Spring Security 配置类
 *
 * @author AkagiYui
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val tokenAuthenticationFilter: TokenAuthenticationFilter,
    private val authenticationEntryPoint: AuthenticationEntryPoint,
    private val accessDeniedHandler: AccessDeniedHandler,
) {
    val permitAllGetMapping = arrayOf(
        "/system/version", // 获取系统版本
        "/system/setting/register", // 是否开放注册
        "/file/*/download/**", // 下载文件
        "/captcha", // 获取验证码

        "/share/*", // 获取分享信息

        "/auth/token/temporary/*", // 获取临时登录Token信息
    )
    val permitAllPostMapping = emptyArray<String>()
    val anonymousPostMapping = arrayOf(
        "/auth/token", // 获取 Token
        "/auth/token/password",
        "/auth/token/sms", // 短信登录

        "/auth/otp/email", // 发送邮件注册验证码
        "/auth/otp/sms", // 发送短信注册验证码
        "/auth/register/email", // 确认邮箱注册

        "/auth/token/temporary", // 申请临时登录Token
    )

    /**
     * 配置 Spring Security
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception 异常
     */
    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        val localhostIpMatchers = arrayOf(
            IpAddressMatcher("127.0.0.1"),
            IpAddressMatcher("0:0:0:0:0:0:0:1")
        )

        return http
            .authorizeHttpRequests {
                it // 允许指定路径通过
                    .requestMatchers(HttpMethod.GET, *permitAllGetMapping).permitAll() // 允许匿名 GET 请求访问
                    .requestMatchers(HttpMethod.POST, *permitAllPostMapping).permitAll() // 允许匿名 POST 请求访问
                    .requestMatchers(HttpMethod.POST, *anonymousPostMapping).anonymous() // 仅允许匿名 POST 访问
                    .requestMatchers("/actuator/**").access { _, context ->
                        AuthorizationDecision(localhostIpMatchers.any { matcher ->
                            matcher.matches(context.request.remoteAddr) // 仅允许本地访问
                        })
                    }
                    .anyRequest().authenticated() // 其他请求需要认证
            }
            .csrf { it.disable() } // 关闭 CSRF
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) } // 关闭 Session
            .addFilterBefore(
                tokenAuthenticationFilter,
                UsernamePasswordAuthenticationFilter::class.java
            ) // 添加 Token 过滤器
            .formLogin { it.disable() } // 禁用内置的表单登录
            .logout { it.disable() } // 禁用内置的登出
            .exceptionHandling {
                it
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler)
            }
            .cors(Customizer.withDefaults())
            .build()
    }

    /**
     * 配置跨域
     *
     * @return CorsFilter
     */
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            // 以下配置仅供参考，具体配置根据实际情况修改
            addAllowedOrigin("http://localhost:5173") // 明确指定允许的域名
            addAllowedOriginPattern("https://*.akagiyui.com") // 使用通配符匹配允许的域名
            addAllowedOriginPattern("*") // 使用通配符匹配允许的域名
            addAllowedHeader("*") // 允许所有请求头
            addAllowedMethod("*") // 允许所有请求方法
            allowCredentials = true // 允许携带cookie
        }
        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        }
    }

}
