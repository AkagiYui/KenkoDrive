package com.akagiyui.drive.config

import com.akagiyui.common.ResponseEnum
import com.akagiyui.common.ResponseResult
import com.akagiyui.common.token.TokenTemplate
import com.akagiyui.drive.component.RequestMatcherBuilder
import com.akagiyui.drive.filter.CustomPasswordHandleFilter
import com.akagiyui.drive.filter.TokenAuthenticationFilter
import com.akagiyui.drive.model.LoginUserDetails
import com.akagiyui.drive.model.response.LoginResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.*
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
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
    private val customPasswordHandleFilter: CustomPasswordHandleFilter,
    private val authenticationEntryPoint: AuthenticationEntryPoint,
    private val accessDeniedHandler: AccessDeniedHandler,
    private val tokenTemplate: TokenTemplate,
) {
    companion object {
        const val LOGIN_URL: String = "/user/token"
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    /**
     * 配置 Spring Security
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception 异常
     */
    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity, mvc: RequestMatcherBuilder): SecurityFilterChain {
        return http
            .authorizeHttpRequests {
                it // 允许指定路径通过
                    .requestMatchers(HttpMethod.GET, "/system/version").permitAll()
                    .requestMatchers(HttpMethod.GET, "/system/setting/register").permitAll()
                    .requestMatchers(HttpMethod.GET, "/file/*/download/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/captcha/**").permitAll()
                    .requestMatchers(*mvc.matchers("/user/register/**")).permitAll()
                    .requestMatchers(*mvc.matchers("/sse")).permitAll()
                    .anyRequest().authenticated() // 其他请求需要认证
            }

            // 关闭 CSRF
            .csrf { it.disable() }

            // 关闭 Session
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java) // 添加 JWT 过滤器
//            .addFilterBefore(customPasswordHandleFilter, UsernamePasswordAuthenticationFilter::class.java) // 添加密码处理过滤器
            .formLogin {
                it
                    .loginProcessingUrl(LOGIN_URL)
                    .successHandler(this::onAuthenticationSuccess)
                    .failureHandler(this::onAuthenticationFailure)
                    .permitAll()
            }
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
            addAllowedOrigin("https://drive.akagiyui.com") // 允许指定域名通过
            addAllowedOriginPattern("*") // 允许所有域名通过
            addAllowedHeader("*") // 允许所有请求头通过
            addAllowedMethod("*") // 允许所有请求方法通过
            allowCredentials = true // 允许跨域携带cookie
        }
        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        }
    }

    /**
     * 认证失败处理
     */
    @Suppress("UNUSED_PARAMETER")
    private fun onAuthenticationFailure(
        httpServletRequest: HttpServletRequest,
        response: HttpServletResponse,
        e: AuthenticationException,
    ) {
        ResponseResult.writeResponse(response, HttpStatus.UNAUTHORIZED, ResponseEnum.UNAUTHORIZED)
    }

    /**
     * 认证成功处理
     */
    @Suppress("UNUSED_PARAMETER")
    private fun onAuthenticationSuccess(
        httpServletRequest: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication,
    ) {
        val loginUserDetails = authentication.principal as LoginUserDetails

        val user = loginUserDetails.user
        val token = tokenTemplate.createToken(user.id)
        val loginResponse = LoginResponse(token, null)
        ResponseResult.writeResponse(response, HttpStatus.OK, loginResponse)
    }
}
