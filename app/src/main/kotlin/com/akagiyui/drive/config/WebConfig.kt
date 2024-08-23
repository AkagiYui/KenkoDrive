package com.akagiyui.drive.config

import com.akagiyui.drive.component.ip.ClientIpArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Web配置
 * @author AkagiYui
 */
@Configuration
class WebConfig : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(ClientIpArgumentResolver()) // 注册客户端IP参数注入解析器
    }
}
